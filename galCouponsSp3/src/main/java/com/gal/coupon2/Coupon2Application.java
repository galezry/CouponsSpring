package com.gal.coupon2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableScheduling
@EnableSwagger2

public class Coupon2Application {

	public static void main(String[] args) {
		SpringApplication.run(Coupon2Application.class, args);
		System.out.println("IoC Container was loaded\n");
	}

}
