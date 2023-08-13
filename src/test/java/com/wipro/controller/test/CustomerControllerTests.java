package com.wipro.controller.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.bean.Account;
import com.wipro.bean.Customer;
import com.wipro.controller.CustomerController;
import com.wipro.service.ICustomerService;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ICustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        mockMvc = MockMvcBuilders.standaloneSetup(customerController).build();
    }

    @Test
    public void testCreateCustomer() throws Exception {
        Customer customer = createSampleCustomerWithAccounts();

        when(customerService.createCustomer(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(customer)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John"));
    }

    @Test
    public void testGetCustomer() throws Exception {
        Customer customer = createSampleCustomerWithAccounts();
        when(customerService.getCustomerByID(1)).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders.get("/customers")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John"));
    }

    @Test
    public void testGetCustomers() throws Exception {
        List<Customer> customers = createSampleCustomersWithAccounts();
        when(customerService.getCustomers()).thenReturn(customers);

        mockMvc.perform(MockMvcRequestBuilders.get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("John"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Jane"));
    }

    @Test
    public void testUpdateCustomer() throws Exception {
        Customer customer = createSampleCustomerWithAccounts();
        when(customerService.updateCustomerByID(eq(1), any(Customer.class))).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders.put("/customers")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(customer)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John"));
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        when(customerService.delelteCustomerById(1)).thenReturn("Customer deleted");

        mockMvc.perform(MockMvcRequestBuilders.delete("/customers")
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Customer deleted"));
    }

 // Helper method to create a sample Customer object with associated Accounts
    private Customer createSampleCustomerWithAccounts() {
        Customer customer = new Customer();
        customer.setCustId(1);
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

        Set<Account> accounts = new HashSet<>();
        accounts.add(account1);
        accounts.add(account2);
        customer.setAccounts(accounts);

        return customer;
    }

    // Helper method to create sample list of Customers with associated Accounts
    private List<Customer> createSampleCustomersWithAccounts() {
        Customer customer1 = createSampleCustomerWithAccounts();

        Customer customer2 = new Customer();
        customer2.setCustId(2);
        customer2.setName("Jane");
        customer2.setAddress("456 Oak St");
        customer2.setMobileNum("987-654-3210");

        Account account3 = new Account();
        account3.setAccountType("Savings");
        account3.setBalance(1500.0);
        account3.setCustomer(customer2);

        Set<Account> accounts2 = new HashSet<>();
        accounts2.add(account3);
        customer2.setAccounts(accounts2);

        return List.of(customer1, customer2);
    }


    // Helper method to convert objects to JSON format
    private String asJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}