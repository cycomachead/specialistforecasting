package com.salesforce.itqe.domain.ui.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.itqe.controller.Controller;



public class OpenTab extends OtherApps{

	private static Logger logger = LoggerFactory.getLogger(OpenTab.class); 
	 public SysAdminPage sysadminTab(WebDriver driver) {
		 
		 	logger.info("sysadmin tab will be clicked now");
		 	driver.findElement(By.linkText("SysAdmin: NA1")).click();
	    	logger.info("sysadmin tab will be returned");
	    	return new SysAdminPage(driver, new Controller());
	    }
	 
	 public HomePage homePageTab(WebDriver driver) {
		 
		 	logger.info("Home tab will be clicked now");
		 	driver.findElement(By.linkText("Home")).click();
	    	logger.info("Home tab will be returned");
	    	return new HomePage(driver);
	    }
}
