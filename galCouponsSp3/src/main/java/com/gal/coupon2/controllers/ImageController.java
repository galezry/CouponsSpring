package com.gal.coupon2.controllers;

import java.io.IOException;

import javax.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gal.coupon2.beans.Image;
import com.gal.coupon2.exceptions.CouponSysSecurityException;
import com.gal.coupon2.login.ClientType;
import com.gal.coupon2.security.TokenManager;
import com.gal.coupon2.services.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
//@CrossOrigin(value = "http://localhost:4200")
@CrossOrigin(value = "https://gal-coupons.herokuapp.com")
@RequestMapping("image")
@RequiredArgsConstructor
public class ImageController {

	private final ImageService imageService;
	private final TokenManager tokenManager;

	@PostMapping("addImage/{couponId}/{companyId}")
	public ResponseEntity<?> addImage(@RequestHeader(name = "Authorization") String token,
			@RequestParam("couponImg") MultipartFile file, @PathVariable int couponId, @PathVariable int companyId)
			throws IOException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.COMPANY);
		Image img = new Image(file.getOriginalFilename(), file.getContentType(), file.getBytes(), couponId, companyId);
		imageService.addImage(img);
		return new ResponseEntity<>(HttpStatus.CREATED); // 201
	}

	@PutMapping("updateImage/{couponId}/{companyId}")
	public ResponseEntity<?> updateImage(@RequestHeader(name = "Authorization") String token,
			@RequestParam("couponImg") MultipartFile file, @PathVariable int couponId, @PathVariable int companyId)
			throws IOException, CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.COMPANY);
		Image img = new Image(file.getOriginalFilename(), file.getContentType(), file.getBytes(), couponId, companyId);
		imageService.updateImage(img);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
	}

	@GetMapping("getOneImage/{couponId}")
	public ResponseEntity<?> getOneImage(@RequestHeader(name = "Authorization") String token,
			@PathVariable int couponId) throws CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.COMPANY);
		if (imageService.doesImageExist(couponId)) {
			return new ResponseEntity<>(imageService.getOneImage(couponId), HttpStatus.OK); // 200
		}
		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
	}

	@GetMapping("getCompanyImages/{companyId}")
	public ResponseEntity<?> getCompanyImages(@RequestHeader(name = "Authorization") String token,
			@PathVariable int companyId) throws CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.COMPANY);
		return new ResponseEntity<>(imageService.getCompanyImages(companyId), HttpStatus.OK); // 200
	}

	// change it and get all coupons in the welcome controller so won't need token
	// change get all coupons from customer controller to welcome controller
	@GetMapping("getAllImages")
	public ResponseEntity<?> getAllImages() throws CouponSysSecurityException {
		return new ResponseEntity<>(imageService.getAllImages(), HttpStatus.OK); // 200
	}

//	@GetMapping("getOneImage2")
//	public ResponseEntity<?> getOneImage2(@RequestHeader(name = "Authorization") String token,
//			@RequestHeader(name = "couponId") Integer couponId) throws CouponSysSecurityException {
//		tokenManager.checkValidation(token, ClientType.COMPANY);
//		if (imageService.doesImageExist(couponId)) {
//			return new ResponseEntity<>(imageService.getOneImage(couponId), HttpStatus.OK); // 200
//		}
//		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
//	}

	@DeleteMapping("deleteImage/{couponId}")
	@Transactional
	public ResponseEntity<?> deleteImage(@RequestHeader(name = "Authorization") String token,
			@PathVariable int couponId) throws CouponSysSecurityException {
		tokenManager.checkValidation(token, ClientType.COMPANY);
		imageService.deleteImage(couponId);

		return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
	}

}
