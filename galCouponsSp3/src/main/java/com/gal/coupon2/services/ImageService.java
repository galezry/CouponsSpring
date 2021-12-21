package com.gal.coupon2.services;

import java.util.List;

import com.gal.coupon2.beans.Image;

public interface ImageService {

	void addImage(Image image);

	void updateImage(Image image);

	void deleteImage(int couponId);

	Image getOneImage(int couponId);

	List<Image> getCompanyImages(int companyId);

	List<Image> getAllImages();

	boolean doesImageExist(int couponId);

}
