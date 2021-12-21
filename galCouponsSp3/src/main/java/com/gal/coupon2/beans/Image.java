package com.gal.coupon2.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "images")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Image {
	@Id
	@GeneratedValue
	private Integer id;

	private String name;

	private String type;

	@Lob
	@Type(type = "org.hibernate.type.ImageType")
	private byte[] picture;

	private int couponId;

	private int companyId;

//Custom Construtor
	public Image(String name, String type, byte[] picture, int couponId, int companyId) {
		this.name = name;
		this.type = type;
		this.picture = picture;
		this.couponId = couponId;
		this.companyId = companyId;
	}

}
