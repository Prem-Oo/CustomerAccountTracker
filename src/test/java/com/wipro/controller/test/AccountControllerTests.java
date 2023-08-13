package com.wipro.controller.test;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wipro.bean.Account;
import com.wipro.bean.Customer;
import com.wipro.controller.AccountController;
import com.wipro.exception.AccountNotFoundException;
import com.wipro.service.IAccountService;

@AutoConfigureMockMvc
@WebMvcTest(AccountController.class)
public class AccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAccountService accountService;

//    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGetAccounts() throws Exception {
        List<Account> accounts = createSampleAccounts();
        when(accountService.getAllAccounts()).thenReturn(accounts);

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountType").value("Savings"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].accountType").value("Current"));
    }

    @Test
    public void testFundTransfer_ValidTransfer() throws Exception {
        when(accountService.transferFunds(anyInt(), anyInt(), anyDouble())).thenReturn("Fund Transfer Successful");

        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/1/2/1000")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Fund Transfer Successful"));
    }

    @Test
    public void testFundTransfer_NegativeAmount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/1/2/-100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // Assuming you're returning a 400 Bad Request for invalid inputs
                .andExpect(MockMvcResultMatchers.content().string("Invalid amount"));
    }

    @Test
    public void testFundTransfer_SameAccounts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/1/1/500")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // Assuming you're returning a 400 Bad Request for invalid inputs
                .andExpect(MockMvcResultMatchers.content().string("From and to accounts cannot be the same"));
    }

    @Test
    public void testFundTransfer_InvalidAccounts() throws Exception {
        when(accountService.transferFunds(anyInt(), anyInt(), anyDouble())).thenThrow(new AccountNotFoundException("Invalid account"));

        mockMvc.perform(MockMvcRequestBuilders.put("/accounts/10/20/500")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // Assuming you're returning a 400 Bad Request for invalid inputs
                .andExpect(MockMvcResultMatchers.content().string("Invalid account"));
    }

    // Helper method to create sample list of Accounts
    private List<Account> createSampleAccounts() {
        Customer customer1 = createSampleCustomer("John", "123 Main St", "123-456-7890");
        Customer customer2 = createSampleCustomer("Jane", "456 Oak St", "987-654-3210");

        Account account1 = new Account();
        account1.setAccountId(1);
        account1.setAccountType("Savings");
        account1.setBalance(1000.0);
        account1.setCustomer(customer1);

        Account account2 = new Account();
        account2.setAccountId(2);
        account2.setAccountType("Current");
        account2.setBalance(2000.0);
        account2.setCustomer(customer1);

        customer1.setAccounts(Set.of(account1, account2));

        return List.of(account1, account2);
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
