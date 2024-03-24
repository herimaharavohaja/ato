package com.exam.finalexam.repository;

import com.exam.finalexam.dbConnection.ConnectionDb;
import com.exam.finalexam.model.BankAccount;
import com.exam.finalexam.model.Customer;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Repository
public class CustomerRepository {
    private final Connection connection;

    public CustomerRepository() {
        this.connection = ConnectionDb.getConnection();
    }

    public void addCustomer(Customer customer) throws SQLException {
        String customerSql = "INSERT INTO customer (monthly_salary, date_of_birth, email, first_name, last_name, name, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String bankAccountSql = "INSERT INTO bank_account (id, account_number, created_at) VALUES (?, ?, ?)";

        try (PreparedStatement customerStatement = connection.prepareStatement(customerSql);
             PreparedStatement bankAccountStatement = connection.prepareStatement(bankAccountSql)) {

            customerStatement.setBigDecimal(1, BigDecimal.valueOf(customer.getMonthlySalary()));
            customerStatement.setDate(2, Date.valueOf(String.valueOf(customer.getDateOfBirth())));
            customerStatement.setString(3, customer.getEmail());
            customerStatement.setString(4, customer.getFirstName());
            customerStatement.setString(5, customer.getLastName());
            customerStatement.setString(6, customer.getName());
            customerStatement.setString(7, customer.getPassword());
            customerStatement.executeUpdate();

            UUID accountId = UUID.randomUUID();
            String accountNumber = generateAccountNumber();
            LocalDateTime createdAt = LocalDateTime.now();

            bankAccountStatement.setObject(1, accountId);
            bankAccountStatement.setString(2, accountNumber);
            bankAccountStatement.setObject(3, createdAt);
            bankAccountStatement.executeUpdate();
        }
    }

    private String generateAccountNumber() {
        return "ACC" + (int) (Math.random() * 1000000000);
    }


    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT c.id, c.monthly_salary, c.date_of_birth, c.email, c.first_name, c.last_name, c.name, c.password, " +
                "b.account_number AS bank_account_number, b.created_at AS bank_account_created_at " +
                "FROM customer c " +
                "LEFT JOIN bank_account b ON c.id = b.customer_id";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Customer customer = new Customer();
                customer.setId(resultSet.getLong("id"));
                customer.setMonthlySalary(resultSet.getBigDecimal("monthly_salary"));
                customer.setDateOfBirth(resultSet.getDate("date_of_birth").toLocalDate());
                customer.setEmail(resultSet.getString("email"));
                customer.setFirstName(resultSet.getString("first_name"));
                customer.setLastName(resultSet.getString("last_name"));
                customer.setName(resultSet.getString("name"));
                customer.setPassword(resultSet.getString("password"));

                if (resultSet.getString("bank_account_number") != null) {
                    BankAccount bankAccount = new BankAccount();
                    bankAccount.setAccountNumber(resultSet.getString("bank_account_number"));
                    bankAccount.setCreatedAt(resultSet.getDate("bank_account_created_at").toLocalDate().atStartOfDay());
                    customer.setBankAccounts(Collections.singletonList(bankAccount));
                }

                customers.add(customer);
            }
        }
        return customers;
    }



    public Customer findById(int id) throws SQLException {
        String sql = "SELECT c.*, b.account_number AS bank_account_number, b.created_at AS bank_account_created_at " +
                "FROM customer c " +
                "LEFT JOIN bank_account b ON c.id = b.customer_id " +
                "WHERE c.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Customer customer = new Customer();
                    customer.setId(resultSet.getLong("id"));
                    customer.setMonthlySalary(resultSet.getBigDecimal("monthly_salary"));
                    customer.setDateOfBirth(resultSet.getDate("date_of_birth").toLocalDate());
                    customer.setEmail(resultSet.getString("email"));
                    customer.setFirstName(resultSet.getString("first_name"));
                    customer.setLastName(resultSet.getString("last_name"));
                    customer.setName(resultSet.getString("name"));
                    customer.setPassword(resultSet.getString("password"));

                    String bankAccountNumber = resultSet.getString("bank_account_number");
                    if (bankAccountNumber != null) {
                        BankAccount bankAccount = new BankAccount();
                        bankAccount.setAccountNumber(bankAccountNumber);
                        bankAccount.setCreatedAt(resultSet.getDate("bank_account_created_at").toLocalDate().atStartOfDay());
                        customer.setBankAccounts(Collections.singletonList(bankAccount));
                    }

                    return customer;
                }
            }
        }
        return null;
    }


    public void update(Customer customer) throws SQLException {
        String sql = "UPDATE customer SET monthly_salary = ?, date_of_birth = ?, email = ?, first_name = ?, last_name = ?, name = ?, password = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, BigDecimal.valueOf(customer.getMonthlySalary()));
            statement.setDate(2, java.sql.Date.valueOf(String.valueOf(customer.getDateOfBirth())));
            statement.setString(3, customer.getEmail());
            statement.setString(4, customer.getFirstName());
            statement.setString(5, customer.getLastName());
            statement.setString(6, customer.getName());
            statement.setString(7, customer.getPassword());
            statement.setLong(8, customer.getId());
            statement.executeUpdate();
        }
    }


    public void deleteCustomerAndAssociatedData(int customerId) throws SQLException {
        try {
            connection.setAutoCommit(false);
            deleteAccountOperationsForCustomer(customerId);
            deleteBankAccountsForCustomer(customerId);
            deleteCustomer(customerId);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void deleteAccountOperationsForCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM account_operation WHERE bank_account_id IN (SELECT id FROM bank_account WHERE customer_id = ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.executeUpdate();
        }
    }

    private void deleteBankAccountsForCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM bank_account WHERE customer_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.executeUpdate();
        }
    }

    private void deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customer WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, customerId);
            statement.executeUpdate();
        }
    }
}
