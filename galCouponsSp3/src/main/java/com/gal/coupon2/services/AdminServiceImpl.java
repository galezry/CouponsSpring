package com.gal.coupon2.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gal.coupon2.beans.Company;
import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.beans.Customer;
import com.gal.coupon2.beans.Image;
import com.gal.coupon2.exceptions.CouponSystemException;
import com.gal.coupon2.repos.CompanyRepository;
import com.gal.coupon2.repos.CouponRepository;
import com.gal.coupon2.repos.CustomerRepository;
import com.gal.coupon2.repos.ImageRepository;

@Service
public class AdminServiceImpl extends ClientService implements AdminService {

	private static final String EMAIL = "admin@admin.com";
	private static final String PASSWORD = "admin12#";

	@Autowired
	private ImageRepository imageRepository;

	public AdminServiceImpl(CompanyRepository companyRepository, CustomerRepository customerRepository,
			CouponRepository couponRepository) {
		super(companyRepository, customerRepository, couponRepository);

	}

	@Override
	public boolean login(String email, String password) {

		return email.equalsIgnoreCase(EMAIL) && password.equals(PASSWORD);
	}

	@Override
	public void addCompany(Company company) throws CouponSystemException {
		Company sameName = companyRepository.findByNameIgnoreCase(company.getName());
		Company sameEmail = companyRepository.findByEmailIgnoreCase(company.getEmail());
		if (sameName != null && sameEmail != null) {
			throw new CouponSystemException("We're sorry. Both name and email already exist in our records. "
					+ "Please Enter a different name and email");
		}
		if (sameName != null) {
			throw new CouponSystemException(
					"We're sorry. An account already exists with this name. Please Enter a different name");
		}
		if (sameEmail != null) {
			throw new CouponSystemException("We're sorry. Our records show this email address is taken already. "
					+ "Please Enter a different email");
		}
//		for (Company comp : getAllCompanies()) {
//			if (comp.getName().equalsIgnoreCase(company.getName())
//					&& comp.getEmail().equalsIgnoreCase(company.getEmail())) {
//				throw new CouponSystemException(
//						"Company Name and Email are already exist. " + "Please enter another name and email");
//			}
//			if (comp.getName().equalsIgnoreCase(company.getName())) {
//				throw new CouponSystemException("Company Name is already exist. Please enter another name");
//			}
//			if (comp.getEmail().equalsIgnoreCase(company.getEmail())) {
//				throw new CouponSystemException("Email is already exist. Please enter another email");
//			}
//		}
		companyRepository.save(company);
	}

	@Override
	public void updateCompany(Company company) throws CouponSystemException {
		Company comp = getOneCompany(company.getId());
		if (comp.getName().equalsIgnoreCase(company.getName())) {
			if (!comp.getEmail().equalsIgnoreCase(company.getEmail())) {
				Company sameEmail = companyRepository.findByEmailIgnoreCase(company.getEmail());
				if (sameEmail != null) {
					throw new CouponSystemException("We're sorry. Our records show this email address"
							+ " is taken already. Please Enter a different email");
				}
			}
			companyRepository.saveAndFlush(company);
			return;
		}
		throw new CouponSystemException("Cannot update Comapny: Name cannot be changed");

//		for (Company comp : getAllCompanies()) {
//			if (comp.getId() == company.getId()) {
//				if (comp.getName().equalsIgnoreCase(company.getName())) {
//					companyRepository.saveAndFlush(company);
//					return;
//				}
//				throw new CouponSystemException("Cannot update Comapny: Name cannot be changed");
//			}
//		}
//		throw new CouponSystemException("Cannot update Comapny: Id does not exist");
	}

	@Override
	public void deleteCompany(int companyId) throws CouponSystemException {

		// this deletes the coupons from the customers coupon list so the
		// customers_coupons table in the DB will be updated:
		List<Customer> customers = customerRepository.findAll();
		for (Coupon coupon : getOneCompany(companyId).getCoupons()) {
			for (Customer customer : customers) {
				if (customer.getCoupons().contains(coupon)) {
					customer.getCoupons().remove(coupon);
					customerRepository.saveAndFlush(customer);
				}
			}
		}

		// this deletes the images of this company from the image repository
		List<Image> imagesOfThisCompany = imageRepository.findByCompanyId(companyId);
		if (!imagesOfThisCompany.isEmpty()) {
			for (int i = 0; i < imagesOfThisCompany.size(); i++) {
				int imageId = imagesOfThisCompany.get(i).getId();
				imageRepository.deleteById(imageId);
			}
		}

		companyRepository.deleteById(companyId);
		// Now The company's coupons was deleted automatically from the coupons table
		// and from companies_coupons table
	}

	@Override
	public List<Company> getAllCompanies() {
		return companyRepository.findAll();
	}

	@Override
	public Company getOneCompany(int companyId) throws CouponSystemException {
		return companyRepository.findById(companyId).orElseThrow(() -> new CouponSystemException("Id not found"));
	}

	@Override
	public void addCustomer(Customer customer) throws CouponSystemException {
		for (Customer c : getAllCustomers()) {
			if (c.getEmail().equalsIgnoreCase(customer.getEmail())) {
				throw new CouponSystemException("We're sorry. Our records show this e-mail address"
						+ " is taken already. Please Enter a different email");
			}
		}
		customerRepository.save(customer);
	}

	@Override
	public void updateCustomer(Customer customer) throws CouponSystemException {
		Customer cust = getOneCustomer(customer.getId());
		if (!cust.getEmail().equalsIgnoreCase(customer.getEmail())) {
			List<Customer> sameEmail = customerRepository.findByEmailIgnoreCase(customer.getEmail());
			if (sameEmail.size() > 0) {
				throw new CouponSystemException("We're sorry. Our records show this e-mail address"
						+ " is taken already. Please Enter a different email");
			} // if doesnt work change sameEmail to Customer instead of a list - here and on
				// repository
		}
		customerRepository.saveAndFlush(customer);
		return;

//		for (Customer c : getAllCustomers()) {
//			if (c.getId() == customer.getId()) {
//				customerRepository.saveAndFlush(customer);
//				return;
//			}
//			
//			
//			// We're sorry. Our records show this e-mail address"
//		//	+ " is taken already. Please Enter a different email
//		}

	}

	@Override
	public void deleteCustomer(int customerId) throws CouponSystemException {
		getOneCustomer(customerId); // in order to make sure this Id exists => otherwise exception will be thrown
		customerRepository.deleteById(customerId);
		// customers_coupons table was automatically updated in the DB
	}

	@Override
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	@Override
	public Customer getOneCustomer(int customerId) throws CouponSystemException {
		return customerRepository.findById(customerId).orElseThrow(() -> new CouponSystemException("Id not found"));
	}

	@Override
	public void restore() {

		Company c1 = Company.builder().name("Coca Cola").email("cola@gmail.com").password("1234ab!").build();
		Company c2 = Company.builder().name("Walmart").email("walmart@gmail.com").password("1234ab!").build();
		Company c3 = Company.builder().name("Netflix").email("netflix@gmail.com").password("1234ab!").build();
		Company c4 = Company.builder().name("CVS").email("cvs@gmail.com").password("1234ab!").build();
		Company c5 = Company.builder().name("Target").email("target@gmail.com").password("1234ab!").build();
		Company c6 = Company.builder().name("Pepsi").email("pepsi@gmail.com").password("1234ab!").build();

		addCompanyIfNotExisted(c1);
		addCompanyIfNotExisted(c2);
		addCompanyIfNotExisted(c3);
		addCompanyIfNotExisted(c4);
		addCompanyIfNotExisted(c5);
		addCompanyIfNotExisted(c6);

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

		Customer cu6 = Customer.builder().firstName("Jeff").lastName("Sims").email("jeff@gmail.com").password("1234ab@")
				.build();

		addCustomerIfNotExisted(cu1);
		addCustomerIfNotExisted(cu2);
		addCustomerIfNotExisted(cu3);
		addCustomerIfNotExisted(cu4);
		addCustomerIfNotExisted(cu5);
		addCustomerIfNotExisted(cu6);

//
//		Company walmart = companyRepository.findByEmailIgnoreCase("walmart@gmail.com");
//		if (walmart != null) { // just in case - the company is surly existed
//			Coupon co3 = Coupon.builder().companyId(walmart.getId()).categoryId(Category.FOOD).title("Frozen Fries")
//					.description("30% off").startDate(Date.valueOf(LocalDate.now()))
//					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(3.49)
//					.image("http://image.com").build();
//			Coupon co11 = Coupon.builder().companyId(walmart.getId()).categoryId(Category.ELECTRICITY).title("Heater")
//					.description("30% off").startDate(Date.valueOf(LocalDate.now()))
//					.endDate(Date.valueOf(LocalDate.now().plusWeeks(1))).amount(100).price(29.99)
//					.image("http://image.com").build();
//			addCouponIfNotExisted(walmart, co3);
//			addCouponIfNotExisted(walmart, co11);
//			if (walmart.getCoupons().isEmpty()) {
//				try {
//					System.out.println(co3);
//					System.out.println(walmart.getCoupons());
//					walmart.setCoupons(Arrays.asList(co3, co11));
//					System.out.println(walmart);
//				} catch (Exception e) {
//					System.out.println(e);
//				}
//				try {
//					companyRepository.saveAndFlush(walmart);
//				} catch (Exception e) {
//					System.out.println("l.284");
//					System.out.println(e);
//				}
//
//			}
//
//		}
//

	}

	public void addCompanyIfNotExisted(Company c) {
		Company sameName = companyRepository.findByNameIgnoreCase(c.getName());
		Company sameEmail = companyRepository.findByEmailIgnoreCase(c.getEmail());
		if (sameName == null && sameEmail == null) {
			companyRepository.save(c);
		}
	}

	public void addCustomerIfNotExisted(Customer c) {
		List<Customer> sameEmail = customerRepository.findByEmailIgnoreCase(c.getEmail());
		if (sameEmail.size() == 0) {
			customerRepository.save(c);
		}
	}

//	public void addCouponIfNotExisted(Company company, Coupon coupon) {
//		for (Coupon c : company.getCoupons()) {
//			if (c.getTitle().equalsIgnoreCase(coupon.getTitle())) {
//				return;
//			}
//		}
//		couponRepository.save(coupon);
//	}

	// cannot add coupons to customers because you can't be sure the coupon was
	// created
//	public void addCouponToCustomer(Customer customer, Coupon coupon) {
//		for (Coupon c : customer.getCoupons()) {
//			if (c.getTitle().equalsIgnoreCase(coupon.getTitle())) {
//				return;
//			}
//		}
//		customer.getCoupons().add(coupon);
//		customerRepository.saveAndFlush(customer);
//		coupon.setAmount(coupon.getAmount() - 1);
//		couponRepository.saveAndFlush(coupon);
//	}

}
