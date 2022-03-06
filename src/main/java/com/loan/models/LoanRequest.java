package com.loan.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanRequest {

	@JsonProperty(ATTR_INITIAL_AMOUNT)
	public BigDecimal initialAmount;
	public static final String ATTR_INITIAL_AMOUNT = "initialAmount";

	@JsonProperty(ATTR_INTEREST_DATE)
	public Double interestRate;
	public static final String ATTR_INTEREST_DATE = "interestRate";

	@JsonProperty(ATTR_START_DATE)
	public String startDate;
	public static final String ATTR_START_DATE = "startDate";

	public BigDecimal getInitialAmount() {
		return initialAmount;
	}

	public void setInitialAmount(BigDecimal initialAmount) {
		this.initialAmount = initialAmount;
	}

	public Double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(Double interestRate) {
		this.interestRate = interestRate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

}
