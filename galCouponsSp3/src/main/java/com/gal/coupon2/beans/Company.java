package com.gal.coupon2.beans;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "companies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private String email;
	private String password;
	@OneToMany (cascade = CascadeType.ALL) 
	@Singular
	private List<Coupon> coupons = new ArrayList<>();
	
	
	@Override
	public String toString() {
		List<Integer> couponsIds = new ArrayList<>();
		for (Coupon coupon : coupons) {
			couponsIds.add(coupon.getId());
		}
		
		String space1 = id/10 == 0 ? "       " : "      ";
		
		String space2 = "";
		for (int i = name.length(); i < 17 ; i++) {
			space2 += " ";
		}
		
		String space3 = "";
		for (int i = email.length(); i < 25 ; i++) {
			space3 += " ";
		}
		
		return id + space1 + name + space2 + email + space3 + password + "         " + couponsIds;
	}
	
}
