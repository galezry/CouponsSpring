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
			Coupon co1 = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS).title("6-Pack Vanilla Soda")
					.description("Coca-Cola Vanilla Soda 6 cans / 7.5 fl oz. Enjoy this unique"
							+ " Coca-Cola’s crisp, delicious taste with meals, on the go, or to "
							+ "share. Serve ice cold for maximum refreshment. Perfect size for "
							+ "drinking with meals, on the go, or any time.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(35)))
					.amount(70).price(3.67).image("http://image.com").build();
			Coupon co2 = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS)
					.title("Coca-Cola Life - 20 fl oz")
					.description("Nothing compares to the refreshing, crisp taste of Coca-Cola Life, "
							+ "the delicious soda you know and love with a twist. Coca-Cola Life makes "
							+ "life’s special moments a little bit better, with cane sugar and stevia "
							+ "leaf extract.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(1)))
					.amount(100).price(1.88).image("http://image.com").build();
			Coupon co6 = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY).title("Snow Cone Maker")
					.description("Nostalgia SCM550COKE Coca-Cola Countertop Snow Cone Maker, "
							+ "Makes 20 Icy Treats, White/Red. The stainless steel cutting blades "
							+ "transform regular ice cubes into frozen treats that everybody loves. "
							+ "Assembled Product Dimensions: 12.00 x 11.00 x 15.25 Inches.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(40)))
					.amount(100).price(49.99).image("http://image.com").build();
			Coupon co12 = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS).title("12 Pack Cans")
					.description("12 Fl Oz, 12 Pack Cans of Coca-Cola Original Taste. 34 mg of "
							+ "caffeine in each 12 oz serving. This sparkling beverage is best "
							+ "enjoyed ice-cold for maximum refreshment.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(40)))
					.amount(100).price(5.92).image("http://image.com").build();
			Coupon cherry = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS)
					.title("12 Pack Cherry Coke Cans")
					.description("Coca-Cola Cherry Soda Pop, 12 Fl Oz, 12 Pack Cans. A little flavor "
							+ "can make a lot of magic happen and Coca-Cola Cherry is here to make your "
							+ "taste buds happy. 34 mg of caffeine in each 12 oz serving. Best enjoyed "
							+ "ice-cold for maximum refreshment.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(40)))
					.amount(100).price(5.92).image("http://image.com").build();
			Coupon co15 = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY)
					.title("Coca-Cola Clock")
					.description("Bring a classic diner look to your kitchen or dining room with this Coca-Cola"
							+ " Chrome-Finish Clock. This 12-inch clock features the Coca-Cola logo in bright"
							+ " white on a red backdrop with a high-gloss chrome molded case"
							+ " for a timeless contrast.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(20)))
					.amount(50).price(15.99).image("http://image.com").build();
			Coupon co16 = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("Pranzo Lunch Tote")
					.description("The Coca-Cola Pranzo is an insulated lunch box that features isolated"
							+ " sections so you can separate your hot and cold food and drink items. "
							+ "Made of polyester with a storage capacity of 200 square inches, the Pranzo"
							+ " also features a hidden flatware section.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(20)))
					.amount(50).price(59.95).image("http://image.com").build();
			Coupon fridge = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY)
					.title("Portable Fridge")
					.description("Classic Coca-Cola 4 Liter, 6 Can Portable Fridge, Mini Cooler "
							+ "for Food, Beverages, Skincare - Use at Home, Office, Dorm, Car, Boat "
							+ "- AC & DC Plugs Included. Cold mode keeps items cool to 32F (18C) and "
							+ "hot mode keeps items warm up to 135F (57C).")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(30)))
					.amount(50).price(29.49).image("http://image.com").build();

			addCouponIfNotExisted(co1);
			addCouponIfNotExisted(co2);
			addCouponIfNotExisted(co6);
			addCouponIfNotExisted(co12);
			addCouponIfNotExisted(cherry);
			addCouponIfNotExisted(co15);
			addCouponIfNotExisted(co16);
			addCouponIfNotExisted(fridge);
		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("walmart@gmail.com")) {
			Coupon fries = Coupon.builder().companyId(companyId).categoryId(Category.FOOD)
					.title("Ore-Ida Fries - 32 oz")
					.description("Ore-Ida Golden Crinkles French Fried Frozen Potatoes, 32 oz Bag. "
							+ "Get this great brand of fries for this amazing price. Made from the "
							+ "highest quality Grade A potatoes grown in the U.S.  Gluten free. "
							+ "Free with this coupon on any Walmart in store purchase.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(40)))
					.amount(100).price(3.12).image("http://image.com").build();
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
			Coupon chocolate = Coupon.builder().companyId(companyId).categoryId(Category.FOOD)
					.title("Mixed Chocolate Candy Bar")
					.description("Minis Party Size - 135 Piece Bag. Featuring SNICKERS, TWIX, 3 MUSKETEERS, "
							+ "MILKY WAY Original and MILKY WAY Midnight Candy, this 40-ounce bag of MARS "
							+ "Chocolate Bars is a perfect way to always have a gift, surprise treat or "
							+ "chocolate snack on hand.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(39)))
					.amount(80).price(10.98).image("http://image.com").build();
			Coupon mnm = Coupon.builder().companyId(companyId).categoryId(Category.FOOD).title("M&M's Family Size Bag")
					.description("M&M'S Peanut Milk Chocolate Candy, 19.2-ounce bag - Family Size. Enjoy "
							+ "this colorful candy made with real peanuts and milk chocolate and surrounded "
							+ "by a candy shell. Peanut M&M'S Candy is a favorite movie candy to share with "
							+ "family and friends.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(41)))
					.amount(100).price(5.98).image("http://image.com").build();
			addCouponIfNotExisted(fries);
			addCouponIfNotExisted(heater);
			addCouponIfNotExisted(printerpix);
			addCouponIfNotExisted(watch);
			addCouponIfNotExisted(chocolate);
			addCouponIfNotExisted(mnm);
		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("netflix@gmail.com")) {
			Coupon co4 = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("Holiday Sale - 20% Off")
					.description("Get 20% off any monthly plan using this coupon. Limited time offer")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(1)))
					.amount(100).price(1.49).image("http://image.com").build();
			Coupon co13 = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("One Month Pass")
					.description("New subscriber? Get one free month when joining any plan for at least one year")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(20)))
					.amount(50).price(7.99).image("http://image.com").build();
			Coupon co17 = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("Up To 20% Off Any Plan").description("Get up to 20% off on any plan using this coupon.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(20)))
					.amount(50).price(1.89).image("http://image.com").build();

			addCouponIfNotExisted(co4);
			addCouponIfNotExisted(co13);
			addCouponIfNotExisted(co17);

		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("cvs@gmail.com")) {
			Coupon co5 = Coupon.builder().companyId(companyId).categoryId(Category.HEALTH).title("Hand Cream (3 Pack)")
					.description("CVS Healthy Hands Hand & Nail Care Lotion 3.25 Fl Oz (3 Pack). "
							+ "Designed with essential ingredients like alpha hydroxy, keratin and "
							+ "vitamin E that are specific to your total hand and nail needs. "
							+ "It strengthens nails, softens cuticles and hydrates the skin.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(40)))
					.amount(60).price(7.93).image("http://image.com").build();
			Coupon co7 = Coupon.builder().companyId(companyId).categoryId(Category.HEALTH).title("Halls Cough Drops")
					.description("Special offer - Hall’s Cough Drops 25-30 ct (regularly $2.79)!")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(35)))
					.amount(100).price(2.09).image("http://image.com").build();
			Coupon vaccine = Coupon.builder().companyId(companyId).categoryId(Category.HEALTH)
					.title("$5 Off $20 Purchase")
					.description("Get any vaccine including Covid-19 vaccine or a flu shot and "
							+ "get $5 off your purchase of $20 or more - In store offer. Go get "
							+ "your vaccine today and enjoy this great offer!")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(35)))
					.amount(150).price(0.1).image("http://image.com").build();
			Coupon soda = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS).title("3 12-pk Soda Cans")
					.description("Get 3 (three) 12 pk, 12 fl oz Cans or 8 pk 12 fl oz Bottles: "
							+ "Select Varieties with Additional $10 Purchase (Regularly $5.99 each)!")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(30)))
					.amount(80).price(11).image("http://image.com").build();

			addCouponIfNotExisted(co5);
			addCouponIfNotExisted(co7);
			addCouponIfNotExisted(vaccine);
			addCouponIfNotExisted(soda);
		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("target@gmail.com")) {
			Coupon co8 = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS).title("2 Liters Soda")
					.description("2 liter bottle. Pick from a variety of soda beverages: "
							+ "Coca Cola, Dr Pepper, Sprite, Fanta, Pepsi, 7UP and more. "
							+ "Enjoy your favorite drink with any meal. Perfect Pairing: "
							+ "Pizza, tacos or salad, It is the perfect companion for your " + "next meal.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(35)))
					.amount(100).price(1.49).image("http://image.com").build();

			Coupon co9 = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY)
					.title("Keurig Coffee Maker")
					.description("The Keurig K-Mini single serve coffee maker features a sleek design "
							+ "with matte finish, and at less than 5 inches wide is the perfect size "
							+ "for any space or occasion. Add fresh water, pop in your favorite K-Cup "
							+ "pod, press the brew button and enjoy!")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(40)))
					.amount(75).price(69.99).image("http://image.com").build();

			Coupon co10 = Coupon.builder().companyId(companyId).categoryId(Category.HEALTH)
					.title("Advil Tablets - Ibuprofen (NSAID)")
					.description("Pain Reliever/Fever Reducer - Ibuprofen (NSAID). 24 Tablets; "
							+ "Active Ingredient Strength Value: 200 Milligrams; Age: 12 Years and Up; "
							+ "Aspirin-Free, Contains Pain Reliever, Caffeine-Free. Primary Active "
							+ "Ingredient: Ibuprofen; Warning: Contains ibuprofen.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(48)))
					.amount(100).price(4.49).image("http://image.com").build();
			Coupon co14 = Coupon.builder().companyId(companyId).categoryId(Category.FOOD).title("Lays Chips 8oz")
					.description("Lay's Classic Potato Chips - 8oz. Farm-grown potatoes seasoned with "
							+ "just the right amount of salt. Made with no artificial flavors, "
							+ "no preservatives, and are Gluten Free. Only 3 Ingredients: Potatoes, "
							+ "Vegetable Oil, and Salt.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(36)))
					.amount(100).price(2.79).image("http://image.com").build();
			Coupon dvd = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("Knives Out (DVD)")
					.description("Enjoy a movie night at home with your loved one with this great "
							+ "offer from Target. DVD Movie. A detective investigates the death of a "
							+ "patriarch of an eccentric, combative family. Genre: Comedy / Crime/ Drama; "
							+ "Rating: Pg-13; 130 minutes.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(38)))
					.amount(75).price(5.96).image("http://image.com").build();
			addCouponIfNotExisted(co8);
			addCouponIfNotExisted(co9);
			addCouponIfNotExisted(co10);
			addCouponIfNotExisted(co14);
			addCouponIfNotExisted(dvd);

		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("pepsi@gmail.com")) {
			Coupon coup = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("Pepsi Can Koozie")
					.description("12 oz Can and Bottle Holders. Neoprene foam hat provides good"
							+ " thermal and moisture insulation. It is resistant to ozone, sunlight,"
							+ " and oxidation. Most importantly Keep  drinks Cool.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(15)))
					.amount(100).price(5.99).image("http://image.com").build();
			Coupon koozie = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("Collapsible Koozie")
					.description("The Pepsi Collapsible KOOZIE Can cooler folds quickly so you can take "
							+ "it anywhere! Materials: Polyester with Foam Backing. "
							+ "Approx. Size: 3-7/8″w x 5-1/4″h x 1/8″d.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(35)))
					.amount(100).price(1.99).image("http://image.com").build();
			Coupon fridge = Coupon.builder().companyId(companyId).categoryId(Category.ELECTRICITY)
					.title("Portable 6-can Mini Fridge")
					.description("Store snacks, beverages, and skin care products. Fits easily anywhere "
							+ "- Your desk, bookshelf etc. Holds 6 cans or 4 liters. Dependable "
							+ "thermoelectric cooling; quiet and vibration free. Plugs into a 12V "
							+ "car adapter or home outlet, both cords are included.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(38)))
					.amount(65).price(39.9).image("http://image.com").build();
			Coupon bottles = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS)
					.title("6 Pack Bottles, 16.9 Fl Oz")
					.description("Pepsi Cola Soda Pop, 16.9 Fl Oz, 6 Pack Bottles. Formulated to be "
							+ "refreshing and crisp. Enjoy the bold, refreshing, robust cola over "
							+ "ice, or with a twist of lemon or lime. For a classic treat, try serving "
							+ "with a scoop of vanilla ice cream!")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(33)))
					.amount(100).price(3.88).image("http://image.com").build();
			Coupon cans = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS)
					.title("24 Pack Cans, 12 Fl Oz")
					.description("Pepsi Cola Soda Pop, 12 Fl Oz, 24 Pack Cans. The bold, refreshing "
							+ "cola born in New Bern, NC in 1898 and still bottled in the USA. Ideal "
							+ "for parties, meals and celebrations. 150 calories per can. No fat, no "
							+ "cholesterol and  low sodium. Recyclable cans.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(38)))
					.amount(80).price(8.48).image("http://image.com").build();
			Coupon mango = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS)
					.title("6 Pack Mango Cola Soda, 16.9 Fl Oz")
					.description("Pepsi Mango Cola Soda Pop, 16.9 Fl Oz, 6 Pack Bottles. Pepsi with a Splash "
							+ "of Mango Juice with Other Natural Flavors. Enjoy this unique and tasty flavor "
							+ "from Pepsi!")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(33)))
					.amount(70).price(3.88).image("http://image.com").build();
			Coupon zero = Coupon.builder().companyId(companyId).categoryId(Category.DRINKS)
					.title("Zero Sugar 6 Pack Bottles")
					.description("Pepsi Zero Sugar Cola Soda Pop, 16.9 Fl Oz, 6 Pack Bottles. Pepsi Zero "
							+ "Sugar has arrived and it's exactly what it says it is: A bold and refreshing "
							+ "zero calorie cola. Enjoy maximum Pepsi taste with zero sugar!")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(33)))
					.amount(70).price(3.88).image("http://image.com").build();
			addCouponIfNotExisted(coup);
			addCouponIfNotExisted(koozie);
			addCouponIfNotExisted(fridge);
			addCouponIfNotExisted(bottles);
			addCouponIfNotExisted(cans);
			addCouponIfNotExisted(mango);
			addCouponIfNotExisted(zero);
		}
		if (getCompanyDetails().getEmail().equalsIgnoreCase("amc@gmail.com")) {
			Coupon mix = Coupon.builder().companyId(companyId).categoryId(Category.FOOD)
					.title("The SING 2 All-Star Snack Pack")
					.description("Enough for you and your own all-star cast! Available for a limited time, "
							+ "the SING 2 All-Star Snack Pack contains a large popcorn, 2 candies, and 2 "
							+ "large fountain or ICEE drinks.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(16)))
					.amount(70).price(22.99).image("http://image.com").build();
			Coupon cokePop = Coupon.builder().companyId(companyId).categoryId(Category.FOOD)
					.title("Coke & Small Popcorn")
					.description("Get cheap eats at the concession stand at AMC Theaters. "
							+ "Use this coupon to get a $5 deal on a 20-ounce Coke and a "
							+ "small popcorn at participating classic and dine-in locations. "
							+ "Print the offer or show it on your smartphone.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(24)))
					.amount(80).price(5).image("http://image.com").build();
			Coupon silver = Coupon.builder().companyId(companyId).categoryId(Category.ENTERTAINMENT)
					.title("Silver Experience Movie Ticket")
					.description("Get to the movies on a discount! The tickets do not expire. "
							+ "Use on movies at any date/time. Use on movies after 2 weekends of "
							+ "release date. Good at any AMC in the US, excluding 3D, IMAX®, ETX, "
							+ "alternative content, and premium services and locations.")
					.startDate(Date.valueOf(LocalDate.now())).endDate(Date.valueOf(LocalDate.now().plusWeeks(100)))
					.amount(100).price(7.97).image("http://image.com").build();
			addCouponIfNotExisted(mix);
			addCouponIfNotExisted(cokePop);
			addCouponIfNotExisted(silver);
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
