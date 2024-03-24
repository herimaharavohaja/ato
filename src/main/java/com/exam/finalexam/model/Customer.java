package com.exam.finalexam.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private Long id;
    private String name;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private double monthlySalary;
    private List<BankAccount> bankAccounts;
    public void setMonthlySalary(BigDecimal monthlySalary) {
        this.monthlySalary = monthlySalary.doubleValue();
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = java.sql.Date.valueOf(dateOfBirth);
    }

}
