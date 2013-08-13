package com.salesforce.itqe.domain.ui.pageobjects;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SignUpPage {
	
 private final WebDriver driver;
	 
 
 private static Logger logger = LoggerFactory.getLogger(SignUpPage.class); 

	 public SignUpPage(WebDriver driver) {
    	 this.driver = driver;
    	 logger.info("constructor is set");
       
    }
	 
	 
	 

}
