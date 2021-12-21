package com.gal.coupon2.security;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.gal.coupon2.exceptions.CouponSysSecurityException;
import com.gal.coupon2.login.ClientType;
import com.gal.coupon2.services.AdminService;
import com.gal.coupon2.services.ClientService;
import com.gal.coupon2.services.CompanyService;
import com.gal.coupon2.services.CustomerService;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Component
public class TokenManager {

	private final Map<String, Information> map;

	public String createToken(ClientService clientService) {
		Information information = new Information(System.currentTimeMillis(), clientService);
		String token = UUID.randomUUID().toString();
		map.put(token, information);
		return token;
	}

	public boolean isExist(String token) {
		return map.get(token) != null;
	}

//  I've assumed it's more efficient (computer resources wise) to run the thread (that deletes the older tokens) every hour (or every day) instead of every second.
//  So that's why I check the validation of the timestamp of every token (that it's within the last 30 minutes).
//	The timestamp is set to the current time in every valid operation of a client.

	public ClientService checkValidation(String token, ClientType clientType) throws CouponSysSecurityException {
		boolean throwsecurityException = false;
		if (!isExist(token)) {
			throw new CouponSysSecurityException("You are not signed in - Please sign in");
		}
		if (System.currentTimeMillis() - map.get(token).getTimestamp() > 1000 * 60 * 30) {
			map.remove(token);
			throw new CouponSysSecurityException("Your session has been timed out - Please sign in again");
		}

		switch (clientType) {
		case ADMINISTRATOR:
			if (!(map.get(token).getClientService() instanceof AdminService)) {
				throwsecurityException = true;
			}
			break;

		case COMPANY:
			if (!(map.get(token).getClientService() instanceof CompanyService)) {
				throwsecurityException = true;
			}
			break;

		case CUSTOMER:
			if (!(map.get(token).getClientService() instanceof CustomerService)) {
				throwsecurityException = true;
			}
		}
		if (throwsecurityException) {
			String type;
			if (map.get(token).getClientService() instanceof AdminService) {
				type = "Admin";
			} else if (map.get(token).getClientService() instanceof CompanyService) {
				type = "Company";
			} else {
				type = "Customer";
			}
			throw new CouponSysSecurityException(
					String.format("You are connected as a %s. You are not authorized for this action", type));
		}
		map.get(token).setTimestamp(System.currentTimeMillis());
		return map.get(token).getClientService();
	}

}
