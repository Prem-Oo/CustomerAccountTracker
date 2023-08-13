package com.wipro.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wipro.bean.Account;
import com.wipro.bean.Customer;
import com.wipro.dao.ICustomerDAO;
import com.wipro.exception.AccountNotFoundException;
import com.wipro.exception.CustomerNotFoundException;

@Service
public class CustomerServiceImpl implements ICustomerService {

	@Autowired
	ICustomerDAO repo;
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Customer createCustomer(Customer customer) throws Exception {
		Customer result = repo.save(customer);
		if(result== null)
			throw new Exception("Cant create Customer");
		return result;
	}

	@Override
	public Customer updateCustomerByID( Integer id,Customer updatedCustomer) throws CustomerNotFoundException {
		Optional<Customer> op = Optional.of(repo.findById(id).orElseThrow(()-> new CustomerNotFoundException("Customer ID not there to update")));
		if(op.isPresent()) {
			Customer existingCustomer = op.get();

			 // Update parent entity properties
	        existingCustomer.setName(updatedCustomer.getName());
	        existingCustomer.setAddress(updatedCustomer.getAddress());
	        existingCustomer.setMobileNum(updatedCustomer.getMobileNum());

	        // Update child entities (accounts)
	        Set<Account> updatedAccounts = updatedCustomer.getAccounts();

	        // Clear existing child entities and reattach updated child entities
	        existingCustomer.getAccounts().clear();
	        existingCustomer.getAccounts().addAll(updatedAccounts);

	        // Update customer reference for each account
	        existingCustomer.getAccounts().forEach(account -> account.setCustomer(existingCustomer));

	        // Save the updated parent entity along with updated child entities
	        return repo.save(existingCustomer);
	    }
		return null;
	}

	@Override
	public List<Customer> getCustomers() throws CustomerNotFoundException {
		List<Customer> customers = repo.findAll();
		if(customers==null)
			throw new CustomerNotFoundException("Customers not Found");
		if(customers.size()==0) {
			throw new CustomerNotFoundException("There are no customers");
		}
		return repo.findAll();
	}
	
	@Override
	public Customer getCustomerByID(Integer id) throws CustomerNotFoundException {
		
		return repo.findById(id).orElseThrow(()-> new CustomerNotFoundException("Customer with ID "+id+" not found"));
	}

	@Override
	public String delelteAllCustomer() throws CustomerNotFoundException {
		List<Customer> list = repo.findAll();
		if(list.isEmpty()) {
			throw new CustomerNotFoundException("No Record there to delete");
		}
		repo.deleteAll();
		return "All Customers deleted";
	}

	@Override
	public String delelteCustomerById(Integer id) throws CustomerNotFoundException {
		Optional<Customer> op = Optional.of(repo.findById(id).orElseThrow(()-> new CustomerNotFoundException("Customer with ID "+id+" doesn't exist to Delete")));
		if(op.isPresent()) {
			repo.deleteById(id);
		}
		return "Customer with id "+id+" deleted";
	}

//	@Override
//	@Transactional
//    public String deleteAccountFromCustomer(Integer customerId, Integer accountId) throws CustomerNotFoundException, AccountNotFoundException {
//        Optional<Customer> optionalCustomer = repo.findById(customerId);
//        boolean bool;
//        if (optionalCustomer.isPresent()) {
//            Customer customer = optionalCustomer.get();
//            
//            // Find the account to delete
//            Account accountToDelete = null;
//            for (Account account : customer.getAccounts()) {
//                if (account.getAccountId().equals(accountId)) {
//                    accountToDelete = account;
//                    break;
//                }
//            }
//            
//            if (accountToDelete != null) {
//            	 customer.getAccounts().remove(accountToDelete);
//                 entityManager.remove(accountToDelete);
//                bool=true;
//            }
//            else
//                throw new AccountNotFoundException("Account with ID "+accountId+" not Found");
//        } else
//            throw new CustomerNotFoundException(" Customer not Found : "+customerId);
//        
//        return (bool)? customerId+"CustomerAccount "+" Account with ID "+accountId+" deleted":"Deletion Failed";
//    }
	
}
