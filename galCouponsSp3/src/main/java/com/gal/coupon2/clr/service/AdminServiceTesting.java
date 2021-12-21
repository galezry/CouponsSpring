package com.gal.coupon2.clr.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import com.gal.coupon2.beans.Company;
import com.gal.coupon2.beans.Customer;
import com.gal.coupon2.repos.CouponRepository;
import com.gal.coupon2.repos.CustomerRepository;
import com.gal.coupon2.services.AdminService;
import com.gal.coupon2.services.ClientService;
import com.gal.coupon2.utils.ArtUtils;
import com.gal.coupon2.utils.PrintUtils;
import com.gal.coupon2.utils.TestUtils;

import lombok.RequiredArgsConstructor;

//@Component // This testing is NOT relevant for production - DB no drop-create method but update
@Order(2)
@RequiredArgsConstructor
public class AdminServiceTesting implements CommandLineRunner {

	private final AdminService adminService;
	private final CustomerRepository customerRepository; // just for pulling data from the DB - not for the admin
															// testings
	private final CouponRepository couponRepository; // the same as above

	private final TestUtils testUtils;

	@Override
	public void run(String... args) throws Exception {
		System.out.println(ArtUtils.ADMIN_SERVICE_2);

		testUtils.printTest("login - bad credentials");
		System.err.println(((ClientService) adminService).login("d", "t"));

		testUtils.printTest("login - good credentials");
		System.out.println(((ClientService) adminService).login("admin@admin.com", "admin12#"));

		testUtils.printTest("Add Company - exist name");
		Company toAdd = Company.builder().name("Coca Cola").email("pepsi@gmail.com").password("1234").build();
		try {
			adminService.addCompany(toAdd);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Add Company - exist email");

		toAdd.setEmail("cola@gmail.com");
		toAdd.setName("Pepsi");
		try {
			adminService.addCompany(toAdd);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Add Company - Correct : Pepsi");

		toAdd.setEmail("pepsi@gmail.com");
		adminService.addCompany(toAdd);

		PrintUtils.companyTitle();
		adminService.getAllCompanies().forEach(System.out::println);

		testUtils.printTest("Update Company - ID not exist");
		Company toUpdate = adminService.getOneCompany(2);
		toUpdate.setId(50);
		try {
			adminService.updateCompany(toUpdate);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Update Company - changed name (not allowed)");
		toUpdate.setId(2);
		toUpdate.setName("Walmart and Co");
		try {
			adminService.updateCompany(toUpdate);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Update Company - Correct: password changed to 5678 ");
		toUpdate.setName("Walmart");
		toUpdate.setPassword("5678");
		adminService.updateCompany(toUpdate);
		PrintUtils.companyTitle();
		adminService.getAllCompanies().forEach(System.out::println);

		testUtils.printTest("Delete company #2");
		adminService.deleteCompany(2);
		PrintUtils.companyTitle();
		adminService.getAllCompanies().forEach(System.out::println);

		System.out.println("\nAnd the coupons (3,11) was also deleted from the coupons table:\n");
		PrintUtils.couponTitle();
		couponRepository.findAll().forEach(System.out::println);

		System.out.println("\nAnd the coupons (3,11) was also deleted from the coustomers table:\n");
		PrintUtils.customerTitle();
		customerRepository.findAll().forEach(System.out::println);

		System.out.println(
				"\nThe comapnies_coupons and customers_coupons tables were also updated in the DB - company 2 and coupons 3, 11 were all deleted");

		testUtils.printTest("Get all Companis");
		PrintUtils.companyTitle();
		adminService.getAllCompanies().forEach(System.out::println);

		testUtils.printTest("Get one Company : Id not exist");
		try {
			adminService.getOneCompany(50);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Get one Company - correct: company # 3");
		PrintUtils.companyTitle();
		System.out.println(adminService.getOneCompany(3));

//	------------------- Customer Tests ---------------------------------------------

		testUtils.printTest("Add Customer - exist email");
		Customer toAddCu = Customer.builder().firstName("Jeff").lastName("Sims").email("russ@gmail.com")
				.password("1234").build();
		try {
			adminService.addCustomer(toAddCu);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Add Customer - Correct : Jeff");
		toAddCu.setEmail("jeff@gmail.com");
		adminService.addCustomer(toAddCu);

		PrintUtils.companyTitle();
		PrintUtils.customerTitle();
		adminService.getAllCustomers().forEach(System.out::println);

		testUtils.printTest("Update Customer - ID not exist");
		Customer toUpdateCu = adminService.getOneCustomer(2);
		toUpdateCu.setId(50);
		try {
			adminService.updateCustomer(toUpdateCu);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Update Customer - Correct: password changed to 5678 ");
		toUpdateCu.setId(2);
		toUpdateCu.setPassword("5678");
		adminService.updateCustomer(toUpdateCu);

		PrintUtils.customerTitle();
		adminService.getAllCustomers().forEach(System.out::println);

		testUtils.printTest("Delete Customer: customer #4 ");
		adminService.deleteCustomer(4);

		PrintUtils.customerTitle();
		adminService.getAllCustomers().forEach(System.out::println);
		System.out.println("\n The customers_coupons table in the DB was also updated");

		testUtils.printTest("Get all Customers");
		PrintUtils.customerTitle();
		adminService.getAllCustomers().forEach(System.out::println);

		testUtils.printTest("Get one Customer : Id not exist");
		try {
			adminService.getOneCustomer(50);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		testUtils.printTest("Get one Customer - correct: Customer # 3");
		PrintUtils.customerTitle();
		System.out.println(adminService.getOneCustomer(3));

	}

}
