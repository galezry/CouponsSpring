package com.gal.coupon2.services;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.gal.coupon2.beans.Category;
import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.beans.Customer;
import com.gal.coupon2.beans.Image;
import com.gal.coupon2.beans.WelcomeCoupons;
import com.gal.coupon2.exceptions.CouponSystemException;
import com.gal.coupon2.repos.CompanyRepository;
import com.gal.coupon2.repos.CouponRepository;
import com.gal.coupon2.repos.CustomerRepository;
import com.gal.coupon2.repos.ImageRepository;

@Service
@Scope("prototype")
public class CustomerServiceImpl extends ClientService implements CustomerService {

	private int customerId;

	@Autowired
	private ImageRepository imageRepository;

	public CustomerServiceImpl(CompanyRepository companyRepository, CustomerRepository customerRepository,
			CouponRepository couponRepository) {
		super(companyRepository, customerRepository, couponRepository);
	}

	@Override
	public boolean login(String email, String password) {
		return isCustomerExists(email, password);
	}

	@Override
	public void purchaseCoupon(Coupon coupon) throws CouponSystemException {
		couponRepository.findById(coupon.getId())
				.orElseThrow(() -> new CouponSystemException("This coupon is not exist"));
		Customer customer = getCustomerDetails();
		for (Coupon c : customer.getCoupons()) {
			if (c.getId() == coupon.getId()) {
				throw new CouponSystemException("Cannot purchase coupon more than once - coupon was already purchased");
			}
		}
		if (coupon.getAmount() == 0) {
			throw new CouponSystemException("Cannot purchase coupon - Coupon amount is zero");
		}
		if (coupon.getEndDate().before(Date.valueOf(LocalDate.now()))) {
			throw new CouponSystemException("Cannot purchase coupon - Coupon is expired");
		}
		customer.getCoupons().add(coupon);
		customerRepository.saveAndFlush(customer);
		coupon.setAmount(coupon.getAmount() - 1);
		couponRepository.saveAndFlush(coupon);
	}

	@Override
	public List<Coupon> getCustomerCoupons() {
		return getCustomerDetails().getCoupons();
	}

	@Override
	public List<Coupon> getCustomerCoupons(Category category) {
		List<Coupon> results = new ArrayList<>();
		for (Coupon c : getCustomerCoupons()) {
			if (c.getCategoryId().ordinal() == category.ordinal()) {
				results.add(c);
			}
		}
		return results;
	}

	@Override
	public List<Coupon> getCustomerCoupons(double maxPrice) {
		List<Coupon> results = new ArrayList<>();
		for (Coupon c : getCustomerCoupons()) {
			if (c.getPrice() <= maxPrice) {
				results.add(c);
			}
		}
		return results;
	}

	@Override
	public Customer getCustomerDetails() {
		return customerRepository.getOne(customerId);
	}

	@Override
	public boolean isCustomerExists(String email, String password) {
		List<Customer> customers = customerRepository.findByEmailAndPassword(email, password);
		if (customers.isEmpty()) {
			return false;
		}
		if (!customers.get(0).getPassword().equals(password)) {
			return false; // checks case sensitivity (the findBy... method is not case sensitive)
		}
		customerId = customers.get(0).getId();
		return true;
	}

	@Override
	public List<Coupon> getAllCoupons() {
		return couponRepository.findAll();
	}

	@Override
	public List<Image> getCustomerImages() {
		List<Image> result = new ArrayList<>();
		for (Coupon coupon : getCustomerCoupons()) {
			List<Image> images = imageRepository.findByCouponId(coupon.getId());
			if (!images.isEmpty()) {
				result.add(images.get(0));
			}
		}
		return result;
	}

	@Override
	public List<Image> getAllImages() {
		return imageRepository.findAll();
	}

	// all other images that do not belong to customer's coupons (all images that
	// are not customer's images)
	@Override
	public List<Image> getAllOtherImages() {
		List<Image> allImages = getAllImages();
		List<Image> customerImages = getCustomerImages();
		for (Image image : customerImages) {
			if (allImages.contains(image)) {
				allImages.remove(image);
			}
		}
		return allImages;
	}

	@Override
	public WelcomeCoupons getMaxSixWelcomeCoupons() throws CouponSystemException {
		int imageListSize = imageRepository.findAll().size();

		List<Image> allImages = getAllImages();
		List<Integer> validCouponIds = new ArrayList<>();
		List<Image> validImages = new ArrayList<>();
		List<Coupon> validCoupons = new ArrayList<>();
		List<Image> images = new ArrayList<>();
		List<Coupon> coupons = new ArrayList<>();

		for (Image image : allImages) {
			Coupon coupon = getOneCoupon(image.getCouponId());
			if (coupon.getAmount() > 0 && !coupon.getEndDate().before(Date.valueOf(LocalDate.now()))) {

				validImages.add(image);
				validCoupons.add(coupon);
				validCouponIds.add(coupon.getId());
			}
			if (validCouponIds.size() > 6) {
				break;
			}
		}

		if (validCouponIds.size() <= 6) {
			for (int i = 0; i < validCouponIds.size(); i++) {
				if (validImages.get(i).getCouponId() == validCoupons.get(i).getId()) {
					images.add(validImages.get(i));
					coupons.add(validCoupons.get(i));
				}
			}

		} else {
			List<Integer> possibleNumbers = new ArrayList<>();
			for (int i = 0; i < imageListSize; i++) {
				possibleNumbers.add(i);
			}

			for (int i = 0; i < 6; i++) {
				int randIdx = (int) (Math.random() * possibleNumbers.size());
				int rand = possibleNumbers.get(randIdx);

				int couponId = allImages.get(rand).getCouponId();
				Coupon randCoupon = getOneCoupon(couponId);
				if (randCoupon.getEndDate().before(Date.valueOf(LocalDate.now())) || randCoupon.getAmount() < 1) {
					possibleNumbers.remove(randIdx);
					i--;
				} else {
					Image image = allImages.get(rand);
					images.add(image);
					coupons.add(randCoupon);
					possibleNumbers.remove(randIdx);
				}
			}
		}
		WelcomeCoupons welcomeCoupons = new WelcomeCoupons(coupons, images);

		return welcomeCoupons;
	}

	@Override
	public int getNumOfImages() {
		return imageRepository.findAll().size();

	}

	@Override
	public Image getOneImage(int couponId) {
		List<Image> images = imageRepository.findByCouponId(couponId);
		if (images.isEmpty()) {
			return null;
		}
		return images.get(0);
	}

	@Override
	public Coupon getOneCoupon(int couponId) throws CouponSystemException {
		return couponRepository.findById(couponId)
				.orElseThrow(() -> new CouponSystemException("This coupon id is not exist: " + couponId));
	}

}
