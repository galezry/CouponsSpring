package com.gal.coupon2.services;

import org.springframework.stereotype.Service;

import com.gal.coupon2.repos.CompanyRepository;
import com.gal.coupon2.repos.CouponRepository;
import com.gal.coupon2.repos.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public abstract class ClientService {
	
	protected final CompanyRepository companyRepository;
	protected final CustomerRepository customerRepository;
	protected final CouponRepository couponRepository;
	
	
	public abstract boolean login(String email, String password); 
}
