package com.exam.finalexam.service;

import com.exam.finalexam.enums.OperationType;
import com.exam.finalexam.model.AccountOperation;
import com.exam.finalexam.model.BankAccount;
import com.exam.finalexam.repository.AccountOperationRepository;
import com.exam.finalexam.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BankingServiceImpl implements BankingService {

    private final BankAccountRepository bankAccountRepository;
    private final AccountOperationRepository accountOperationRepository;

    @Autowired
    public BankingServiceImpl(BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.accountOperationRepository = accountOperationRepository;
    }

    @Override
    public BankAccount save(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount).orElse(null);
    }

    @Override
    public List<BankAccount> findAll() {
        return bankAccountRepository.findAll();
    }

    @Override
    public BankAccount findByAccountNumber(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber).orElse(null);
    }

    @Override
    public AccountOperation credit(String accountNumber, BigDecimal amount) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber).orElse(null);
        if (account != null) {
            BigDecimal newBalance = account.getBalance().add(amount);
            account.setBalance(newBalance);
            AccountOperation operation = new AccountOperation();
            operation.setAmount(amount);
            operation.setOperationDate(LocalDateTime.now());
            operation.setDescription("Credit operation");
            operation.setType(OperationType.CREDIT);
            operation.setBankAccount(account);

            bankAccountRepository.save(account);
            return accountOperationRepository.save(operation);
        }
        return null;
    }


    @Override
    public AccountOperation debit(String accountNumber, BigDecimal amount) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber).orElse(null);
        if (account != null && account.getBalance().compareTo(amount) >= 0) {
            BigDecimal newBalance = account.getBalance().subtract(amount);
            bankAccountRepository.updateBalance(account.getId(), newBalance);

            AccountOperation operation = new AccountOperation();
            operation.setAmount(amount);
            operation.setOperationDate(LocalDateTime.now());
            operation.setDescription("Debit operation");
            operation.setType(OperationType.DEBIT);
            operation.setBankAccount(account);
            return accountOperationRepository.save(operation);
        }
        return null;
    }

    @Override
    public AccountOperation transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
        BankAccount fromAccount = bankAccountRepository.findByAccountNumber(fromAccountNumber).orElse(null);
        BankAccount toAccount = bankAccountRepository.findByAccountNumber(toAccountNumber).orElse(null);

        if (fromAccount != null && toAccount != null && fromAccount.getBalance().compareTo(amount) >= 0) {
            AccountOperation debitOperation = new AccountOperation();
            debitOperation.setAmount(amount);
            debitOperation.setOperationDate(LocalDateTime.now());
            debitOperation.setDescription("Transfer to " + toAccountNumber);
            debitOperation.setType(OperationType.DEBIT);
            debitOperation.setBankAccount(fromAccount);

            AccountOperation creditOperation = new AccountOperation();
            creditOperation.setAmount(amount);
            creditOperation.setOperationDate(LocalDateTime.now());
            creditOperation.setDescription("Transfer from " + fromAccountNumber);
            creditOperation.setType(OperationType.CREDIT);
            creditOperation.setBankAccount(toAccount);

            accountOperationRepository.save(debitOperation);
            accountOperationRepository.save(creditOperation);

            BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
            bankAccountRepository.updateBalance(fromAccount.getId(), newFromBalance);

            BigDecimal newToBalance = toAccount.getBalance().add(amount);
            bankAccountRepository.updateBalance(toAccount.getId(), newToBalance);

            return creditOperation;
        }
        return null;
    }

}
