package com.gal.coupon2.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gal.coupon2.beans.Category;
import com.gal.coupon2.beans.Company;
import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.exceptions.CouponSysSecurityException;
import com.gal.coupon2.exceptions.CouponSystemException;
import com.gal.coupon2.login.ClientType;
import com.gal.coupon2.security.TokenManager;
import com.gal.coupon2.services.AdminService;
import com.gal.coupon2.services.ClientService;
import com.gal.coupon2.services.CompanyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("company") // http://localhost:8080/company
@RequiredArgsConstructor
//@CrossOrigin(value = "http://localhost:4200")
public class CompanyController {

	private final AdminService adminService; // used for updateCompany method
	private final TokenManager tokenManager;

	@PostMapping("addCoupon")
	public ResponseEntity<?> addCoupon(@RequestHeader(name = "Authorization") String token, @RequestBody Coupon coupon)
			throws CouponSystemException, CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.COMPANY);
		((CompanyService) clientService).addCoupon(coupon);
		return new ResponseEntity<>(HttpStatus.CREATED); // 201
	}

	@PutMapping("updateCoupon")
	public ResponseEntity<?> updateCoupon(@RequestHeader(name = "Authorization") String token,
			@RequestBody Coupon coupon) throws CouponSysSecurityException, CouponSystemException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.COMPANY);
		((CompanyService) clientService).updateCoupon(coupon);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
	}

	@DeleteMapping("deleteCoupon/{couponId}")
	public ResponseEntity<?> deleteCoupon(@RequestHeader(name = "Authorization") String token,
			@PathVariable int couponId) throws CouponSysSecurityException, CouponSystemException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.COMPANY);
		((CompanyService) clientService).deleteCoupon(couponId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
	}

	@GetMapping("getCompanyCoupons")
	public ResponseEntity<?> getCompanyCoupons(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.COMPANY);
		return new ResponseEntity<>(((CompanyService) clientService).getCompanyCoupons(), HttpStatus.OK); // 200
	}

	@GetMapping("getCompanyCoupons/{category}")
	public ResponseEntity<?> getCompanyCoupons(@RequestHeader(name = "Authorization") String token,
			@PathVariable Category category) throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.COMPANY);
		return new ResponseEntity<>(((CompanyService) clientService).getCompanyCoupons(category), HttpStatus.OK); // 200
	}

	@GetMapping("getCompanyCoupons/upTo/{maxPrice}")
	public ResponseEntity<?> getCompanyCoupons(@RequestHeader(name = "Authorization") String token,
			@PathVariable double maxPrice) throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.COMPANY);
		return new ResponseEntity<>(((CompanyService) clientService).getCompanyCoupons(maxPrice), HttpStatus.OK); // 200
	}

	@GetMapping("getCompanyDetails")
	public ResponseEntity<?> getCompanyDetails(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.COMPANY);
		return new ResponseEntity<>(((CompanyService) clientService).getCompanyDetails(), HttpStatus.OK); // 200
	}

	@PutMapping("updateCompany")
	public ResponseEntity<?> updateCompany(@RequestHeader(name = "Authorization") String token,
			@RequestBody Company company) throws CouponSystemException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.COMPANY);

		adminService.updateCompany(company);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
	}

	@GetMapping("getCouponIdByTitle")
	public ResponseEntity<?> getCouponIdByTitle(@RequestHeader(name = "Authorization") String token,
			@RequestHeader(name = "CouponTitle") String title) throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.COMPANY);
		return new ResponseEntity<>(((CompanyService) clientService).getCouponIdByTitle(title), HttpStatus.OK); // 200
	}

	@GetMapping("getCompanyImages")
	public ResponseEntity<?> getCompanyImages(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.COMPANY);
		return new ResponseEntity<>(((CompanyService) clientService).getCompanyImages(), HttpStatus.OK); // 200
	}

	@PostMapping("restore")
	public ResponseEntity<?> restore(@RequestHeader(name = "Authorization") String token)
			throws CouponSysSecurityException, CouponSystemException {
		ClientService clientService = tokenManager.checkValidation(token, ClientType.COMPANY);
		((CompanyService) clientService).restore();
		return new ResponseEntity<>(HttpStatus.CREATED); // 201
	}

}
