package com.gal.coupon2.services;

import java.util.List;

import com.gal.coupon2.beans.Category;
import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.beans.Customer;
import com.gal.coupon2.beans.Image;
import com.gal.coupon2.beans.WelcomeCoupons;
import com.gal.coupon2.exceptions.CouponSystemException;

public interface CustomerService {
	void purchaseCoupon(Coupon coupon) throws CouponSystemException;

	List<Coupon> getCustomerCoupons();

	List<Coupon> getCustomerCoupons(Category category);

	List<Coupon> getCustomerCoupons(double maxPrice);

	Customer getCustomerDetails();

	boolean isCustomerExists(String email, String password);

	List<Coupon> getAllCoupons();

	List<Image> getCustomerImages();

	List<Image> getAllImages();

	List<Image> getAllOtherImages();

	WelcomeCoupons getMaxSixWelcomeCoupons() throws CouponSystemException;

	int getNumOfImages();

	Image getOneImage(int couponId);

	Coupon getOneCoupon(int couponId) throws CouponSystemException;

}
