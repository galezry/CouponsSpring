package com.gal.coupon2.beans;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;


@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "customers")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	
	@ManyToMany //(cascade = CascadeType.DETACH)
	@Singular
	private List<Coupon> coupons = new ArrayList<>();
	
	
	
	@Override
	public String toString() {
		List<Integer> couponsIds = new ArrayList<>();
		for (Coupon coupon : coupons) {
			couponsIds.add(coupon.getId());
		}
		
		String space1 = id/10 == 0 ? "         " : "        ";
		
		String space2 = "";
		for (int i = firstName.length(); i < 16 ; i++) {
			space2 += " ";
		}
		
		String space3 = "";
		for (int i = lastName.length(); i < 16 ; i++) {
			space3 += " ";
		}
		
		String space4 = "";
		for (int i = email.length(); i < 23 ; i++) {
			space4 += " ";
		}
		
		return id + space1 + firstName + space2 + lastName + space3 + email + space4 + password + "             " + couponsIds;
		
	}
	
	
	
	

}
