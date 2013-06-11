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

import com.salesforce.itqe.testcases.common.BaseTestCase;
import com.salesforce.itqe.controller.utils.SfdcConstants;
import com.salesforce.itqe.domain.ui.pageobjects.ECOMMPage;

/**
 * Automation Test on Deal Report Sanity Check
 * @author bob
 *
 */
@Listeners(SalesforceEnforcer.class)
public class ForecastAutomationDealModel extends BaseTestCase{
	 /* Initializing logger */
    private static Logger logger = LoggerFactory.getLogger(ForecastAutomationDealModel.class); 
 
	/* LoginPage Object */
	LoginPage loginPage;
	/* LogoutPage Object */
	LogOutPage logoutPage;
	/* ECOMMTabPage Object */
	ECOMMPage ecommPage;
    
    @BeforeClass    
    public void setUp(){    
    	super.setUp();
		controller.setTestID("ecomm");
    }
    
    /**
     *  ECOMM Report Basic Sanity Testing For Deal 		
     */
    @Test(groups = "unit")
    public void ecommReportDealSanityTest(){  
    	logger.info("Executing ecommReportDealSanityTest...");
    
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.PAGE_LOAD, "salesforce.com");
		logger.info("Logging in...");
		loginPage = new LoginPage(driver,controller);	
		logoutPage = new LogOutPage(driver);
		loginPage.loginAs(LoginPage.getR6SysAdminUser(), LoginPage.getR6SysAdminUserPass());
		
		logger.info("Main Window Handle:" + driver.getWindowHandle());
		controller.updateWindowHandleMap("mainwindow", driver.getWindowHandle());
		ecommPage = new ECOMMPage(driver, controller);
		ecommPage.clickECOMMTab();
		
		logger.info("View Reports and Dashboards...");
		//controller.chooseReport(driver, SfdcConstants.FORECAST_DEAL_TYPE);
		Assert.assertEquals(ecommPage.getDealReportHeader(), SfdcConstants.FORECAST_DEAL);
		Assert.assertTrue(ecommPage.isForecastLinkPresent());
		
		driver.close();
		
    }  
   
}
