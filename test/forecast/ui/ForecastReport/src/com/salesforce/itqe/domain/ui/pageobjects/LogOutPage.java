package com.salesforce.itqe.domain.ui.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogOutPage {
	
 private final WebDriver driver;
 private static Logger logger = LoggerFactory.getLogger(LogOutPage.class);
	 public LogOutPage(WebDriver driver) {
    	 this.driver = driver;

         // Check that we're on the right page.
         
    }
	 
	 public void logoutLoginForWebstore(WebDriver driver) {
		 driver.findElement(By.linkText("Logout")).click();
		 driver.findElement(By.id("userNav-arrow")).click();
		 driver.findElement(By.linkText("Logout")).click();
	 }
	 
	 
	 public void logOutandLoginPage(WebDriver driver) {
		 driver.findElement(By.id("userNavButton")).click();
			driver.findElement(By.linkText("Logout")).click();
	 }
	 
	 public void logout(WebDriver driver, LoginPage loginPage) {
		 logger.info("logging out");
		 driver.findElement(By.id("userNavButton")).click();
		 driver.findElement(By.linkText("Logout")).click();
		 logger.info("Logged out user" + "{" + loginPage.getCurrentLoggedInUser()==""?loginPage.getLoggedInSysAdminUser():loginPage.getCurrentLoggedInUser());
		 loginPage.setCurrentLoggedInUser("");
	 }
}
