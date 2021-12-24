package com.gal.coupon2.login;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.gal.coupon2.exceptions.CouponSysSecurityException;
import com.gal.coupon2.security.TokenManager;
import com.gal.coupon2.services.AdminService;
import com.gal.coupon2.services.ClientService;
import com.gal.coupon2.services.CompanyService;
import com.gal.coupon2.services.CustomerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginManager {

	private final ApplicationContext ctx;
	private final AdminService adminService;
	private final TokenManager tokenManager;

	private CompanyService companyService;
	private CustomerService customerService;

	public String login(String email, String password, ClientType clientType) throws CouponSysSecurityException {
		switch (clientType) {
		case ADMINISTRATOR:
			if (((ClientService) adminService).login(email, password)) {
				return tokenManager.createToken((ClientService) adminService);

			}
			break;

		case COMPANY:
			companyService = ctx.getBean(CompanyService.class);
			if (((ClientService) companyService).login(email, password)) {
				return tokenManager.createToken((ClientService) companyService);

			}
			break;

		case CUSTOMER:
			customerService = ctx.getBean(CustomerService.class);
			if (((ClientService) customerService).login(email, password)) {
				return tokenManager.createToken((ClientService) customerService);

			}
		}
		throw new CouponSysSecurityException(
				"The sign in information is incorrect." + " Please try again or create an account");
	}
}
