package com.gal.coupon2.security;

import com.gal.coupon2.services.ClientService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Information {
	
	private long timestamp;
	private ClientService clientService;

}
