package com.loan.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.loan.models.Loan;

@Repository
public interface LoanRepository extends CrudRepository<Loan, Long>{

}
