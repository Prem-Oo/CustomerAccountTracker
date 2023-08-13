package com.wipro.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.bean.Account;
import com.wipro.bean.AccountDTO;
import com.wipro.bean.Customer;
import com.wipro.dao.IAccountDAO;
import com.wipro.dao.ICustomerDAO;
import com.wipro.exception.AccountNotFoundException;
import com.wipro.exception.CustomerNotFoundException;
import com.wipro.exception.FundTransferException;

@Service
public class AccountServiceImpl implements IAccountService {
	
	@Autowired
	IAccountDAO repo;
	
	@Autowired
	ICustomerDAO crepo;
	
	@Override
    public List<AccountDTO> getAllAccountsWithCustomers() throws AccountNotFoundException {
        List<Account> accounts = repo.findAllWithCustomers();
        if (accounts == null || accounts.isEmpty()) {
            throw new AccountNotFoundException("There are no Accounts");
        }
        
        List<AccountDTO> accountDTOs = new ArrayList<>();
        for (Account account : accounts) {
            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setAccountId(account.getAccountId());
            accountDTO.setAccountType(account.getAccountType());
            accountDTO.setBalance(account.getBalance());

            Customer customer = account.getCustomer();
            accountDTO.setCustomerId(customer.getCustId());
            accountDTO.setCustomerName(customer.getName());
            accountDTO.setCustomerAddress(customer.getAddress());
            accountDTO.setCustomerMobileNum(customer.getMobileNum());

            accountDTOs.add(accountDTO);
        }

        return accountDTOs;
    }

	@Override
	public List<Account> getAllAccounts() throws AccountNotFoundException {
		List<Account> accounts = repo.findAll();
		if(accounts==null)
			throw new AccountNotFoundException("Accounts not there");
		if(accounts.size()==0) {
			throw new AccountNotFoundException("There are no Accounts");
		}
		return accounts;
	}

	@Override
	@Transactional
	public String transferFunds(int from, int to, double amount) throws FundTransferException {
		if(from != to && amount>0) {
			// check accounts and its balance
			Account fromAcc = findAccount(from);
			Account toAcc = findAccount(to);
			if(fromAcc.getBalance()>=amount && toAcc!=null) {
				// transfer funds
				fromAcc.setBalance(fromAcc.getBalance()-amount);
				toAcc.setBalance(toAcc.getBalance()+amount);
				repo.save(fromAcc);
				repo.save(toAcc);
				return "Fund Transfer Succesful";
			}
			else {
				throw new FundTransferException("Insufficient balance in the source account");
			}
			
		}
		else {
			throw new FundTransferException("Invalid account IDs or Amount");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Account findAccount(int accountNumber) throws FundTransferException {
		// // check accounts 
		Optional<Account> acc = repo.findById(accountNumber);
		if(acc.isPresent()) {
			
			return acc.get();
		}
		else throw new FundTransferException("Invalid account ID "+accountNumber);
	}

	@Override
	public String deleteAccountFromCustomer(Integer customerId, Integer accountId)
			throws CustomerNotFoundException, AccountNotFoundException {
		
		 Optional<Customer> optionalCustomer = crepo.findById(customerId);
		    boolean bool;
		    if (optionalCustomer.isPresent()) {
		        Customer customer = optionalCustomer.get();

		        // Find the account to delete
		        Account accountToDelete = null;
		        for (Account account : customer.getAccounts()) {
		            if (account.getAccountId().equals(accountId)) {
		                accountToDelete = account;
		                break;
		            }
		        }

		        if (accountToDelete != null) {
		        	 // Remove the account reference from the customer entity
		            customer.getAccounts().remove(accountToDelete);

		            // Set account's customer reference to null
		            accountToDelete.setCustomer(null);

		            // Delete the orphaned account from the database
		            repo.delete(accountToDelete);

		            // Save the modified customer entity
		            crepo.save(customer); // Use crepo for customer


		            bool = true;
		        } else {
		            throw new AccountNotFoundException("Account with ID " + accountId + " not Found");
		        }
		    } else {
		        throw new CustomerNotFoundException("Customer not Found : " + customerId);
		    }

		    return (bool) ? "Customer Account " + " Account with ID " + accountId + " deleted" : "Deletion Failed";
	}



}
