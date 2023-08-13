package com.wipro.service;

import java.util.List;

import com.wipro.bean.Customer;
import com.wipro.exception.AccountNotFoundException;
import com.wipro.exception.CustomerNotFoundException;

public interface ICustomerService {

	public Customer createCustomer(Customer customer) throws Exception;
	
	public Customer updateCustomerByID(Integer id,Customer customer) throws CustomerNotFoundException;
	
	public List<Customer>  getCustomers() throws CustomerNotFoundException;
	public Customer getCustomerByID(Integer id) throws CustomerNotFoundException;
	
	public  String delelteAllCustomer() throws CustomerNotFoundException;
	//public String deleteAccountFromCustomer(Integer customerId, Integer accountId) throws CustomerNotFoundException, AccountNotFoundException;
	public  String delelteCustomerById(Integer id) throws CustomerNotFoundException;
	
}
