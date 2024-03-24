package com.exam.finalexam.repository;

import com.exam.finalexam.dbConnection.ConnectionDb;
import com.exam.finalexam.model.AccountOperation;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
@Repository
public class AccountOperationRepository {

    public AccountOperation save(AccountOperation operation) {
        try (Connection conn = ConnectionDb.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO account_operation (id, amount, operation_date, bank_account_id, description, type) " +
                     "VALUES (?, ?, ?, ?, ?, ?)")) {

            stmt.setInt(1, operation.getId());
            stmt.setBigDecimal(2, operation.getAmount());
            stmt.setObject(3, operation.getOperationDate());
            stmt.setObject(4, operation.getBankAccount());
            stmt.setString(5, operation.getDescription());
            stmt.setString(6, operation.getType().toString());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return operation;
    }
}
