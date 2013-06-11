package com.salesforce.itqe.testcases.o2q;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import com.salesforce.it.qe.testng.SalesforceEnforcer;
import com.salesforce.itqe.controller.Controller;
import com.salesforce.itqe.domain.ui.pageobjects.LogOutPage;
import com.salesforce.itqe.domain.ui.pageobjects.LoginPage;
import com.salesforce.itqe.domain.ui.pageobjects.ProfilesPage;

import com.salesforce.itqe.testcases.common.BaseTestCase;
import com.salesforce.itqe.controller.utils.SfdcConstants;
import com.salesforce.itqe.domain.ui.pageobjects.ECOMMPage;

/**
 * ECOMM Report Security Test with 
 * three users: xtang, xchen and qao2q 
 * three profiles: System Administrator(APEX & Activate), Global Field Sales and O2Q QA
 * @author bob
 *
 */
@Listeners(SalesforceEnforcer.class)
public class ForecastAutomationSecurityModel extends BaseTestCase{
	 /* Initializing logger */
    private static Logger logger = LoggerFactory.getLogger(ForecastAutomationSecurityModel.class); 
 
	/* LoginPage Object */
	LoginPage loginPage;
	/* LogoutPage Object */
	LogOutPage logoutPage;
	/* ECOMMTabPage Object */
	ECOMMPage ecommPage;
	/* ProfilesPage Object */
	ProfilesPage profilesPage;
	
    @BeforeClass    
    public void setUp(){    
    	super.setUp();
		controller.setTestID("ecomm");
    }
    
    /**
     *  ECOMM Report Security Testing by xchen/Global Field Sales		
     */
    //@Test(groups = "unit")
    public void ecommReportSecurityTest(){  
    	logger.info("Executing ecommReportSecurityTest...");
    
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.PAGE_LOAD, "salesforce.com");
		logger.info("Logging in...");
		loginPage = new LoginPage(driver,controller);	
		logoutPage = new LogOutPage(driver);
		loginPage.loginAs(LoginPage.getSecurityTestUser(), LoginPage.getSecurityTestUserPass());
		
		logger.info("Main Window Handle:" + driver.getWindowHandle());
		controller.updateWindowHandleMap("mainwindow", driver.getWindowHandle());
		
		//check ECOMM tab not available after login
		logger.info("Validate ECOMM Tab is not Visible");
		Assert.assertTrue(!loginPage.isEcommTabPresent());
    }  
    

    /**
     *  Test can't add ECOMM tab by xchen/Global Field Sales	
     */
   //@Test(groups = "unit",dependsOnMethods={"ecommReportSecurityTest"}, alwaysRun=false)
    public void ecommReportSecurityAddTabTest(){  
    	logger.info("Executing ecommReportSecurityAddTabTest...");
    	ecommPage = new ECOMMPage(driver, controller);
		logger.info("Click Add '+'...");
		ecommPage.clickAddTab();
		
		//check ECOMM tab not available to add
		logger.info("Validate ECOMM Tab is not available to add");
		//Assert.assertTrue(!ecommPage.isEcommObjectPresent());
		
		logoutPage.logout(driver, loginPage);
		
    }  
   
   /**
    *  Test can't add ECOMM tab after update profile by xchen/Global Field Sales			
    */
  //@Test(groups = "unit",dependsOnMethods={"ecommReportSecurityAddTabTest"}, alwaysRun=false)
   public void ecommReportSecurityDefaultOnClickTabTest(){  
	   	logger.info("Executing ecommReportSecurityDefaultOnClickTabTest...");
	   	updateTabSettings(SfdcConstants.ECOMM_TAB_DEFAULT_ON);
		
		logger.info("Save the settings...");
		profilesPage.clickSave();
		
		logoutPage.logout(driver, loginPage);
		loginPage.loginAs(LoginPage.getSecurityTestUser(), LoginPage.getSecurityTestUserPass());
		
		ecommPage.clickAddTab();
		ecommPage.clickECOMMReportsTabForAdding();
		
		logger.info("Validate Insufficient Privilieges...");
		Assert.assertEquals(ecommPage.getInsufficientPrivilegesMsg(), SfdcConstants.INSUFFICIENT_PRIVILEGES_MSG);
		
		//logger.info("Reset tab settings to hidden...");
		//resetSettingsToHidden();
		logoutPage.logout(driver, loginPage);
		
   }
  
  /**
   *  Test can't add ECOMM tab after update profile	xchen/Global Field Sales	
   */
 //@Test(groups = "unit",dependsOnMethods={"ecommReportSecurityDefaultOnClickTabTest"}, alwaysRun=false)
  public void ecommReportSecurityObjectPermissionClickTabTest(){  
	   	logger.info("Executing ecommReportSecurityObjectPermissionClickTabTest...");
	   	updateObjectPermission();
		
		logger.info("Save the settings...");
		profilesPage.clickSave();
		
		logoutPage.logout(driver, loginPage);
		loginPage.loginAs(LoginPage.getSecurityTestUser(), LoginPage.getSecurityTestUserPass());
		
		ecommPage.clickAddTab();
		ecommPage.clickECOMMReportsTabForAdding();
		
		logger.info("Validate Insufficient Privilieges...");
		Assert.assertEquals(ecommPage.getInsufficientPrivilegesMsg(), SfdcConstants.INSUFFICIENT_PRIVILEGES_MSG);
		
		logger.info("Reset tab settings to hidden...");
		
		logoutPage.logout(driver, loginPage);
		
		resetSettingsToHidden();
		
		driver.close();
  }
 
 /**
  *  ECOMM Report Security Testing by qao2q /O2Q QA		
  */
 //@Test(groups = "unit",dependsOnMethods={"ecommReportSecurityObjectPermissionClickTabTest"}, alwaysRun=false)
 /*public void ecommReportSecurityTestByNewUser(){  
 	   logger.info("Executing ecommReportSecurityTestByNewUser...");
 
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.PAGE_LOAD, "salesforce.com");
		logger.info("Logging in...");
		loginPage = new LoginPage(driver,controller);	
		logoutPage = new LogOutPage(driver);
		loginPage.loginAs(LoginPage.getO2QTestUser(), LoginPage.getO2QTestUserPass());
		
		logger.info("Main Window Handle:" + driver.getWindowHandle());
		controller.updateWindowHandleMap("mainwindow", driver.getWindowHandle());
		
		//check ECOMM tab not available after login
		logger.info("Validate ECOMM Tab is not Visible");
		Assert.assertTrue(!loginPage.isEcommTabPresent());
		
		driver.close();
 }  */
  
  
  /**
   *  Reset tab settting to tab hidden		
   */
  private void resetSettingsToHidden(){  
	   	logger.info("Executing resetSettingsToHidden...");
	   	updateTabSettings(SfdcConstants.ECOMM_TAB_HIDDEN);
		
		logger.info("Save the settings...");
		profilesPage.clickSave();	
  }

  /**
   * Update ECOMM Reports Tab settings
   * @param settings: Default On, Default Off, Tab Hidden
   */
  private void updateTabSettings(String settings) {
		
		//loginPage.loginAs(LoginPage.getSysAdminUser(), LoginPage.getSysAdminUserPass());
	    loginPage.loginAs(LoginPage.getR6SysAdminUser(), LoginPage.getR6SysAdminUserPass());
		
		logger.info("Click User SetUp...");
		ecommPage.clickSetup();
		
		logger.info("Update Global Field Sales Profile...");
		profilesPage = new ProfilesPage(driver,controller);	
		profilesPage.clickProfile();
		profilesPage.clickECOMMObject();
		
		logger.info("Set ECOMM Tab to: " + settings);
		controller.chooseECOMMTabSettings(driver, settings);
  }
  
  /**
   * Update Testing Profile has permission to Forecast Report Objects
   */
  private void updateObjectPermission() {
		
	    loginPage.loginAs(LoginPage.getR6SysAdminUser(), LoginPage.getR6SysAdminUserPass());
		
		logger.info("Click User SetUp...");
		ecommPage.clickSetup();
		
		logger.info("Update Global Field Sales Profile...");
		profilesPage = new ProfilesPage(driver,controller);	
		profilesPage.clickProfile();
		
		profilesPage.clickCorporateForecastObject();
		logger.info("Set Corporate Forecast Object Permissions: " );
		controller.chooseCorporateForecastObjectPermissions(driver);
  }
 
   
}
