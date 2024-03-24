package com.exam.finalexam.model;

import com.exam.finalexam.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BankAccount {
    private UUID id;
    private BigDecimal balance;
    private BigDecimal interestRate;
    private BigDecimal overDraft;
    private LocalDateTime createdAt;
    private Long customerId;
    private String accountNumber;
    private AccountStatus status;

    private String generateAccountNumber() {
        return "ACC" + (int) (Math.random() * 1000000000);
    }
}
