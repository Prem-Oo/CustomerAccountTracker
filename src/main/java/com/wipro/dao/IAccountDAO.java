package com.wipro.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wipro.bean.Account;

public interface IAccountDAO extends JpaRepository<Account, Integer> {
	@Query("SELECT a FROM Account a JOIN FETCH a.customer")
	List<Account> findAllWithCustomers();
}
