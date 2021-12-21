package com.gal.coupon2.repos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gal.coupon2.beans.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
	List<Image> findByCouponId(int couponId);

	List<Image> findByCompanyId(int companyId);

	void deleteByCouponId(int couponId);

	void deleteByCompanyId(int companyId);

}
