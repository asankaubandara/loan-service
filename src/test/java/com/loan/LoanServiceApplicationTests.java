package com.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import com.loan.exception.LoanException;
import com.loan.models.Loan;
import com.loan.models.Payment;
import com.loan.services.LoanService;

@SuppressWarnings("unused")
@SpringBootTest()
@AutoConfigureMockMvc
class LoanServiceApplicationTests {

	@Autowired
	@InjectMocks
	private LoanService loanService;
	
	Loan loan;
	List<Payment> paymentList;
	
	@BeforeEach
	public void init() throws LoanException {
		// generate test data
		loan = generateLoan();
		paymentList = generatePayments(loan);
    }

	@Test
	public void testRemainingBalance() throws LoanException {

		LocalDateTime requestedDate = loanService
				.convertToLocalDateTimeViaMilisecond(loanService.getDateFromString("2022-03-05"));

		BigDecimal principleBalance = new BigDecimal(500);
		assertEquals(BigDecimal.valueOf(528.77),
				loanService.getRemainingBalance(principleBalance, loan, requestedDate));
	}

	@Test
	public void testAlreadyPaidLoan() throws LoanException {

		LocalDateTime requestedDate = loanService
				.convertToLocalDateTimeViaMilisecond(loanService.getDateFromString("2022-03-05"));
		BigDecimal principleBalance = new BigDecimal(0.00);
		assertEquals(principleBalance.doubleValue(),
				loanService.getRemainingBalance(principleBalance, loan, requestedDate).doubleValue());
	}
	
	@Test
	public void testPrincipleAmount() throws LoanException{
				
		BigDecimal principleAmount = loanService.getPrincipleBalance(loan.getInitialAmount(), paymentList, loan);
		principleAmount = principleAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		
		assertEquals(BigDecimal.valueOf(759256.91), principleAmount);
	}
	
	@Test
	public void testPrincipleAmountWithoutPayment() throws LoanException{
		
		List<Payment> payments = new ArrayList<Payment>();
				
		BigDecimal principleAmount = loanService.getPrincipleBalance(loan.getInitialAmount(), payments, loan);
		principleAmount = principleAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		
		assertEquals(BigDecimal.valueOf(1000000.00).doubleValue(), principleAmount.doubleValue());
	}

	@Test
	public void testInvalidDate() throws LoanException {
		assertThrows(LoanException.class, () -> loanService.getBalance(9L, "2020-03-05"));
	}
	
	public Loan generateLoan() throws LoanException {
		Loan loan = new Loan();
		loan.setInitialAmount(new BigDecimal(1000000));
		loan.setInterestRate(10.0);
		loan.setStatus(Boolean.TRUE);
		loan.setStartDate(loanService.getDateFromString("2021-10-01"));
		loan.setEndDate(loanService.getDateFromString("2022-10-01"));
		return loan;
	}
	
	public List<Payment> generatePayments(Loan loan) throws LoanException{
		List<Payment> payments = new ArrayList<Payment>();
		
		Payment pay1 = new Payment();
		pay1.setAmount(new BigDecimal(87916));
		pay1.setLoan(loan);
		pay1.setPaymentDate(loanService.getDateFromString("2021-10-01"));
		payments.add(pay1);
		
		Payment pay2 = new Payment();
		pay2.setAmount(new BigDecimal(87916));
		pay2.setLoan(loan);
		pay2.setPaymentDate(loanService.getDateFromString("2021-11-01"));
		payments.add(pay2);
		
		Payment pay3 = new Payment();
		pay3.setAmount(new BigDecimal(87916));
		pay3.setLoan(loan);
		pay3.setPaymentDate(loanService.getDateFromString("2021-12-01"));
		payments.add(pay3);
		
		return payments;
	}

}
