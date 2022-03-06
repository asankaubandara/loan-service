package com.loan.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {

	@JsonProperty(ATTR_LOAN_ID)
	public Long loanId;
	public static final String ATTR_LOAN_ID = "loanId";

	@JsonProperty(ATTR_PAY_ID)
	public Long paymentId;
	public static final String ATTR_PAY_ID = "paymentId";

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

}
