package com.gal.coupon2.clr.primary;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

import com.gal.coupon2.beans.Category;
import com.gal.coupon2.beans.Company;
import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.beans.Customer;
import com.gal.coupon2.repos.CompanyRepository;
import com.gal.coupon2.repos.CouponRepository;
import com.gal.coupon2.repos.CustomerRepository;
import com.gal.coupon2.utils.ArtUtils;
import com.gal.coupon2.utils.PrintUtils;
import com.gal.coupon2.utils.TestUtils;

import lombok.RequiredArgsConstructor;

// @Component  // This testing is NOT relevant for production - DB no drop-create method but update
@Order(1)
@RequiredArgsConstructor

public class Bootstrap implements CommandLineRunner {

	private final CompanyRepository companyRepository;
	private final CustomerRepository customerRepository;
	private final CouponRepository couponRepository;

	private final TestUtils testUtils;

	@Override
	public void run(String... args) throws Exception {

		System.out.println(ArtUtils.BOOTSTRAP_1);
		testUtils.printTest("Init Companies");

		Company c1 = Company.builder().name("Coca Cola").email("cola@gmail.com").password("1234ab!").build();

		Company c2 = Company.builder().name("Walmart").email("walmart@gmail.com").password("1234ab!").build();

		Company c3 = Company.builder().name("Netflix").email("netflix@gmail.com").password("1234ab!").build();

		Company c4 = Company.builder().name("CVS").email("cvs@gmail.com").password("1234ab!").build();

		Company c5 = Company.builder().name("Target").email("target@gmail.com").password("1234ab!").build();

		companyRepository.saveAll(Arrays.asList(c1, c2, c3, c4, c5));
		PrintUtils.companyTitle();
		companyRepository.findAll().forEach(System.out::println);

		testUtils.printTest("Init Customers");

		Customer cu1 = Customer.builder().firstName("Dave").lastName("Smith").email("dave@gmail.com")
				.password("1234ab@").build();

		Customer cu2 = Customer.builder().firstName("John").lastName("Doe").email("john@gmail.com").password("1234ab@")
				.build();

		Customer cu3 = Customer.builder().firstName("Kim").lastName("Lopez").email("kim@gmail.com").password("1234ab@")
				.build();

		Customer cu4 = Customer.builder().firstName("Russ").lastName("Johns").email("russ@gmail.com")
				.password("1234ab@").build();

		Customer cu5 = Customer.builder().firstName("Tom").lastName("Brown").email("tom@gmail.com").password("1234ab@")
				.build();

		customerRepository.saveAll(Arrays.asList(cu1, cu2, cu3, cu4, cu5));
		PrintUtils.customerTitle();
		customerRepository.findAll().forEach(System.out::println);

		testUtils.printTest("Init Coupons");

		Coupon co1 = Coupon.builder().companyId(1).categoryId(Category.DRINKS).title("6 pack cans")
				.description("$1 off").startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(4.99).image("http://image.com")
				.build();

		// https://drive.google.com/file/d/18gZdiSCCOcd2B_oPAEE4WWDY9LLxckjX/view?usp=sharing
		Coupon co2 = Coupon.builder().companyId(1).categoryId(Category.DRINKS).title("Coke Life - 64oz bottle")
				.description("64oz for $1.79!").startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(1.79).image("http://image.com")
				.build();
		// https://drive.google.com/file/d/17TqFuKYy1PGGjmIm26ctwwqxsklJl2y-/view
		// https://www.target.com/p/coca-cola-12pk-12-fl-oz-cans/-/A-12953464#
		Coupon co3 = Coupon.builder().companyId(2).categoryId(Category.FOOD).title("Frozen Fries")
				.description("30% off").startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(3.49).image("http://image.com")
				.build();

		Coupon co4 = Coupon.builder().companyId(3).categoryId(Category.ENTERTAINMENT).title("20% Off")
				.description("holiday sale").startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(1.49).image("http://image.com")
				.build();

//https://drive.google.com/file/d/1n6M-vgIZXidmI-53tOJ70te93EJzhdQG/view?usp=sharing
		Coupon co5 = Coupon.builder().companyId(4).categoryId(Category.HEALTH).title("Hand Cream")
				.description("50% off").startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(1.99).image("http://image.com")
				.build();
//
		// https://drive.google.com/file/d/1BOcqG1DDmgfiniAyyHJhUv0_oVnDu8Xm/view?usp=sharing
		Coupon co6 = Coupon.builder().companyId(1).categoryId(Category.ELECTRICITY).title("Ice Machine")
				.description("Vintage").startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(19.99).image("http://image.com")
				.build();
//https://drive.google.com/file/d/1s09nXlsxIK7Sr6xcjH-1AwEc1RqOsu69/view?usp=sharing
		Coupon co7 = Coupon.builder().companyId(4).categoryId(Category.FOOD).title("Cough drops").description("35% off")
				.startDate(Date.valueOf(LocalDate.now().minusWeeks(2)))
				.endDate(Date.valueOf(LocalDate.now().minusWeeks(1))).amount(100).price(19.99).image("http://image.com")
				.build();
		// https://drive.google.com/file/d/1HqUIW9Ob9QSb75TcWKdRBFof7y7Vr-cB/view?usp=sharing
		Coupon co8 = Coupon.builder().companyId(5).categoryId(Category.DRINKS).title("2LTR Soda").description("50% off")
				.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(2)))
				.amount(100).price(0.99).image("http://image.com").build();
//
		// https://drive.google.com/file/d/16cwwaCoaS4NALailKgE8yNoE4Wy8gXKj/view?usp=sharing
		Coupon co9 = Coupon.builder().companyId(5).categoryId(Category.ELECTRICITY).title("Coffee Machine")
				.description("philips").startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(0).price(29.99).image("http://image.com")
				.build();
//https://drive.google.com/file/d/1uhB3MRceFBk_EubHpkweWLoNPW3Ggwk6/view?usp=sharing
		Coupon co10 = Coupon.builder().companyId(5).categoryId(Category.HEALTH).title("Advil Tablets")
				.description("30 of 200mg").startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(5.99).image("http://image.com")
				.build();

		// https://drive.google.com/file/d/18y6-5n8pPgnifyIj4l58aPZYilgIMsT8/view?usp=sharing

		Coupon co11 = Coupon.builder().companyId(2).categoryId(Category.ELECTRICITY).title("Heater")
				.description("30% off").startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(29.99).image("http://image.com")
				.build();

		Coupon co12 = Coupon.builder().companyId(1).categoryId(Category.DRINKS).title("12 pack cans")
				.description("Dr. Pepper").startDate(Date.valueOf(LocalDate.now()))
				.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(5.99).image("http://image.com")
				.build();

		Coupon co13 = Coupon.builder().companyId(3).categoryId(Category.ENTERTAINMENT).title("1 months pass")
				.description("promo").startDate(Date.valueOf(LocalDate.now().minusWeeks(4)))
				.endDate(Date.valueOf(LocalDate.now().minusWeeks(3))).amount(100).price(7.99).image("http://image.com")
				.build();

		Coupon co14 = Coupon.builder().companyId(5).categoryId(Category.FOOD).title("Lays Chips").description("10 oz")
				.startDate(Date.valueOf(LocalDate.now().minusWeeks(3)))
				.endDate(Date.valueOf(LocalDate.now().minusWeeks(2))).amount(100).price(2.49).image("http://image.com")
				.build();

		couponRepository
				.saveAll(Arrays.asList(co1, co2, co3, co4, co5, co6, co7, co8, co9, co10, co11, co12, co13, co14));
		PrintUtils.couponTitle();
		couponRepository.findAll().forEach(System.out::println);

		testUtils.printTest("Attaching the Coupons to the companies");

		c1.setCoupons(Arrays.asList(co1, co2, co6, co12));
		c2.setCoupons(Arrays.asList(co3, co11));
		c3.setCoupons(Arrays.asList(co4, co13));
		c4.setCoupons(Arrays.asList(co5, co7));
		c5.setCoupons(Arrays.asList(co8, co9, co10, co14));

		companyRepository.saveAll(Arrays.asList(c1, c2, c3, c4, c5));
		PrintUtils.companyTitle();
		companyRepository.findAll().forEach(System.out::println);

		testUtils.printTest("Attaching the Coupons to the customers - Initialing the customers with coupons");

		cu1.setCoupons(Arrays.asList(co2, co4, co5, co12, co13));
		cu2.setCoupons(Arrays.asList(co1, co3, co5, co6, co11, co13));
		cu3.setCoupons(Arrays.asList(co2, co3, co5, co12, co14));
		cu4.setCoupons(Arrays.asList(co1, co4, co11, co12, co14));
		cu5.setCoupons(Arrays.asList(co6, co3, co12, co13, co14));

		customerRepository.saveAll((Arrays.asList(cu1, cu2, cu3, cu4, cu5)));
		PrintUtils.customerTitle();
		customerRepository.findAll().forEach(System.out::println);

		// This is not purchasing coupons by customers but just initializing - that's
		// why we need to set the coupons amount manually -
		co1.setAmount(98);
		co2.setAmount(98);
		co3.setAmount(97);
		co4.setAmount(98);
		co5.setAmount(97);
		co6.setAmount(98);
		co11.setAmount(98);
		co12.setAmount(96);
		co13.setAmount(97);
		co14.setAmount(97);

		couponRepository.saveAll(Arrays.asList(co1, co2, co3, co4, co5, co6, co11, co12, co13, co14));
		System.out.println("\n");
		PrintUtils.couponTitle();
		couponRepository.findAll().forEach(System.out::println);

	}

}
