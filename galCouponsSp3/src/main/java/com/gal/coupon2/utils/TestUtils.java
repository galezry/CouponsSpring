package com.gal.coupon2.utils;

import org.springframework.stereotype.Component;

@Component
public class TestUtils {
	
	private static int count = 1;
	
	public void printTest(String testName) {
		System.out.println("-------------------------------------------------------------------------------------------------------------------");
		System.out.println("          Test#" + count++ + " " + testName + "                          ");
		System.out.println("-------------------------------------------------------------------------------------------------------------------");
	}

}
