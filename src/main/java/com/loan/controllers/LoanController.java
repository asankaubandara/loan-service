package com.loan.controllers;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;

import com.loan.exception.LoanException;
import com.loan.models.LoanBalanceResponse;
import com.loan.models.LoanRequest;
import com.loan.models.LoanResponse;
import com.loan.models.PaymentRequest;
import com.loan.models.PaymentResponse;
import com.loan.services.LoanService;


@Validated
@RestController
@RequestMapping("/loan")
public class LoanController {
	
	@Autowired
	LoanService loanService;
	
	private static final Logger LOGGER = Logger.getLogger(LoanController.class.getSimpleName());

	
	/**
	 * Create Loan. HTTP Request handler at the /post endpoint. Only accepts POST
	 * requests returns JSON
	 * 
	 * @param loanReq TaxRequest with vehicle type, city and dates
	 * @return LoanResponse record
	 */
	@PostMapping
	public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanRequest taxReq) {
		try {
			return ResponseEntity.ok().body(loanService.addLoan(taxReq));
		} catch (IllegalArgumentException ex) {
			LOGGER.log(Level.SEVERE, ex.toString(), ex);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (LoanException ex) {
			LOGGER.log(Level.SEVERE, ex.toString(), ex);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}
	
	/**
	 * Do a payment for the loan. HTTP Request handler at the /post endpoint. Only accepts POST
	 * requests returns JSON
	 * 
	 * @param loanReq TaxRequest with vehicle type, city and dates
	 * @return LoanResponse record
	 */
	@PostMapping("/pay")
	public ResponseEntity<PaymentResponse> payment(@Valid @RequestBody PaymentRequest taxReq) {
		try {
			return ResponseEntity.ok().body(loanService.makePayment(taxReq));
		} catch (IllegalArgumentException ex) {
			LOGGER.log(Level.SEVERE, ex.toString(), ex);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (LoanException ex) {
			LOGGER.log(Level.SEVERE, ex.toString(), ex);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}
	
	
	/**
	 * Get balance of the existing loan. HTTP Request handler at the /post endpoint. Only accepts GET
	 * requests returns JSON
	 * 
	 * @param  id is the Loan ID
	 * @return LoanResponse record
	 */
	@GetMapping
	public ResponseEntity<LoanBalanceResponse> getBalance(@Valid @RequestParam("id")  Long loanId, @Valid @RequestParam("date") String date) {
		try {
			return ResponseEntity.ok().body(loanService.getBalance(loanId, date));
		} catch (IllegalArgumentException ex) {
			LOGGER.log(Level.SEVERE, ex.toString(), ex);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (LoanException ex) {
			LOGGER.log(Level.SEVERE, ex.toString(), ex);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
		}
	}

	
}
