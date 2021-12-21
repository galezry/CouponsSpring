package com.gal.coupon2.clr.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import com.gal.coupon2.beans.Category;
import com.gal.coupon2.beans.Company;
import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.repos.CompanyRepository;
import com.gal.coupon2.repos.CouponRepository;
import com.gal.coupon2.repos.CustomerRepository;
import com.gal.coupon2.services.AdminService;
import com.gal.coupon2.services.ClientService;
import com.gal.coupon2.services.CustomerService;
import com.gal.coupon2.utils.ArtUtils;
import com.gal.coupon2.utils.PrintUtils;
import com.gal.coupon2.utils.TestUtils;

import lombok.RequiredArgsConstructor;

//@Component  // This testing is NOT relevant for production - DB no drop-create method but update
@Order(4)
@RequiredArgsConstructor
public class CustomerServiceTesting implements CommandLineRunner {

	private final AdminService adminService; // just for deleting all companies and customers

	private final CompanyRepository companyRepository; // this is just for creating company Walmart again (was deleted
														// in previous test in adminService testing

	private final CustomerService customerService;
	private final CouponRepository couponRepository; // this is just for getting coupons from the DB and NOT for testing
														// customerService methods
	private final CustomerRepository customerRepository; // Same as above
	private final TestUtils testUtils;

	@Override
	public void run(String... args) throws Exception {
		System.out.println(ArtUtils.CUSTOMER_SERVICE_4);

		System.out.println(
				"\nHere are the customers table and the coupons table from the DB, for a refference to the following tests:\n");

		PrintUtils.customerTitle();
		customerRepository.findAll().forEach(System.out::println);
		System.out.println();
		PrintUtils.couponTitle();
		couponRepository.findAll().forEach(System.out::println);

		testUtils.printTest("login - bad credentials");
		System.err.println(((ClientService) customerService).login("d", "1234"));

		testUtils.printTest("login - good credentials (customer #1)");
		System.out.println(((ClientService) customerService).login("dave@gmail.com", "1234ab@"));

		testUtils.printTest("Purchase coupon - coupon was already purchased by this customer (coupon #2)");
		Coupon coup2 = couponRepository.getOne(2);
		try {
			customerService.purchaseCoupon(coup2);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Purchase coupon - coupon amount is zero (coupon #9)");

		Coupon coup9 = couponRepository.getOne(9);
		try {
			customerService.purchaseCoupon(coup9);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Purchase coupon - coupon has expired (coupon #7)");
		Coupon coup7 = couponRepository.getOne(7);
		try {
			customerService.purchaseCoupon(coup7);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Purchase coupon - Correct (coupon #8)");
		Coupon coup8 = couponRepository.getOne(8);

		customerService.purchaseCoupon(coup8);

		PrintUtils.customerTitle();
		customerRepository.findAll().forEach(System.out::println);
		System.out.println("\nWe can see that coupon #8 amount is down by 1 because of the purchase:\n");

		PrintUtils.couponTitle();
		couponRepository.findAll().forEach(System.out::println);

		testUtils.printTest("Get customer coupons");
		PrintUtils.couponTitle();
		customerService.getCustomerCoupons().forEach(System.out::println);

		testUtils.printTest("Get all this customer's coupons of a specific category: DRINKS");
		PrintUtils.couponTitle();
		customerService.getCustomerCoupons(Category.DRINKS).forEach(System.out::println);

		testUtils.printTest("Get all this customer's coupons with a price up to a maxPrice: 1.49");
		PrintUtils.couponTitle();
		customerService.getCustomerCoupons(1.49).forEach(System.out::println);

		testUtils.printTest("Get customer details");
		PrintUtils.customerTitle();
		System.out.println(customerService.getCustomerDetails());

		testUtils.printTest("Creating again company Walmart and its coupons (after previously deleted)");
		Company walmart = Company.builder().name("Walmart").email("walmart@gmail.com").password("1234ab!").build();
		companyRepository.save(walmart);
		Company walmartFromDB = companyRepository.findByEmailIgnoreCase("walmart@gmail.com");
		if (walmartFromDB != null) {
			Coupon fries = Coupon.builder().companyId(walmartFromDB.getId()).categoryId(Category.FOOD)
					.title("Frozen Fries").description("30% off").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(3.49)
					.image("http://image.com").build();
			Coupon heater = Coupon.builder().companyId(walmartFromDB.getId()).categoryId(Category.ELECTRICITY)
					.title("Heater").description("30% off").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(29.99)
					.image("http://image.com").build();
			couponRepository.saveAll(Arrays.asList(fries, heater));
			walmartFromDB.setCoupons(Arrays.asList(fries, heater));
			companyRepository.saveAndFlush(walmartFromDB);
			PrintUtils.companyTitle();
			companyRepository.findAll().forEach(System.out::println);
		}
		testUtils.printTest("Updating Pepsi password");
		Company pepsi = companyRepository.findByEmailIgnoreCase("pepsi@gmail.com");
		pepsi.setPassword("1234ab!");
		companyRepository.saveAndFlush(pepsi);
		PrintUtils.companyTitle();
		companyRepository.findAll().forEach(System.out::println);

		// for production mode - delete everything so on restart DB is continuing from
		// where it was
		// Not drop-create but update

		testUtils.printTest("Delete everything");

		testUtils.printTest("Delete all companies");
		adminService.deleteCompany(1);
		adminService.deleteCompany(7);
		adminService.deleteCompany(3);
		adminService.deleteCompany(4);
		adminService.deleteCompany(5);
		adminService.deleteCompany(6);
		PrintUtils.companyTitle();
		adminService.getAllCompanies().forEach(System.out::println);

		System.out.println("\nAnd the coupons were also deleted from the coupons table:\n");
		PrintUtils.couponTitle();
		couponRepository.findAll().forEach(System.out::println);

		System.out.println("\nAnd the coupons were also deleted from the coustomers table:\n");
		PrintUtils.customerTitle();
		customerRepository.findAll().forEach(System.out::println);

		System.out.println(
				"\nThe comapnies_coupons and customers_coupons tables were also updated in the DB - and now empty");

		testUtils.printTest("Delete All Customers");
		adminService.deleteCustomer(1);
		adminService.deleteCustomer(2);
		adminService.deleteCustomer(3);
		adminService.deleteCustomer(5);
		adminService.deleteCustomer(6);

		PrintUtils.customerTitle();
		adminService.getAllCustomers().forEach(System.out::println);
		System.out.println("\n The customers_coupons table in the DB was also updated - and now empty");

	}

}
