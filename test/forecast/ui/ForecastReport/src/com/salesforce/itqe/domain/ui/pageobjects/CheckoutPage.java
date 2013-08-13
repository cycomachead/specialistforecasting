package com.salesforce.itqe.domain.ui.pageobjects;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CheckoutPage {
	
	 private static Logger logger = LoggerFactory.getLogger(CheckoutPage.class); 
	 private final WebDriver driver;

	 public CheckoutPage(WebDriver driver) {
    	 this.driver = driver;
    	 logger.info("constructor is set");
    }
	 
	 public CheckoutPage placeAnOrder() {
		 
			try {
				TimeUnit.SECONDS.sleep(5);
			}catch(Exception e){
				
			}
			
			WebElement select = driver.findElement(By.id("j_id0:editForm:billingcountry"));
			List<WebElement> allOptions = select.findElements(By.tagName("option"));
			for (WebElement option : allOptions) {
			
			    if(option.getAttribute("value").equals("US - United States")) {
			    	option.click();
			    }
			}
			
			try {
				TimeUnit.SECONDS.sleep(5);
			}catch(Exception e){
				
			}
			
			select = driver.findElement(By.id("j_id0:editForm:billingstate"));
			allOptions = select.findElements(By.tagName("option"));
			for (WebElement option : allOptions) {
			  
			    if(option.getAttribute("value").equals("CA")) {
			    	option.click();
			    }
			}
		
			driver.findElement(By.id("serviceBillingAgreement")).click();
			driver.findElement(By.cssSelector("span.rightside")).click();
			driver.findElement(By.id("firstName")).clear();
			driver.findElement(By.id("firstName")).sendKeys("test");
			driver.findElement(By.id("lastName")).clear();
			driver.findElement(By.id("lastName")).sendKeys("test");
			select = driver.findElement(By.id("ccTypes"));
			allOptions = select.findElements(By.tagName("option"));
			for (WebElement option : allOptions) {
			   
			    if(option.getAttribute("value").equals("001")) {
			    	option.click();
			    }
			}
			
			// ERROR: Caught exception [ERROR: Unsupported command [select]]
			driver.findElement(By.id("cardAccountNumber")).clear();
			driver.findElement(By.id("cardAccountNumber")).sendKeys("4444222233331111");
			driver.findElement(By.id("cardcvvnumber")).clear();
			driver.findElement(By.id("cardcvvnumber")).sendKeys("111");
			
			select = driver.findElement(By.id("cardExpMnth"));
			allOptions = select.findElements(By.tagName("option"));
			for (WebElement option : allOptions) {
			  
			    if(option.getAttribute("value").equals("12")) {
			    	option.click();
			    }
			}
			
			select = driver.findElement(By.id("cardExpYr"));
			allOptions = select.findElements(By.tagName("option"));
			for (WebElement option : allOptions) {
			    
			    if(option.getAttribute("value").equals("2021")) {
			    	option.click();
			    }
			}
			// ERROR: Caught exception [ERROR: Unsupported command [select]]
			// ERROR: Caught exception [ERROR: Unsupported command [select]]
			driver.findElement(By.cssSelector("span.rightside")).click();
			// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
			try {
				TimeUnit.SECONDS.sleep(5);
			}catch(Exception e){
				
			}
			driver.findElement(By.id("msacheckbox")).click();
			// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
			driver.findElement(By.cssSelector("span.rightside")).click();
			// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
			driver.findElement(By.cssSelector("span.rightside")).click();
			// ERROR: Caught exception [ERROR: Unsupported command [isTextPresent]]
			//driver.findElement(By.linkText("03884448")).click();
			driver.findElement(By.linkText("Quotes")).click();

			return new CheckoutPage(driver);
	 }


}
