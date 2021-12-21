package com.gal.coupon2.repos;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gal.coupon2.beans.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
	List<Coupon> findByEndDateBefore(Date date);

	List<Coupon> findByTitle(String title);

}
