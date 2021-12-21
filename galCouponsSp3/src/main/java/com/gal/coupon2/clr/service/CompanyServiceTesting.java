package com.gal.coupon2.clr.service;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import com.gal.coupon2.beans.Category;
import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.repos.CouponRepository;
import com.gal.coupon2.repos.CustomerRepository;
import com.gal.coupon2.services.ClientService;
import com.gal.coupon2.services.CompanyService;
import com.gal.coupon2.utils.ArtUtils;
import com.gal.coupon2.utils.PrintUtils;
import com.gal.coupon2.utils.TestUtils;

import lombok.RequiredArgsConstructor;

//@Component  // This testing is NOT relevant for production - DB no drop-create method but update
@Order(3)
@RequiredArgsConstructor
public class CompanyServiceTesting implements CommandLineRunner {

	private final CompanyService companyService;
	private final CouponRepository couponRepository; // this is just for getting coupons from the DB - And Not to
														// perform Company testings
	private final CustomerRepository customerRepository; // same as above
	private final TestUtils testUtils;

	@Override
	public void run(String... args) throws Exception {
		System.out.println(ArtUtils.COMPANY_SERVICE_3);

		testUtils.printTest("login - bad credentials");
		System.err.println(((ClientService) companyService).login("d", "t"));

		testUtils.printTest("login - good credentials");
		System.out.println(((ClientService) companyService).login("cola@gmail.com", "1234ab!"));

		testUtils.printTest("Add Coupon - exist - title: '6 pack cans' ");

		Coupon toAdd = Coupon.builder().companyId(1).categoryId(Category.ELECTRICITY).title("6 pack cans")
				.description("35% off").startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(49.99).image("http://image.com")
				.build();

		try {
			companyService.addCoupon(toAdd);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Add Coupon - Correct - title: 'Mini Fridge'");
		toAdd.setTitle("Mini Fridge");
		companyService.addCoupon(toAdd);

		PrintUtils.couponTitle();
		companyService.getCompanyCoupons().forEach(System.out::println);

		testUtils.printTest("Update Coupon - Id not exist");
		Coupon toUpdate = couponRepository.getOne(1);

		toUpdate.setId(50);
		try {
			companyService.updateCoupon(toUpdate);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Update Coupon - Id exist but companyId is changed");
		toUpdate.setId(1);
		toUpdate.setCompanyId(50);
		try {
			companyService.updateCoupon(toUpdate);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Update Coupon - Correct: Updated price of coupon#1 to 6.89");
		toUpdate.setCompanyId(1);

		toUpdate.setPrice(6.89);
		companyService.updateCoupon(toUpdate);

		PrintUtils.couponTitle();
		companyService.getCompanyCoupons().forEach(System.out::println);

		testUtils.printTest("Delete Coupon - #12");
		companyService.deleteCoupon(12);

		System.out.println(
				"Here are the customers table and the coupons table in the DB (no longer contain coupon #12):\n");
		PrintUtils.customerTitle();
		customerRepository.findAll().forEach(System.out::println);
		System.out.println();
		PrintUtils.couponTitle();
		couponRepository.findAll().forEach(System.out::println);

		System.out.println(
				"\nThe coupon was also deleted from the companies_coupons table and from the customers_coupons table is the DB");

		testUtils.printTest("Get all Coupons of this company");
		PrintUtils.couponTitle();
		companyService.getCompanyCoupons().forEach(System.out::println);

		testUtils.printTest("Get all Coupons of a specific category: DRINKS");
		PrintUtils.couponTitle();
		companyService.getCompanyCoupons(Category.DRINKS).forEach(System.out::println);

		testUtils.printTest("Get all Coupons with a price up to a maxPrice: 25");
		PrintUtils.couponTitle();
		companyService.getCompanyCoupons(25).forEach(System.out::println);

		testUtils.printTest("Get company details");
		PrintUtils.companyTitle();
		System.out.println(companyService.getCompanyDetails());

	}

}
