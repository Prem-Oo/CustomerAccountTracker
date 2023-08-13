package com.wipro.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wipro.bean.Customer;
public interface ICustomerDAO extends JpaRepository<Customer, Integer> {

}
