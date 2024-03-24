package com.exam.finalexam.controller;

import com.exam.finalexam.model.AccountOperation;
import com.exam.finalexam.model.BankAccount;
import com.exam.finalexam.service.BankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/banking")
public class BankingController {

    private final BankingService bankingService;

    @Autowired
    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @PostMapping("/accounts")
    public BankAccount createAccount(@RequestBody BankAccount bankAccount) {
        return bankingService.save(bankAccount);
    }

    @GetMapping("/accounts")
    public List<BankAccount> getAllAccounts() {
        return bankingService.findAll();
    }

    @GetMapping("/accounts/{accountNumber}")
    public BankAccount getAccountByNumber(@PathVariable String accountNumber) {
        return bankingService.findByAccountNumber(accountNumber);
    }

    @PostMapping("/accounts/{accountNumber}/credit")
    public AccountOperation creditAccount(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        return bankingService.credit(accountNumber, amount);
    }

    @PostMapping("/accounts/{accountNumber}/debit")
    public AccountOperation debitAccount(@PathVariable String accountNumber, @RequestParam BigDecimal amount) {
        return bankingService.debit(accountNumber, amount);
    }

    @PostMapping("/transfer")
    public AccountOperation transfer(@RequestParam String fromAccountNumber, @RequestParam String toAccountNumber, @RequestParam BigDecimal amount) {
        return bankingService.transfer(fromAccountNumber, toAccountNumber, amount);
    }
}
