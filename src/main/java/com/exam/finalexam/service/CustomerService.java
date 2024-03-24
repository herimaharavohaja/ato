package com.exam.finalexam.service;
import com.exam.finalexam.model.Customer;
import com.exam.finalexam.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void addCustomer(Customer customer) throws SQLException {
        customerRepository.addCustomer(customer);
    }

    public List<Customer> getAllCustomers() throws SQLException {
        return customerRepository.getAllCustomers();
    }

    public Customer findById(int id) throws SQLException {
        return customerRepository.findById(id);
    }
    public void updateCustomer(Customer customer) throws SQLException {
        customerRepository.update(customer);
    }

    public void deleteCustomerWithAssociatedData(int customerId) throws SQLException {
        customerRepository.deleteCustomerAndAssociatedData(customerId);
    }
}
