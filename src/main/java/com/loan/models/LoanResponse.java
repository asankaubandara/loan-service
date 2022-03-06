package com.loan.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LoanResponse {

	@JsonProperty(ATTR_LOAN_ID)
	public Long loanId;
	public static final String ATTR_LOAN_ID = "loanId";

	public Long getLoanId() {
		return loanId;
	}

	public void setLoanId(Long loanId) {
		this.loanId = loanId;
	}

}
