package com.salesforce.itqe.domain.ui.pageobjects;

import org.openqa.selenium.WebDriver;

public class HomePage extends OpenTab{

    private final WebDriver driver;

    public HomePage(WebDriver driver) {
    	 this.driver = driver;

         // Check that we're on the right page.
         if (!driver.getTitle().startsWith("salesforce.com -")) {
             // Alternatively, we could navigate to the login page, perhaps logging out first
             throw new IllegalStateException("This is not the home page");
         }
    }

    public HomePage manageProfile() {
            // Page encapsulation to manage profile functionality
            return new HomePage(driver);
    }

}