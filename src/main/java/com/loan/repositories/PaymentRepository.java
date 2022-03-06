package com.loan.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.loan.models.Payment;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long>{
	
	List<Payment> findByLoanIdOrderByPaymentDateAsc(Long loanId);

}
