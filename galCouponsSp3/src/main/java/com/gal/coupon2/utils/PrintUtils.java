package com.gal.coupon2.utils;

public class PrintUtils {
	
	public static void companyTitle() {
		System.out.println("id      name             email                  password       coupons  ");
		System.out.println("--      ----             -----                  --------       -------  ");
	}
	
	public static void customerTitle() {
		System.out.println("id        firstName       lastName        email                 password          coupons  ");
		System.out.println("--        ---------       --------        -----                 --------          -------  ");
	}
	
	public static void couponTitle() {
		System.out.println("id       companyId      category            title               description      startDate         endDate          amount      price       image ");
		System.out.println("--       ---------      --------            -----               -----------      ---------         -------          ------      -----       ----- ");
	}

}
