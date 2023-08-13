package com.wipro.service.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.wipro.bean.Account;
import com.wipro.dao.IAccountDAO;
import com.wipro.exception.AccountNotFoundException;
import com.wipro.exception.FundTransferException;
import com.wipro.service.AccountServiceImpl;

@SpringBootTest
public class AccountServiceImplTests {

    @Autowired
    private AccountServiceImpl accountService;

    @MockBean
    private IAccountDAO accountRepository;

    @Test
    public void testGetAllAccounts() throws AccountNotFoundException {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account(1, "Savings", 1000.0, null));
        accounts.add(new Account(2, "Current", 2000.0, null));

        when(accountRepository.findAll()).thenReturn(accounts);

        List<Account> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testTransferFunds() throws FundTransferException {
        Account fromAccount = new Account(1, "Savings", 1000.0, null);
        Account toAccount = new Account(2, "Current", 2000.0, null);

        when(accountRepository.findById(1)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2)).thenReturn(Optional.of(toAccount));

        String result = accountService.transferFunds(1, 2, 500.0);

        assertEquals("Fund Transfer Succesful", result);
        assertEquals(500.0, fromAccount.getBalance(), 0.001);
        assertEquals(2500.0, toAccount.getBalance(), 0.001);
    }

    @Test
    public void testTransferFundsInsufficientBalance() {
        Account fromAccount = new Account(1, "Savings", 100.0, null);
        Account toAccount = new Account(2, "Current", 2000.0, null);

        when(accountRepository.findById(1)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findById(2)).thenReturn(Optional.of(toAccount));

        assertThrows(FundTransferException.class, () -> accountService.transferFunds(1, 2, 500.0));
    }

    @Test
    public void testTransferFundsInvalidAccounts() {
        assertThrows(FundTransferException.class, () -> accountService.transferFunds(1, 1, 500.0));
    }

    @Test
    public void testGetBalanceOf() throws FundTransferException {
        Account account = new Account(1, "Savings", 1000.0, null);

        when(accountRepository.findById(1)).thenReturn(Optional.of(account));

        Account result = accountService.findAccount(1);

        assertNotNull(result);
        assertEquals(account, result);
    }

    @Test
    public void testGetBalanceOfInvalidAccount() {
        when(accountRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(FundTransferException.class, () -> accountService.findAccount(1));
    }
}



