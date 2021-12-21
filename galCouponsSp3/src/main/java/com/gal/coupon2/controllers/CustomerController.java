package com.gal.coupon2.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gal.coupon2.beans.Category;
import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.beans.Customer;
import com.gal.coupon2.exceptions.CouponSysSecurityException;
import com.gal.coupon2.exceptions.CouponSystemException;
import com.gal.coupon2.login.ClientType;
import com.gal.coupon2.security.TokenManager;
import com.gal.coupon2.services.AdminService;
import com.gal.coupon2.services.ClientService;
import com.gal.coupon2.services.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("customer") // http://localhost:8080/customer
@RequiredArgsConstructor
//@CrossOrigin(value = "http://localhost:4200")
public class CustomerController {

	private final AdminService adminService; // used for updateCustomer, getOneCompany methods
	private final TokenManager tokenManager;

	@PostMapping("purchaseCoupon")
	public ResponseEntity<?> purchaseCoupon(@RequestHeader(name = "Authorization") String token,
			@RequestBody Coupon coupon) throws CouponSysSecurityException, CouponSystemException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.CUSTOMER);
		((CustomerService) clientService).purchaseCoupon(coupon);
		return new ResponseEntity<>(HttpStatus.CREATED); // 201
	}

	@GetMapping("getCustomerCoupons")
	public ResponseEntity<?> getCustomerCoupons(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.CUSTOMER);
		return new ResponseEntity<>(((CustomerService) clientService).getCustomerCoupons(), HttpStatus.OK); // 200
	}

	@GetMapping("getCustomerCoupons/{category}")
	public ResponseEntity<?> getCustomerCoupons(@RequestHeader(name = "Authorization") String token,
			@PathVariable Category category) throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.CUSTOMER);
		return new ResponseEntity<>(((CustomerService) clientService).getCustomerCoupons(category), HttpStatus.OK); // 200
	}

	@GetMapping("getCustomerCoupons/upTo/{maxPrice}")
	public ResponseEntity<?> getCustomerCoupons(@RequestHeader(name = "Authorization") String token,
			@PathVariable double maxPrice) throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.CUSTOMER);
		return new ResponseEntity<>(((CustomerService) clientService).getCustomerCoupons(maxPrice), HttpStatus.OK); // 200
	}

	@GetMapping("getCustomerDetails")
	public ResponseEntity<?> getCustomerDetails(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.CUSTOMER);
		return new ResponseEntity<>(((CustomerService) clientService).getCustomerDetails(), HttpStatus.OK); // 200
	}

	@PutMapping("updateCustomer")
	public ResponseEntity<?> updateCustomer(@RequestHeader(name = "Authorization") String token,
			@RequestBody Customer customer) throws CouponSystemException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.CUSTOMER);
		adminService.updateCustomer(customer);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
	}

	@GetMapping("getOneCompany/{companyId}")
	public ResponseEntity<?> getOneCompany(@RequestHeader(name = "Authorization") String token,
			@PathVariable int companyId) throws CouponSystemException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.CUSTOMER);
		return new ResponseEntity<>(adminService.getOneCompany(companyId), HttpStatus.OK); // 200
	}

	@GetMapping("getAllCoupons")
	public ResponseEntity<?> getAllCoupons(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.CUSTOMER);
		return new ResponseEntity<>(((CustomerService) clientService).getAllCoupons(), HttpStatus.OK); // 200
	}

	@GetMapping("getCustomerImages")
	public ResponseEntity<?> getCustomerImages(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.CUSTOMER);
		return new ResponseEntity<>(((CustomerService) clientService).getCustomerImages(), HttpStatus.OK); // 200
	}

	@GetMapping("getAllImages")
	public ResponseEntity<?> getAllImages(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.CUSTOMER);
		return new ResponseEntity<>(((CustomerService) clientService).getAllImages(), HttpStatus.OK); // 200
	}

	@GetMapping("getAllOtherImages")
	public ResponseEntity<?> getAllOtherImages(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.CUSTOMER);
		return new ResponseEntity<>(((CustomerService) clientService).getAllOtherImages(), HttpStatus.OK); // 200
	}

}
