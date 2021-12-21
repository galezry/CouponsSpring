package com.gal.coupon2.services;

import java.util.List;

import com.gal.coupon2.beans.Category;
import com.gal.coupon2.beans.Company;
import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.beans.Image;
import com.gal.coupon2.exceptions.CouponSystemException;

public interface CompanyService {
	void addCoupon(Coupon coupon) throws CouponSystemException;

	void updateCoupon(Coupon coupon) throws CouponSystemException;

	void deleteCoupon(int couponId) throws CouponSystemException;

	List<Coupon> getCompanyCoupons();

	List<Coupon> getCompanyCoupons(Category category);

	List<Coupon> getCompanyCoupons(double maxPrice);

	Company getCompanyDetails();

	boolean isCompanyExists(String email, String password);

	int getCouponIdByTitle(String title);

	List<Image> getCompanyImages();

	void restore() throws CouponSystemException;

}
