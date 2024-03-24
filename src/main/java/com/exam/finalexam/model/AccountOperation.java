package com.exam.finalexam.model;

import com.exam.finalexam.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountOperation {
    private int id;
    private BigDecimal amount;
    private LocalDateTime operationDate;
    private String description;
    private OperationType type;
    private BankAccount bankAccount;

}
