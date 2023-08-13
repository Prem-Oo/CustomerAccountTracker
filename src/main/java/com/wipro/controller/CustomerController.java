package com.wipro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wipro.bean.Customer;
import com.wipro.exception.AccountNotFoundException;
import com.wipro.exception.CustomerNotFoundException;
import com.wipro.service.ICustomerService;

@RestController
public class CustomerController {
	
	@Autowired
	ICustomerService service;
	
	@PostMapping("/customers")
	public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) throws Exception {
		
		return new ResponseEntity<Customer>(service.createCustomer(customer), HttpStatus.CREATED);
	}

	@PutMapping("/customers")
	public ResponseEntity<Customer> updateCustomerByID(@RequestParam Integer id,@RequestBody Customer customer) throws CustomerNotFoundException {
		
		return new ResponseEntity<Customer>(service.updateCustomerByID(id,customer), HttpStatus.OK);
	}

	@GetMapping("/customers")
	public ResponseEntity<?> getCustomers(@RequestParam(required = false) Integer id) throws CustomerNotFoundException {
		if(id!=null) {
			
			return new ResponseEntity<Customer>(service.getCustomerByID(id), HttpStatus.OK);
		}
		
		return new ResponseEntity<List<Customer>>(service.getCustomers(), HttpStatus.OK);
	}

	@DeleteMapping("/customers")
	public ResponseEntity<String> delelteAllCustomer(@RequestParam(required = false) Integer id) throws CustomerNotFoundException {
		
		if(id!=null) {
			return new ResponseEntity<String>(service.delelteCustomerById(id), HttpStatus.OK);
		}
		 return new ResponseEntity<String>(service.delelteAllCustomer(), HttpStatus.OK);
	}
	
//	@DeleteMapping("/customers/{cid}/{aid}")
//	public ResponseEntity<String> deleteAccountFromCustomer(@PathVariable Integer cid,@PathVariable Integer aid) throws CustomerNotFoundException, AccountNotFoundException {
//		
//		if(cid!=null && aid !=null) {
//			return new ResponseEntity<String>(service.deleteAccountFromCustomer(cid, aid), HttpStatus.OK);
//		}
//		 return null;
//	}

}
