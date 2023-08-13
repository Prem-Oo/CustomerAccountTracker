package com.wipro.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.wipro.bean.Account;
import com.wipro.bean.Customer;
import com.wipro.dao.ICustomerDAO;
import com.wipro.exception.CustomerNotFoundException;
import com.wipro.service.CustomerServiceImpl;

@SpringBootTest
public class CustomerServiceImplTests {

    @Autowired
    private CustomerServiceImpl customerService;

    @MockBean
    private ICustomerDAO customerRepository;

    @Test
    public void testCreateCustomer() throws Exception {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setAddress("123 Main St");
        customer.setMobileNum("123-456-7890");

        Account account1 = new Account();
        account1.setAccountType("Savings");
        account1.setBalance(1000.0);
        account1.setCustomer(customer);

        Account account2 = new Account();
        account2.setAccountType("Current");
        account2.setBalance(2000.0);
        account2.setCustomer(customer);

        customer.setAccounts(new HashSet<>(Arrays.asList(account1, account2)));

        when(customerRepository.save(customer)).thenReturn(customer);

        Customer createdCustomer = customerService.createCustomer(customer);

        assertNotNull(createdCustomer);
        assertEquals("John", createdCustomer.getName());
        assertEquals("123 Main St", createdCustomer.getAddress());
        assertEquals("123-456-7890", createdCustomer.getMobileNum());
        assertEquals(2, createdCustomer.getAccounts().size());
    }

    @Test
    public void testUpdateCustomerByID() throws CustomerNotFoundException {
        Customer existingCustomer = new Customer();
        existingCustomer.setCustId(1);
        existingCustomer.setName("John");
        existingCustomer.setAddress("123 Main St");
        existingCustomer.setMobileNum("123-456-7890");

        Account account1 = new Account();
        account1.setAccountType("Savings");
        account1.setBalance(1000.0);
        account1.setCustomer(existingCustomer);

        existingCustomer.setAccounts(new HashSet<>(Collections.singletonList(account1)));

        Customer updatedCustomer = new Customer();
        updatedCustomer.setName("Jane");
        updatedCustomer.setAddress("456 Oak St");
        updatedCustomer.setMobileNum("987-654-3210");

        // Initialize the accounts set in the updated customer
        updatedCustomer.setAccounts(new HashSet<>());

        when(customerRepository.findById(existingCustomer.getCustId())).thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(existingCustomer)).thenReturn(updatedCustomer);

        Customer result = customerService.updateCustomerByID(existingCustomer.getCustId(), updatedCustomer);

        assertNotNull(result);
        assertEquals("Jane", result.getName());
        assertEquals("456 Oak St", result.getAddress());
        assertEquals("987-654-3210", result.getMobileNum());
        assertEquals(0, result.getAccounts().size()); // Updated customer should have no accounts
    }

    // Other test methods with one-to-many relationship...
    @Test
    public void testGetCustomerByID() throws CustomerNotFoundException {
        Customer customer = createSampleCustomer("John", "123 Main St", "123-456-7890");
        int customerId = customer.getCustId();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Customer retrievedCustomer = customerService.getCustomerByID(customerId);

        assertNotNull(retrievedCustomer);
        assertEquals("John", retrievedCustomer.getName());
    }

    @Test
    public void testDeleteAllCustomers() throws CustomerNotFoundException {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(CustomerNotFoundException.class, () -> customerService.delelteAllCustomer());

        verify(customerRepository, times(1)).findAll();
        verify(customerRepository, times(0)).deleteAll();
    }

    @Test
    public void testDeleteCustomerById() throws CustomerNotFoundException {
        Customer customer = createSampleCustomer("John", "123 Main St", "123-456-7890");
        int customerId = customer.getCustId();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        String result = customerService.delelteCustomerById(customerId);

        assertEquals("Customer with id " + customerId + " deleted", result);
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    // Helper method to create a sample Customer object
    private Customer createSampleCustomer(String name, String address, String mobileNum) {
        Customer customer = new Customer();
        customer.setCustId(1);
        customer.setName(name);
        customer.setAddress(address);
        customer.setMobileNum(mobileNum);
        return customer;
    }
}