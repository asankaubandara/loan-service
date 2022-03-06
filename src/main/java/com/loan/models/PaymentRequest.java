package com.loan.models;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRequest {

	@JsonProperty(ATTR_PAY_AMOUNT)
	public BigDecimal payment;
	public static final String ATTR_PAY_AMOUNT = "payment";

	@JsonProperty(ATTR_LOAN_ID)
	public int loanId;
	public static final String ATTR_LOAN_ID = "loanId";

	@JsonProperty(ATTR_PAY_DATE)
	public String paymentDate;
	public static final String ATTR_PAY_DATE = "paymentDate";

	public BigDecimal getPayment() {
		return payment;
	}

	public void setPayment(BigDecimal payment) {
		this.payment = payment;
	}

	public int getLoanId() {
		return loanId;
	}

	public void setLoanId(int loanId) {
		this.loanId = loanId;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

}
