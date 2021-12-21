package com.gal.coupon2.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gal.coupon2.beans.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {
	List<Company> findByEmailAndPassword(String name, String password);

	Company findByNameIgnoreCase(String name);

	Company findByEmailIgnoreCase(String email);

}
