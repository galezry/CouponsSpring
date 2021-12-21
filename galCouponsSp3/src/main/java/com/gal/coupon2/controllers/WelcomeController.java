package com.gal.coupon2.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gal.coupon2.beans.Company;
import com.gal.coupon2.beans.Customer;
import com.gal.coupon2.exceptions.CouponSysSecurityException;
import com.gal.coupon2.exceptions.CouponSystemException;
import com.gal.coupon2.login.ClientType;
import com.gal.coupon2.login.LoginManager;
import com.gal.coupon2.security.InfoLogin;
import com.gal.coupon2.security.LoginResponse;
import com.gal.coupon2.security.TokenManager;
import com.gal.coupon2.services.AdminService;
import com.gal.coupon2.services.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("welcome")
@RequiredArgsConstructor
//@CrossOrigin(value = "http://localhost:4200")
@CrossOrigin(value = "https://gal-coupons.herokuapp.com")
public class WelcomeController {

	private final LoginManager loginManager;
	private final TokenManager tokenManager;
	private final AdminService adminService; // used for customerSignUp and companySignUp and getOneCompany
	private final CustomerService customerService; // for findMaxSixWelcomeCoupons

//	@PostMapping("login/{type}")
//	public ResponseEntity<?> login(@RequestBody InfoLogin infoLogin, @PathVariable ClientType type)
//			throws CouponSysSecurityException {
//		String token;
//		token = loginManager.login(infoLogin.getEmail(), infoLogin.getPassword(), type);
//		return new ResponseEntity<>(new LoginResponse(token), HttpStatus.CREATED); // 201
//	}

	@PostMapping("login")
	public ResponseEntity<?> login(@RequestBody InfoLogin infoLogin) throws CouponSysSecurityException {
		String token;
		token = loginManager.login(infoLogin.getEmail(), infoLogin.getPassword(), infoLogin.getType());
		return new ResponseEntity<>(new LoginResponse(token), HttpStatus.CREATED); // 201
	}

	@DeleteMapping("logout")
	public ResponseEntity<?> logout(@RequestHeader(name = "Authorization") String token) {

		if (tokenManager.isExist(token)) {
			tokenManager.getMap().remove(token);
		}
//		return new ResponseEntity<>("You are now disconnected. Thank You and Goodbye", HttpStatus.OK); // 200
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 We can't send a String to the front-end
	}

	@GetMapping("checkIfLoggedIn/{clientType}")
	public ResponseEntity<?> checkIfLoggedIn(@RequestHeader(name = "Authorization") String token,
			@PathVariable ClientType clientType) throws CouponSysSecurityException {
		tokenManager.checkValidation(token, clientType);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("customerSignUp")
	public ResponseEntity<?> customerSignUp(@RequestBody Customer customer) throws CouponSystemException {
		adminService.addCustomer(customer);
		return new ResponseEntity<>(HttpStatus.CREATED); // 201
	}

	@PostMapping("companySignUp")
	public ResponseEntity<?> companySignUp(@RequestBody Company company) throws CouponSystemException {
		adminService.addCompany(company);
		return new ResponseEntity<>(HttpStatus.CREATED); // 201
	}

	@GetMapping("getNumOfImages")
	public ResponseEntity<?> getNumOfImages() {
		return new ResponseEntity<>(customerService.getNumOfImages(), HttpStatus.OK); // 200
	}

	@GetMapping("getWelcomeCoupons")
	public ResponseEntity<?> getWelcomeCoupons() throws CouponSystemException {
		return new ResponseEntity<>(customerService.getMaxSixWelcomeCoupons(), HttpStatus.OK); // 200
	}

	@GetMapping("getOneCompany/{companyId}")
	public ResponseEntity<?> getOneCompany(@PathVariable int companyId) throws CouponSystemException {
		return new ResponseEntity<>(adminService.getOneCompany(companyId), HttpStatus.OK); // 200
	}

}
