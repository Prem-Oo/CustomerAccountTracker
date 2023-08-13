package com.wipro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wipro.bean.Account;
import com.wipro.bean.AccountDTO;
import com.wipro.bean.Customer;
import com.wipro.exception.AccountNotFoundException;
import com.wipro.exception.CustomerNotFoundException;
import com.wipro.exception.FundTransferException;
import com.wipro.service.IAccountService;

@RestController
public class AccountController {

	@Autowired
	IAccountService service;
	
	 @GetMapping("/accountswithcustomer")
	    public ResponseEntity<?> getAccountsWithCustomers(@RequestParam(required = false) Integer id) throws AccountNotFoundException {
	        List<AccountDTO> accountDTOs = service.getAllAccountsWithCustomers();
	        System.out.println(accountDTOs);
	        return new ResponseEntity<List<AccountDTO>>(accountDTOs, HttpStatus.OK);
	    }
	 
	 
	
	@GetMapping("/accounts")
	public ResponseEntity<?> getAccounts(@RequestParam(required = false) Integer id) throws AccountNotFoundException, FundTransferException {
		if(id!=null) {
			return new ResponseEntity<Account>(service.findAccount(id), HttpStatus.OK);

		}
		return new ResponseEntity<List<Account>>(service.getAllAccounts(), HttpStatus.OK);
	}
	
	@PutMapping("/accounts/{from}/{to}/{amount}")
	public ResponseEntity<?> fundTransfer(@PathVariable String from,@PathVariable String to,@PathVariable Double amount ) throws NumberFormatException, FundTransferException {
		
		
		return new ResponseEntity<String>(service.transferFunds(Integer.parseInt(from),Integer.parseInt(to),amount),HttpStatus.OK);
	}
	
	@DeleteMapping("/customers/{cid}/{aid}")
	public ResponseEntity<String> deleteAccountFromCustomer(@PathVariable Integer cid,@PathVariable Integer aid) throws CustomerNotFoundException, AccountNotFoundException {
		
		if(cid!=null && aid !=null) {
			return new ResponseEntity<String>(service.deleteAccountFromCustomer(cid, aid), HttpStatus.OK);
		}
		 return null;
	}
	
}
