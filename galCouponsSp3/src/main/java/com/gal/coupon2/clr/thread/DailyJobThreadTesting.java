package com.gal.coupon2.clr.thread;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.repos.CompanyRepository;
import com.gal.coupon2.repos.CouponRepository;
import com.gal.coupon2.repos.CustomerRepository;
import com.gal.coupon2.utils.ArtUtils;
import com.gal.coupon2.utils.PrintUtils;
import com.gal.coupon2.utils.TestUtils;

import lombok.RequiredArgsConstructor;

//@Component	// UnComment this line (@Component) if you want to test the thread that deleted the expired coupons.
@Order(6) // If you do that, then go to this thread class (CouponExpirationDailyJob
			// class),
@RequiredArgsConstructor // and schedule the thread to work every 10 seconds instead of every 24 hours so
							// you can see it in action.

public class DailyJobThreadTesting implements CommandLineRunner {

	private final TestUtils testUtils;
	private final CouponRepository couponRepository;
	private final CustomerRepository customerRepository;
	private final CompanyRepository companyRepository;

	@Override
	public void run(String... args) throws Exception {
		System.out.println(ArtUtils.DAILY_JOB_THREAD_6);

		testUtils.printTest(
				"Get all coupons before the thread does its job (the expired coupons are printed in red color)");
		PrintUtils.couponTitle();

		List<Coupon> coupons = couponRepository.findAll();
		for (Coupon coupon : coupons) {
			if (coupon.getEndDate().before(Date.valueOf(LocalDate.now()))) {
				System.err.println(coupon);
			} else {
				System.out.println(coupon);
			}
		}

		System.out.println(
				"\nFor these testings we set the thread to do its job every 10 seconds instead of every 24 hours:");
		System.out.println(
				"\nNow we let the main thread sleep for 15 seconds so the next time we print the coupon table will be after the Task thread did its job\n");

		System.out.println(Thread.currentThread().getName() + " thread is going to sleep\n");
		Thread.sleep(1000 * 15);
		System.out.println(Thread.currentThread().getName() + " thread woke up\n");

		testUtils.printTest("Get all coupons after the thread has done its job");
		PrintUtils.couponTitle();
		couponRepository.findAll().forEach(System.out::println);

		testUtils.printTest(
				"Get all companies after the thread has done its job (coupons were deleted from companies' coupons list)");
		PrintUtils.companyTitle();
		companyRepository.findAll().forEach(System.out::println);

		testUtils.printTest(
				"Get all customers after the thread has done its job (coupons were deleted from customers' coupons list)");
		PrintUtils.customerTitle();
		customerRepository.findAll().forEach(System.out::println);

		System.out.println(
				"\ncompanies_coupons table and customers_coupons table in the DB were also updated and the expired coupons were deleted\n");

	}

}
