package com.exam.finalexam.repository;

import com.exam.finalexam.dbConnection.ConnectionDb;
import com.exam.finalexam.enums.AccountStatus;
import com.exam.finalexam.model.BankAccount;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BankAccountRepository {

    public Optional<BankAccount> save(BankAccount bankAccount) {
        try (Connection conn = ConnectionDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO bank_account (id, balance, interest_rate, over_draft, created_at, customer_id, account_number, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setObject(1, bankAccount.getId());
            stmt.setBigDecimal(2, bankAccount.getBalance());
            stmt.setBigDecimal(3, bankAccount.getInterestRate());
            stmt.setBigDecimal(4, bankAccount.getOverDraft());
            stmt.setObject(5, bankAccount.getCreatedAt());
            stmt.setLong(6, bankAccount.getCustomerId());
            stmt.setString(7, bankAccount.getAccountNumber());
            stmt.setString(8, bankAccount.getStatus().toString());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bankAccount.setId(generatedKeys.getObject(1, UUID.class));
                }
            }

            return Optional.of(bankAccount);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    public List<BankAccount> findAll() {
        List<BankAccount> accounts = new ArrayList<>();
        try (Connection conn = ConnectionDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bank_account")) {

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BankAccount account = mapResultSetToBankAccount(rs);
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        try (Connection conn = ConnectionDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM bank_account WHERE account_number = ?")) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBankAccount(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private BankAccount mapResultSetToBankAccount(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        BigDecimal balance = rs.getBigDecimal("balance");
        BigDecimal interestRate = rs.getBigDecimal("interest_rate");
        BigDecimal overDraft = rs.getBigDecimal("over_draft");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        Long customerId = rs.getLong("customer_id");
        String accountNumber = rs.getString("account_number");
        String statusString = rs.getString("status");
        AccountStatus status = null;
        if (statusString != null) {
            try {
                status = AccountStatus.valueOf(statusString);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        return new BankAccount(id, balance, interestRate, overDraft, createdAt, customerId, accountNumber, status);
    }


    public void updateBalance(UUID id, BigDecimal newBalance) {
        try (Connection conn = ConnectionDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE bank_account SET balance = ? WHERE id = ?")) {

            stmt.setBigDecimal(1, newBalance);
            stmt.setObject(2, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(UUID id) {
        try (Connection conn = ConnectionDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM bank_account WHERE id = ?")) {

            stmt.setObject(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
