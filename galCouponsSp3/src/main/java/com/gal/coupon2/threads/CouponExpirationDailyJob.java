package com.gal.coupon2.threads;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gal.coupon2.beans.Coupon;
import com.gal.coupon2.exceptions.CouponSystemException;
import com.gal.coupon2.repos.CouponRepository;
import com.gal.coupon2.services.CompanyServiceImpl;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CouponExpirationDailyJob {

	private final CouponRepository couponRepository;
	private final CompanyServiceImpl companyService;

	@Scheduled(fixedRate = 1000 * 60 * 60 * 24)
//	@Scheduled(fixedRate = 1000*10)
	public void removeExpiredCoupons() throws InterruptedException, CouponSystemException {

		System.out.println(Thread.currentThread().getName() + " thread is waking up\n");

		List<Coupon> expireCoupons = couponRepository.findByEndDateBefore(Date.valueOf(LocalDate.now()));
		for (Coupon coupon : expireCoupons) {
			companyService.setCompanyId(coupon.getCompanyId());
			companyService.deleteCoupon(coupon.getId());
		}
		System.out.println(Thread.currentThread().getName() + " thread is going to sleep\n");

	}

}
