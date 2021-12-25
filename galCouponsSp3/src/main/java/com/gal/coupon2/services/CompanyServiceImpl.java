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
			Coupon co15 = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY)
					.title("Coca-Cola Clock")
					.description("Bring a classic diner look to your kitchen or dining room with this Coca-Cola"
							+ " Chrome-Finish Clock. This 12-inch clock features the Coca-Cola logo in bright"
							+ " white on a red backdrop with a high-gloss chrome molded case"
							+ " for a timeless contrast.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(20)))
					.amount(50).price(15.99).image("http://image.com").build();
			Coupon co16 = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY)
					.title("Coca-Cola Pranzo Lunch Tote")
					.description("The Coca-Cola Pranzo is an insulated lunch box that features isolated"
							+ " sections so you can separate your hot and cold food and drink items. "
							+ "Made of polyester with a storage capacity of 200 square inches, the Pranzo"
							+ " also features a hidden flatware section.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(20)))
					.amount(50).price(59.95).image("http://image.com").build();

			addCouponIfNotExisted(co1);
			addCouponIfNotExisted(co2);
			addCouponIfNotExisted(co6);
			addCouponIfNotExisted(co12);
			addCouponIfNotExisted(co15);
			addCouponIfNotExisted(co16);
		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("walmart@gmail.com")) {
			Coupon fries = Coupon.builder().companyId(companyId).categoryId(Category.FOOD).title("Frozen Fries")
					.description("30% off").startDate(Date.valueOf(LocalDate.now()))
					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(3.49)
					.image("http://image.com").build();
			Coupon heater = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY)
					.title("Portable Electric Heater")
					.description("2021 Portable Electric Space Heater, fast heating for home & office. "
							+ "The advanced PTC ceramic heating element allows the heater to heat quickly. "
							+ "Provides temperature protection, power failure, and other technologies,"
							+ " making it more safe and reliable.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(15)))
					.amount(40).price(34.88).image("http://image.com").build();
			Coupon printerpix = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("Printerpix")
					.description("Personalized Heat Activated Magic Coffee Mug, Custom Image, 11oz. 80% Off! "
							+ "Design your personalized magic mug with text, logo, or photo; color-changing"
							+ " coffee mug reveals your chosen photo when the contents are warm.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(6)))
					.amount(100).price(4.99).image("http://image.com").build();
			Coupon watch = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY)
					.title("P25 Smart Watch")
					.description("P25 Smart Watch Fitness Tracker Pedometer IP68 Waterproof Sport Watch "
							+ "for Android and iOS Phones. 1.7inch Full Touch HD Display, 8 sports modes: "
							+ "Walking, Running, Cycling, Swimming, and more. Sleep Tracker, Friendly-skin "
							+ "silicon protect case.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(10)))
					.amount(60).price(22.99).image("http://image.com").build();
			addCouponIfNotExisted(fries);
			addCouponIfNotExisted(heater);
			addCouponIfNotExisted(printerpix);
			addCouponIfNotExisted(watch);
		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("netflix@gmail.com")) {
			Coupon co4 = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("Holiday Sale - 20% Off")
					.description("Get 20% off any monthly plan using this coupon. Limited time offer")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(1)))
					.amount(100).price(1.49).image("http://image.com").build();
			Coupon co13 = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT).title("1 month pass")
					.description("New subscriber? Get one free month when joining any plan for at least one year")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(20)))
					.amount(50).price(7.99).image("http://image.com").build();
			Coupon co17 = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("Up to 20% off any plan").description("Get up to 20% off on any plan using this coupon")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(20)))
					.amount(50).price(1.89).image("http://image.com").build();

			addCouponIfNotExisted(co4);
			addCouponIfNotExisted(co13);
			addCouponIfNotExisted(co17);

		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("cvs@gmail.com")) {
			Coupon co5 = Coupon.builder().companyId(companyId).categoryId(Category.HEALTH).title("Hand Cream (3 Pack)")
					.description(
							"CVS Healthy Hands Hand & Nail Care Lotion 3.25 Fl Oz (3 Pack). Designed with essential ingredients like alpha hydroxy, keratin and vitamin E that are specific to your total hand and nail needs. It strengthens nails, softens cuticles and immediately hydrate")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(1)))
					.amount(100).price(1.99).image("http://image.com").build();
			Coupon co7 = Coupon.builder().companyId(companyId).categoryId(Category.HEALTH).title("Halls Cough drops")
					.description("Special offer - Hall’s Cough Drops 25-30 ct - "
							+ "free with this coupon  (regularly $2.79)!")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(20)))
					.amount(100).price(2.09).image("http://image.com").build();
			try {
				addCouponIfNotExisted(co5);
			} catch (Exception e) {
				throw new CouponSystemException("too mant characters");
			}

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
		if (getCompanyDetails().getEmail().equalsIgnoreCase("pepsi@gmail.com")) {
			Coupon coup = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS).title("Pepsi Can Koozies")
					.description("12 oz Can and Bottle Holders. Neoprene foam hat provides good"
							+ " thermal and moisture insulation. It is resistant to ozone, sunlight,"
							+ " and oxidation. Most importantly Keep  drinks Cool.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(15)))
					.amount(100).price(5.99).image("http://image.com").build();
			addCouponIfNotExisted(coup);
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
