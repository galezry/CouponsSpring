package com.gal.coupon2.beans;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "coupons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int companyId;
	@Enumerated(EnumType.ORDINAL)
	private Category categoryId;
//	@Column(nullable = false)
	private String title;
	private String description;
	private Date startDate;
	private Date endDate;
	private int amount;
	private double price;
	private String image;
	
	
	
	
	
	@Override
	public String toString() {
		
		String space1 = id/10 == 0 ? "        " : "       ";
		
		String space2 = companyId/10 == 0 ? "              " : "             ";
		
		String space3 = "";
		for (int i = categoryId.name().length(); i < 20 ; i++) {
			space3 += " ";
		}
		
		String space4 = "";
		for (int i = title.length(); i < 20 ; i++) {
			space4 += " ";
		}
		
		String space5 = "";
		for (int i = description.length(); i < 17 ; i++) {
			space5 += " ";
		}
		
		String space6 = amount == 100  ? "         " : amount/10 > 0 ? "          " : "           ";
		
		
		return id + space1 + companyId + space2 + categoryId + space3 + title + space4 + description + space5 
				+ startDate + "        " + endDate + "       " +  amount + space6 + price + "        " + image;
		
		
		
	}
	
	
	
	
}
