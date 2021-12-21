package com.gal.coupon2.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gal.coupon2.beans.Image;
import com.gal.coupon2.repos.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

	private final ImageRepository imageRepository;

	@Override
	public void addImage(Image image) {
		imageRepository.save(image);
	}

	@Override
	public void updateImage(Image image) {
		if (doesImageExist(image.getCouponId())) {
			Image existedImage = getOneImage(image.getCouponId());
			image.setId(existedImage.getId());
			imageRepository.saveAndFlush(image);
		} else {
			imageRepository.save(image);
		}
	}

	@Override
	public void deleteImage(int couponId) {
		if (doesImageExist(couponId)) {
			imageRepository.deleteByCouponId(couponId);
		}
	}

	@Override
	public Image getOneImage(int couponId) {
		List<Image> images = imageRepository.findByCouponId(couponId);
		if (images.isEmpty()) {
			return null;
		}
		return images.get(0);
	}

	@Override
	public List<Image> getCompanyImages(int companyId) {
		return imageRepository.findByCompanyId(companyId);
	}

	@Override
	public List<Image> getAllImages() {
		return imageRepository.findAll();
	}

	@Override
	public boolean doesImageExist(int couponId) {
		List<Image> images = imageRepository.findByCouponId(couponId);
		return !images.isEmpty();
	}

}
