package com.loan.services;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.loan.exception.LoanException;
import com.loan.models.Loan;
import com.loan.models.LoanBalanceResponse;
import com.loan.models.LoanRequest;
import com.loan.models.LoanResponse;
import com.loan.models.Payment;
import com.loan.models.PaymentRequest;
import com.loan.models.PaymentResponse;
import com.loan.repositories.LoanRepository;
import com.loan.repositories.PaymentRepository;

@Service
public class LoanService {

	@Autowired
	private LoanRepository loanRepository;
	@Autowired
	private PaymentRepository paymentRepository;

	private static final Logger LOGGER = Logger.getLogger(LoanService.class.getSimpleName());

	private final String DATE_FORMATE = "yyyy-MM-dd";
	private final int DAYS_PER_YEAR = 365;
	private final int LOAN_TERM_MONTHS = 12;

	/**
	 * Add Loan
	 *
	 * @param loanReq LoanRequest with initialAmount, interestRate, startDate
	 * @return response TaxResponse
	 * @throws LoanException
	 */
	public LoanResponse addLoan(LoanRequest taxReq) throws LoanException {

		Date startDate = getDateFromString(taxReq.getStartDate());
		Loan loan = new Loan();
		loan.setInitialAmount(taxReq.getInitialAmount());
		loan.setInterestRate(taxReq.getInterestRate());
		loan.setStartDate(startDate);
		// loan term is one year
		loan.setEndDate(calculateLoanEndDate(startDate));
		loan.setStatus(Boolean.TRUE);
		loanRepository.save(loan);

		LoanResponse response = new LoanResponse();
		response.setLoanId(loan.getId());

		return response;
	}

	/**
	 * Make Payment
	 *
	 * @param loanReq PaymentRequest with payment, loanId, installment date
	 * @return response TaxResponse
	 * @throws LoanException
	 */
	public PaymentResponse makePayment(PaymentRequest payReq) throws LoanException {

		Loan loan = loanRepository.findById(new Long(payReq.getLoanId())).orElse(null);

		if (loan == null) {
			throw new LoanException("Invalid Loan ID");
		}

		// since assumption is to make monthly payment, we can add a validation to restrict one payment per month
		Payment pay = new Payment();
		pay.setAmount(payReq.getPayment());
		pay.setLoan(loan);
		pay.setPaymentDate(getDateFromString(payReq.getPaymentDate()));
		paymentRepository.save(pay);

		PaymentResponse response = new PaymentResponse();
		response.setLoanId(loan.getId());
		response.setPaymentId(pay.getPaymentId());

		return response;
	}

	/**
	 * Get Balance
	 *
	 * @param loanId loan ID
	 * @param date   balance to date
	 * @return response LoanBalanceResponse
	 * @throws LoanException
	 */
	public LoanBalanceResponse getBalance(Long loanId, String date) throws LoanException {

		LocalDateTime requestedDate = convertToLocalDateTimeViaMilisecond(getDateFromString(date));

		Loan loan = loanRepository.findById(loanId).orElse(null);

		if (loan == null) {
			throw new LoanException("Invalid Loan ID");
		}

		Boolean requestedDateInSideLoanPeriod = (!requestedDate
				.isBefore(convertToLocalDateTimeViaMilisecond(loan.getStartDate())))
				&& (requestedDate.isBefore(convertToLocalDateTimeViaMilisecond(loan.getEndDate())));

		if (!requestedDateInSideLoanPeriod) {
			throw new LoanException("Date is not falling in to the loan periord");
		}

		// Get payments for the loans order by payment date
		// So payment order will be arrange for the payment for the month
		List<Payment> payments = paymentRepository.findByLoanIdOrderByPaymentDateAsc(loanId);

		// get principle Balance for the loan
		BigDecimal principleBalance = getPrincipleBalance(loan.getInitialAmount(), payments, loan);

		BigDecimal amount = getRemainingBalance(principleBalance, loan, requestedDate);

		LoanBalanceResponse response = new LoanBalanceResponse();
		response.setInterestRate(loan.getInterestRate());
		response.setLoanInitialAmount(loan.getInitialAmount());
		response.setStartDate(loan.getStartDate());
		response.setTotalBalanceToDate(amount);

		return response;
	}

	/**
	 * Get Remaining balance Reducing Interest Rate
	 *
	 * Interest payable (each installment) = Outstanding loan amount x interest rate
	 * 
	 * @param principleBalance current principal balance is the amount still owed on
	 *                         the original amount financed without any interest or
	 *                         finance charges that are due.
	 * @param loan             Loan object
	 * @return remainingBalance remaining balance for requested date
	 * @throws LoanException
	 */
	public BigDecimal getRemainingBalance(BigDecimal principleBalance, Loan loan, LocalDateTime requestedDate) {

		LocalDateTime loanEndDate = convertToLocalDateTimeViaMilisecond(loan.getEndDate());

		LocalDateTime tempDateTime = LocalDateTime.from(requestedDate);
		// how many days to the loan end date
		long days = tempDateTime.until(loanEndDate, ChronoUnit.DAYS);

		// The interest added for a day is defined as: annual interest rate / 100 / 365
		// Reducing Interest Rate
		BigDecimal interestPerDay = principleBalance
				.multiply(new BigDecimal(((loan.getInterestRate() / 100) / DAYS_PER_YEAR), MathContext.DECIMAL64));
		BigDecimal interestForRemaingDays = interestPerDay.multiply(new BigDecimal(days));
		BigDecimal remainingBalance = interestForRemaingDays.add(principleBalance);
		remainingBalance = remainingBalance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		return remainingBalance;
	}

	/**
	 * Get Principle balance 
	 * Calculation: Reducing balance method
	 * This method will calculate totalInterestPaid and totalPrinciplePaid for each payment
	 * and it will subtract from initial amount to find amount still owed
	 * @param payments         payments for the loan
	 * @param loan             loan object
	 * @return principleBalance
	 */
	public BigDecimal getPrincipleBalance(BigDecimal loanInitialAmount, List<Payment> payments, Loan loan) {

		BigDecimal totalInterestPaid = new BigDecimal(0);
		BigDecimal totalPrinciplePaid = new BigDecimal(0);

		BigDecimal loanBalance = loan.getInitialAmount();

		for (Payment payment : payments) {

			double interestRate = (loan.getInterestRate() / 100) / LOAN_TERM_MONTHS;

			// interest paid for each month = loan_balance x interest% / 12
			BigDecimal interestAmount = loanBalance.multiply(BigDecimal.valueOf(interestRate));
			totalInterestPaid = totalInterestPaid.add(interestAmount);

			// principle paid for each month = paidAmounnt - interestAmount
			BigDecimal pricipleAmount = payment.getAmount().subtract(interestAmount);
			totalPrinciplePaid = totalPrinciplePaid.add(pricipleAmount);

			// assign loan balance after minus from principle amount
			loanBalance = loanBalance.subtract(pricipleAmount);
		}
		return loanInitialAmount.subtract(totalPrinciplePaid);

	}

	/**
	 * Convert String date to Date format
	 *
	 * @param dateStr String Date
	 * @return Date date object
	 * @throws LoanException
	 */
	public Date getDateFromString(String dateStr) throws LoanException {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMATE, Locale.ENGLISH);
		String dateInString = dateStr;
		try {
			return formatter.parse(dateInString);
		} catch (ParseException e) {
			LOGGER.log(Level.WARNING, "Invalid Date Format");
			throw new LoanException("Invalid Date Format");
		}
	}

	public LocalDateTime convertToLocalDateTimeViaMilisecond(Date dateToConvert) {
		return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * Calculate Loan End Date
	 *
	 * @param startDate String Date
	 * @return Date loan end date
	 * @throws LoanException
	 */
	private Date calculateLoanEndDate(Date startDate) throws LoanException {
		LocalDateTime endDate = convertToLocalDateTimeViaMilisecond(startDate).plusMonths(LOAN_TERM_MONTHS);
		Date loanEndDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
		return loanEndDate;
	}

}
