package com.exam.finalexam.service;

import com.exam.finalexam.model.BankAccount;
import com.exam.finalexam.model.AccountOperation;

import java.math.BigDecimal;
import java.util.List;

public interface BankingService {
    BankAccount save(BankAccount bankAccount);
    List<BankAccount> findAll();
    BankAccount findByAccountNumber(String accountNumber);
    AccountOperation credit(String accountNumber, BigDecimal amount);
    AccountOperation debit(String accountNumber, BigDecimal amount);
    AccountOperation transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount);
}
