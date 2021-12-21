package com.gal.coupon2.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gal.coupon2.beans.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	List<Customer> findByEmailAndPassword(String name, String password);

	List<Customer> findByEmailIgnoreCase(String email);

}
