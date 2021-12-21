package com.gal.coupon2.security;

import com.gal.coupon2.login.ClientType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoLogin {

	private String email;
	private String password;
	private ClientType type;

}
