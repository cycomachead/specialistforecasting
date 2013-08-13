package com.salesforce.itqe.domain.ui.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.itqe.controller.Controller;
import com.salesforce.itqe.controller.utils.O2QUtil;
import com.salesforce.itqe.controller.utils.SfdcConstants;

/**
 * Profiles Page Object
 * @author bob
 *
 */
public class ProfilesPage {
    private final WebDriver driver;

    private static Logger logger = LoggerFactory.getLogger(ECOMMPage.class); 
  
    private Controller controller;
    

	public ProfilesPage(WebDriver driver, Controller controller) {
        this.driver = driver;
        this.controller = controller;
    }

    
    /**
	 * check BU: Enterprise Present
	 */
	public Boolean isEnterpriseLinkPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.ENTERPRISE)) != null)
			return true;
		else
			return false;	
	}
   
    
    /**
     * Test can't add ECOMM tab
     */
    public Boolean clickProfile() {
        
          /*Identify the elements to interact with */     
     
    	if(!controller.populate("profilepage", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
     * Click ECOMM Object
     */
    public Boolean clickECOMMObject() {
        
          /*Identify the elements to interact with */     
     
    	if(!controller.populate("profilepage_ecomm", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
     * Click Corporate Forecast Object
     */
    public Boolean clickCorporateForecastObject() {
        
          /*Identify the elements to interact with */     
     
    	if(!controller.populate("profilepage_corporate_forecast", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
	 * Save the settings
	 */
	public void clickSave() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement save = driver.findElement(By.id("page:console:j_id77:j_id78:j_id79:j_id101:j_id102:objects_tabs_detail:j_id120:0:j_id122:j_id123:j_id124:j_id125:button_pc_save"));
		save.click();
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
	}
	

}