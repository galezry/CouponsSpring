package com.gal.coupon2.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gal.coupon2.beans.Company;
import com.gal.coupon2.beans.Customer;
import com.gal.coupon2.exceptions.CouponSysSecurityException;
import com.gal.coupon2.exceptions.CouponSystemException;
import com.gal.coupon2.login.ClientType;
import com.gal.coupon2.security.TokenManager;
import com.gal.coupon2.services.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("admin") // http://localhost:8080/admin
@RequiredArgsConstructor
//@CrossOrigin(value = "http://localhost:4200")
@CrossOrigin(value = "https://gal-coupons.herokuapp.com")
public class AdminController {

	private final AdminService adminService;
	private final TokenManager tokenManager;

	@PostMapping("addCompany")
	public ResponseEntity<?> addCompany(@RequestHeader(name = "Authorization") String token,
			@RequestBody Company company) throws CouponSystemException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.ADMINISTRATOR);

		adminService.addCompany(company);
		return new ResponseEntity<>(HttpStatus.CREATED); // 201
	}

	@PutMapping("updateCompany")
	public ResponseEntity<?> updateCompany(@RequestHeader(name = "Authorization") String token,
			@RequestBody Company company) throws CouponSystemException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.ADMINISTRATOR);
		adminService.updateCompany(company);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
	}

	@DeleteMapping("deleteCompany/{companyId}")
	public ResponseEntity<?> deleteCompany(@RequestHeader(name = "Authorization") String token,
			@PathVariable int companyId) throws CouponSystemException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.ADMINISTRATOR);
		adminService.deleteCompany(companyId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
	}

	@GetMapping("getAllCompanies") // GET - http://localhost:8080/admin/getAllCompanies
	public ResponseEntity<?> getAllCompanies(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.ADMINISTRATOR);
		return new ResponseEntity<>(adminService.getAllCompanies(), HttpStatus.OK); // 200
	}

	@GetMapping("getOneCompany/{companyId}")
	public ResponseEntity<?> getOneCompany(@RequestHeader(name = "Authorization") String token,
			@PathVariable int companyId) throws CouponSystemException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.ADMINISTRATOR);
		return new ResponseEntity<>(adminService.getOneCompany(companyId), HttpStatus.OK); // 200
	}

	@PostMapping("addCustomer")
	public ResponseEntity<?> addCustomer(@RequestHeader(name = "Authorization") String token,
			@RequestBody Customer customer) throws CouponSystemException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.ADMINISTRATOR);
		adminService.addCustomer(customer);
		return new ResponseEntity<>(HttpStatus.CREATED); // 201
	}

	@PutMapping("updateCustomer")
	public ResponseEntity<?> updateCustomer(@RequestHeader(name = "Authorization") String token,
			@RequestBody Customer customer) throws CouponSystemException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.ADMINISTRATOR);
		adminService.updateCustomer(customer);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
	}

	@DeleteMapping("deleteCustomer/{customerId}")
	public ResponseEntity<?> deleteCustomer(@RequestHeader(name = "Authorization") String token,
			@PathVariable int customerId) throws CouponSysSecurityException, CouponSystemException {
		tokenManager.checkValidation(token, ClientType.ADMINISTRATOR);
		adminService.deleteCustomer(customerId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
	}

	@GetMapping("getAllCustomers")
	public ResponseEntity<?> getAllCustomers(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.ADMINISTRATOR);
		return new ResponseEntity<>(adminService.getAllCustomers(), HttpStatus.OK); // 200
	}

	@GetMapping("getOneCustomer/{customerId}")
	public ResponseEntity<?> getOneCustomer(@RequestHeader(name = "Authorization") String token,
			@PathVariable int customerId) throws CouponSystemException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.ADMINISTRATOR);
		return new ResponseEntity<>(adminService.getOneCustomer(customerId), HttpStatus.OK); // 200
	}

	@PostMapping("restore")
	public ResponseEntity<?> restore(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.ADMINISTRATOR);
		adminService.restore();
		return new ResponseEntity<>(HttpStatus.CREATED); // 201
	}

}
