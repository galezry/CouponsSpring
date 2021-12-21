package com.gal.coupon2.services;

import java.util.List;

import com.gal.coupon2.beans.Company;
import com.gal.coupon2.beans.Customer;
import com.gal.coupon2.exceptions.CouponSystemException;

public interface AdminService {

	void addCompany(Company company) throws CouponSystemException;

	void updateCompany(Company company) throws CouponSystemException;

	void deleteCompany(int companyId) throws CouponSystemException;

	List<Company> getAllCompanies();

	Company getOneCompany(int companyId) throws CouponSystemException;

	void addCustomer(Customer customer) throws CouponSystemException;

	void updateCustomer(Customer customer) throws CouponSystemException;

	void deleteCustomer(int customerId) throws CouponSystemException;

	List<Customer> getAllCustomers();

	Customer getOneCustomer(int customerId) throws CouponSystemException;

	void restore();

}
