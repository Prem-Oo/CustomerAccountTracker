package com.wipro.service;

import java.util.List;

import com.wipro.bean.Account;
import com.wipro.bean.AccountDTO;
import com.wipro.bean.Customer;
import com.wipro.exception.AccountNotFoundException;
import com.wipro.exception.CustomerNotFoundException;
import com.wipro.exception.FundTransferException;

public interface IAccountService {

//	public String addAccount(Account acc);
	public List<Account> getAllAccounts() throws AccountNotFoundException;
	public List<AccountDTO> getAllAccountsWithCustomers() throws AccountNotFoundException;
//	public List<Customer> getAllCustomers();
	public String deleteAccountFromCustomer(Integer customerId, Integer accountId) throws CustomerNotFoundException, AccountNotFoundException;

	public String transferFunds(int from,int to,double amount) throws FundTransferException;
	  
	public Account findAccount(int accountNumber) throws FundTransferException;
}
