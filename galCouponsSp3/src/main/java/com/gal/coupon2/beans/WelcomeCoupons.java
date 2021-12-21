package com.gal.coupon2.beans;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WelcomeCoupons {
	private List<Coupon> coupons = new ArrayList<>();
	private List<Image> images = new ArrayList<>();

}
