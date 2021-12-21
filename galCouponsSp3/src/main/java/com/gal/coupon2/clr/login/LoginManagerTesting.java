package com.gal.coupon2.clr.login;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.gal.coupon2.beans.Category;
import com.gal.coupon2.beans.Company;
import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.login.ClientType;
import com.gal.coupon2.login.LoginManager;
import com.gal.coupon2.services.AdminService;
import com.gal.coupon2.services.CompanyService;
import com.gal.coupon2.services.CustomerService;
import com.gal.coupon2.utils.ArtUtils;
import com.gal.coupon2.utils.PrintUtils;
import com.gal.coupon2.utils.TestUtils;

import lombok.RequiredArgsConstructor;

//@Component     //  the LoginManager was checked in the WelcomeController and that is checked by Swagger - no need to check it here.
				// So this testing is NOT relevant for project 3
@Order(5) 
@RequiredArgsConstructor
public class LoginManagerTesting implements CommandLineRunner {

	private final LoginManager loginManager;
	private final TestUtils testUtils;
	
	
	private final AdminService adminService;
	private final CompanyService companyService1;    // in project 3 the validation happens in the welcome controller and was checked by swagger,
	private final CompanyService companyService6;	 // so here I just initiation 2 companies and 2 customers - to avoid compiler Errors 
	private final CustomerService customerService3;	 // in project 2 we could see the companies and customers returned by LoginManager
	private final CustomerService customerService5;	  
	
	
	
	
	
	@Override
	public void run(String... args) throws Exception {
		
		//--------------------------------  Admin Login  ------------------------------------------------------------
		
		System.out.println(ArtUtils.LOGIN_MANAGER_5);
		testUtils.printTest("Login as Admin fails");
		try {
			loginManager.login("admin@admin.com", "aDmin", ClientType.ADMINISTRATOR);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		testUtils.printTest("Login as Admin correct");
//		AdminService adminService = (AdminService)loginManager.login("admin@admin.com", "admin", ClientType.ADMINISTRATOR); //<= project 2
//		the login was checked by Swagger - no need to check it here.
		System.out.println("successfull login as admin"); 
		
		testUtils.printTest("Show abilities");
		PrintUtils.companyTitle();
		adminService.getAllCompanies().forEach(System.out::println);
		
		
		Coupon coup = Coupon.builder()
				.companyId(7)
				.categoryId(Category.ELECTRICITY)
				.title("Vacuum Cleaner")
				.description("20% off")
				.startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1)))
				.amount(100)
				.price(99.99)
				.image("http://image.com")
				.build();
		
		Company comp = Company.builder()
				.name("Home Depot")
				.email("homed@gmail.com")
				.password("1234")
				.coupon(coup)
				.build();
		
		testUtils.printTest("Add company - 'Home Depot with one coupon'");
		adminService.addCompany(comp);
		PrintUtils.companyTitle();
		adminService.getAllCompanies().forEach(System.out::println);
		
		//--------------------------------  Company #1 Login  ------------------------------------------------------------
		
		testUtils.printTest("Login as Company fails");
		try {
			loginManager.login("cola@gmail.com", "1235", ClientType.COMPANY);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		testUtils.printTest("Login as Company correct: company #1");
//		CompanyService companyService1 = (CompanyService)loginManager.login("cola@gmail.com", "1234", ClientType.COMPANY); <= project 2
//		the login was checked by Swagger - no need to check it here.
		System.out.println("successfull login as company #1");
		
		testUtils.printTest("Show abilities");
		PrintUtils.companyTitle();
		System.out.println(companyService1.getCompanyDetails() + "\n"); 
		
		PrintUtils.couponTitle();
		companyService1.getCompanyCoupons().forEach(System.out::println);
		
		
		//--------------------------------  Company #6 Login  ------------------------------------------------------------
		
		testUtils.printTest("Login as Company correct: company #6");
//		CompanyService companyService6 = (CompanyService)loginManager.login("pepsi@gmail.com", "1234", ClientType.COMPANY); <= project 2
//		the login was checked by Swagger - no need to check it here.
		System.out.println("successfull login as company #6");
		
		testUtils.printTest("Show abilities");
		PrintUtils.companyTitle();
		System.out.println(companyService6.getCompanyDetails() + "\n"); 
		
		Coupon coupPepsi = Coupon.builder()
				.companyId(6)
				.categoryId(Category.DRINKS)
				.title("7 Up")
				.description("64 oz")
				.startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1)))
				.amount(100)
				.price(0.99)
				.image("http://image.com")
				.build();
		
		testUtils.printTest("Add coupon - '7 Up'");
		companyService6.addCoupon(coupPepsi);
		PrintUtils.companyTitle();
		System.out.println(companyService6.getCompanyDetails()); 
		
		testUtils.printTest("Get company coupons");
		PrintUtils.couponTitle();
		companyService6.getCompanyCoupons().forEach(System.out::println);
		
		//--------------------------------  Company #1 again  ------------------------------------------------------------
		
		testUtils.printTest("Company #1 is already logged in and asks to Get company coupons");
		PrintUtils.couponTitle();
		companyService1.getCompanyCoupons().forEach(System.out::println);
		
		
		//--------------------------------  Customer #3 Login  ------------------------------------------------------------
		
		testUtils.printTest("Login as Customer fails");
		try {
			loginManager.login("kimLopez@gmail.com", "1234", ClientType.CUSTOMER);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		testUtils.printTest("Login as Customer correct - customer #3");
//		CustomerService customerService3 = (CustomerService)loginManager.login("kim@gmail.com", "1234", ClientType.CUSTOMER); <= project 2
//		the login was checked by Swagger - no need to check it here.
		System.out.println("successfull login as customer #3"); 
		
		testUtils.printTest("Show abilities");
		PrintUtils.customerTitle();
		System.out.println(customerService3.getCustomerDetails() + "\n");    
		
		PrintUtils.couponTitle();
		customerService3.getCustomerCoupons().forEach(System.out::println);
		
		testUtils.printTest("Purchase coupon - '7 Up coupon'");
		customerService3.purchaseCoupon(coupPepsi);
		
		PrintUtils.couponTitle();
		customerService3.getCustomerCoupons().forEach(System.out::println);
		
		
		//--------------------------------  Customer #5 Login  ------------------------------------------------------------
		
		testUtils.printTest("Login as Customer fails (customer #5 tries to login as a company)");
		try {
			loginManager.login("tom@gmail.com", "1234", ClientType.COMPANY);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		testUtils.printTest("Login as Customer correct - customer #5");
//		CustomerService customerService5 = (CustomerService)loginManager.login("tom@gmail.com", "1234", ClientType.CUSTOMER); <= project 2
//		the login was checked by Swagger - no need to check it here.
		System.out.println("successfull login as customer #5"); 
		
		testUtils.printTest("Show abilities");
		PrintUtils.customerTitle();
		System.out.println(customerService5.getCustomerDetails() + "\n");
		
		PrintUtils.couponTitle();
		customerService5.getCustomerCoupons().forEach(System.out::println);
		
		testUtils.printTest("Customer #5 purchases coupon - 'vacuum cleaner'");
		customerService5.purchaseCoupon(coup);
		
		PrintUtils.couponTitle();
		customerService5.getCustomerCoupons().forEach(System.out::println);
		
		testUtils.printTest("Same customer tries to Purchase the same coupon - 'vacuum cleaner'");
		try {
			customerService5.purchaseCoupon(coup);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
		testUtils.printTest("Get all customer's coupons - same as before");
		
		PrintUtils.couponTitle();
		customerService5.getCustomerCoupons().forEach(System.out::println);
		
		
		
		//--------------------------------  Customer #3  ------------------------------------------------------------
		
		testUtils.printTest("Customer #3 is already logged in and asks to Get her details");
		PrintUtils.customerTitle();
		System.out.println(customerService3.getCustomerDetails()); 
		
		testUtils.printTest("Customer #3 asks to Get her coupons:");
		PrintUtils.couponTitle();
		customerService3.getCustomerCoupons().forEach(System.out::println);
		
		//--------------------------------  Customer #5  ------------------------------------------------------------
		
		testUtils.printTest("Customer #5 is already logged in and asks to Get his details");
		PrintUtils.customerTitle();
		System.out.println(customerService5.getCustomerDetails()); 
		
		
		
		
		
	}
	
	
	
	

}
