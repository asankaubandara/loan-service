package com.loan.models;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanBalanceResponse {

	@JsonProperty(ATTR_LOAN_INITIAL_AMOUNT)
	public BigDecimal loanInitialAmount;
	public static final String ATTR_LOAN_INITIAL_AMOUNT = "loanInitialAmount";

	@JsonProperty(ATTR_TOTAL_BALANCE)
	public BigDecimal totalBalanceToDate;
	public static final String ATTR_TOTAL_BALANCE = "totalBalanceToDate";

	@JsonProperty(ATTR_INTEREST_DATE)
	public Double interestRate;
	public static final String ATTR_INTEREST_DATE = "interestRate";

	@JsonProperty(ATTR_START_DATE)
	public Date startDate;
	public static final String ATTR_START_DATE = "startDate";

	public BigDecimal getLoanInitialAmount() {
		return loanInitialAmount;
	}

	public void setLoanInitialAmount(BigDecimal loanInitialAmount) {
		this.loanInitialAmount = loanInitialAmount;
	}

	public BigDecimal getTotalBalanceToDate() {
		return totalBalanceToDate;
	}

	public void setTotalBalanceToDate(BigDecimal totalBalanceToDate) {
		this.totalBalanceToDate = totalBalanceToDate;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

}
