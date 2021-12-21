package com.gal.coupon2.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.gal.coupon2.exceptions.CouponSysSecurityException;
import com.gal.coupon2.exceptions.CouponSystemException;


//is doing AOP to the web layer - AOP for spring controller 
// it's like a proxy for the controllers - business interception
// single responsibility - the annotation is doing interception to all the unsuccessful http requests. 

@RestController
@ControllerAdvice
public class CouponSysControllerAdvice {
	@ExceptionHandler(CouponSystemException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorDetails handleErrors(Exception e) {
		return new ErrorDetails("Coupon System Error", e.getMessage(), 400);
	}
	
	@ExceptionHandler(CouponSysSecurityException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ErrorDetails handleErrors2(Exception e) {
		return new ErrorDetails("Security Error", e.getMessage(), 401);
	}
	
	

}
