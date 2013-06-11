package com.salesforce.itqe.domain.ui.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.itqe.domain.ui.pageobjects.CheckoutPage;

public class OtherApps {
	private static Logger logger = LoggerFactory.getLogger(OtherApps.class); 
	
	public CheckoutPage checkoutPage(WebDriver driver) {
		 logger.info("place an order, we need to change this method and need to use page object");
		 driver.findElement(By.id("tsid-arrow")).click();
			driver.findElement(By.linkText("Checkout")).click();
			logger.info("user is on checkout page");
		return new CheckoutPage(driver);
	}

}
