package com.gal.coupon2.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.gal.coupon2.beans.Category;
import com.gal.coupon2.beans.Company;
import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.beans.Customer;
import com.gal.coupon2.beans.Image;
import com.gal.coupon2.exceptions.CouponSystemException;
import com.gal.coupon2.repos.CompanyRepository;
import com.gal.coupon2.repos.CouponRepository;
import com.gal.coupon2.repos.CustomerRepository;
import com.gal.coupon2.repos.ImageRepository;

import lombok.Setter;

@Service
@Scope("prototype")
@Setter
public class CompanyServiceImpl extends ClientService implements CompanyService {

	private int companyId;

	@Autowired
	private ImageRepository imageRepository;

	public CompanyServiceImpl(CompanyRepository companyRepository, CustomerRepository customerRepository,
			CouponRepository couponRepository) {
		super(companyRepository, customerRepository, couponRepository);
	}

	@Override
	public boolean login(String email, String password) {
		return isCompanyExists(email, password);
	}

	@Override
	public void addCoupon(Coupon coupon) throws CouponSystemException {
		Company company = getCompanyDetails();
		if (coupon.getCompanyId() != companyId) {
			throw new CouponSystemException(
					"Cannot add coupon of anther company - CompanyId must be of the current company");
		}
		for (Coupon c : company.getCoupons()) {
			if (c.getTitle().equalsIgnoreCase(coupon.getTitle())) {
				throw new CouponSystemException("Cannot add coupon - Coupon title already exists");
			}
		}
		couponRepository.save(coupon);
		company.getCoupons().add(coupon);
		companyRepository.saveAndFlush(company);

	}

	@Override
	public void updateCoupon(Coupon coupon) throws CouponSystemException {

		for (Coupon c : getCompanyDetails().getCoupons()) {
			if (c.getId() == coupon.getId()) {
				if (c.getCompanyId() == coupon.getCompanyId()) {
					couponRepository.saveAndFlush(coupon);
					return;
				}
				throw new CouponSystemException("Cannot update coupon - cannot change companyId");
			}
		}
		throw new CouponSystemException("Cannot update coupon - Id not exists");
	}

	@Override
	public void deleteCoupon(int couponId) throws CouponSystemException {
		Company company = getCompanyDetails();
		Coupon coupon = couponRepository.findById(couponId)
				.orElseThrow(() -> new CouponSystemException("Coupon Id not found: " + couponId));
		if (coupon.getCompanyId() != companyId) {
			throw new CouponSystemException("Cannot delete coupon of anther company");
		}
		company.getCoupons().remove(coupon);
		companyRepository.saveAndFlush(company); // This is so the companies_coupons table in the DB will be updated

		// this deletes the coupons from the customers coupon list so the
		// customers_coupons table in the DB will be updated:
		List<Customer> customers = customerRepository.findAll();
		for (Customer customer : customers) {
			if (customer.getCoupons().contains(coupon)) {
				customer.getCoupons().remove(coupon);
				customerRepository.saveAndFlush(customer);
			}
		}

		couponRepository.deleteById(couponId);
//		imageService.deleteImage(couponId); // the front-end also makes sure the image is deleted
		// from the image repository (so this command is not a must)
	}

	@Override
	public List<Coupon> getCompanyCoupons() {
		return getCompanyDetails().getCoupons();
	}

	@Override
	public List<Coupon> getCompanyCoupons(Category category) {
		List<Coupon> results = new ArrayList<>();
		for (Coupon c : getCompanyCoupons()) {
			if (c.getCategoryId().ordinal() == category.ordinal()) {
				results.add(c);
			}
		}
		return results;
	}

	@Override
	public List<Coupon> getCompanyCoupons(double maxPrice) {
		List<Coupon> results = new ArrayList<>();
		for (Coupon c : getCompanyCoupons()) {
			if (c.getPrice() <= maxPrice) {
				results.add(c);
			}
		}
		return results;
	}

	@Override
	public Company getCompanyDetails() {
		return companyRepository.getOne(companyId);
	}

	@Override
	public boolean isCompanyExists(String email, String password) {
		List<Company> companies = companyRepository.findByEmailAndPassword(email, password);
		if (companies.isEmpty()) {
			return false;
		}
		if (!companies.get(0).getPassword().equals(password)) {
			return false; // checks case sensitivity (the findBy... method is not case sensitive)
		}
		companyId = companies.get(0).getId();
		return true;
	}

	@Override
	public int getCouponIdByTitle(String title) {
		List<Coupon> coupons = this.couponRepository.findByTitle(title);
		if (coupons.isEmpty()) {
			return -1;
		}
		return coupons.get(0).getId();
	}

	@Override
	public List<Image> getCompanyImages() {
		return imageRepository.findByCompanyId(companyId);
	}

	@Override
	public void restore() throws CouponSystemException {
		if (getCompanyDetails().getName().equalsIgnoreCase("coca cola")) {
			Coupon co1 = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS).title("6 pack cans")
					.description("$1 off").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(4.99)
					.image("http://image.com").build();
			Coupon co2 = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS)
					.title("Coke Life - 64oz bottle").description("64oz for $1.79!")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(1)))
					.amount(100).price(1.79).image("http://image.com").build();
			Coupon co6 = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY).title("Ice Machine")
					.description("Vintage").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(19.99)
					.image("http://image.com").build();
			Coupon co12 = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS).title("12 pack cans")
					.description("Dr. Pepper").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(5.99)
					.image("http://image.com").build();
			addCouponIfNotExisted(co1);
			addCouponIfNotExisted(co2);
			addCouponIfNotExisted(co6);
			addCouponIfNotExisted(co12);
		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("walmart@gmail.com")) {
			Coupon fries = Coupon.builder().companyId(companyId).categoryId(Category.FOOD).title("Frozen Fries")
					.description("30% off").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(3.49)
					.image("http://image.com").build();
			Coupon heater = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY).title("Heater")
					.description("30% off").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(29.99)
					.image("http://image.com").build();
			addCouponIfNotExisted(fries);
			addCouponIfNotExisted(heater);
		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("netflix@gmail.com")) {
			Coupon co4 = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT).title("20% Off")
					.description("holiday sale").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(1.49)
					.image("http://image.com").build();
			Coupon co13 = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("1 months pass").description("promo").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(7.99)
					.image("http://image.com").build();
			addCouponIfNotExisted(co4);
			addCouponIfNotExisted(co13);
		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("cvs@gmail.com")) {
			Coupon co5 = Coupon.builder().companyId(companyId).categoryId(Category.HEALTH).title("Hand Cream")
					.description("50% off").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(1.99)
					.image("http://image.com").build();
			Coupon co7 = Coupon.builder().companyId(companyId).categoryId(Category.FOOD).title("Cough drops")
					.description("35% off").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(19.99)
					.image("http://image.com").build();
			addCouponIfNotExisted(co5);
			addCouponIfNotExisted(co7);
		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("target@gmail.com")) {
			Coupon co8 = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS).title("2LTR Soda")
					.description("50% off").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(2))).amount(100).price(0.99)
					.image("http://image.com").build();

			Coupon co9 = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY).title("Coffee Machine")
					.description("philips").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(29.99)
					.image("http://image.com").build();

			Coupon co10 = Coupon.builder().companyId(companyId).categoryId(Category.HEALTH).title("Advil Tablets")
					.description("30 of 200mg").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(5.99)
					.image("http://image.com").build();
			Coupon co14 = Coupon.builder().companyId(companyId).categoryId(Category.FOOD).title("Lays Chips")
					.description("10 oz").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(2))).amount(100).price(2.49)
					.image("http://image.com").build();
			addCouponIfNotExisted(co8);
			addCouponIfNotExisted(co9);
			addCouponIfNotExisted(co10);
			addCouponIfNotExisted(co14);
		}

	}

	public void addCouponIfNotExisted(Coupon coupon) {
		Company company = getCompanyDetails();
		for (Coupon c : company.getCoupons()) {
			if (c.getTitle().equalsIgnoreCase(coupon.getTitle())) {
				return;
			}
		}

		couponRepository.save(coupon);
		company.getCoupons().add(coupon);
		companyRepository.saveAndFlush(company);
	}

}
