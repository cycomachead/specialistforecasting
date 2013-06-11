package com.salesforce.itqe.testcases.o2q;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.salesforce.it.qe.testng.SalesforceEnforcer;
import com.salesforce.itqe.controller.Controller;
import com.salesforce.itqe.controller.utils.SfdcConstants;
import com.salesforce.itqe.domain.ui.pageobjects.LogOutPage;
import com.salesforce.itqe.domain.ui.pageobjects.LoginPage;

import com.salesforce.itqe.testcases.common.BaseTestCase;
import com.salesforce.itqe.domain.ui.pageobjects.ECOMMPage;

/**
 * Automation Test on Navigation Sanity Check
 * @author bob
 *
 */
@Listeners(SalesforceEnforcer.class)
public class ForecastAutomationNavigationModel extends BaseTestCase{
	 /* Initializing logger */
    private static Logger logger = LoggerFactory.getLogger(ForecastAutomationNavigationModel.class); 
 
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
     *  ECOMM Report Basic Sanity Testing For Navigation 		
     */
    //@Test(groups = "unit")
    public void ecommReportNavigationSanityTest(){  
    	logger.info("Executing ecommReportNavigationSanityTest...");
    
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.PAGE_LOAD, "salesforce.com");
		logger.info("Logging in...");
		loginPage = new LoginPage(driver,controller);	
		logoutPage = new LogOutPage(driver);
		loginPage.loginAs(LoginPage.getR6SysAdminUser(), LoginPage.getR6SysAdminUserPass());
		
		logger.info("Main Window Handle:" + driver.getWindowHandle());
		controller.updateWindowHandleMap("mainwindow", driver.getWindowHandle());
		ecommPage = new ECOMMPage(driver, controller);
		ecommPage.clickECOMMTab();
		
		logger.info("View Forecast with Outlook and Plan Report...");
		controller.chooseReport(driver, SfdcConstants.FORECAST_OUTLOOK_PLAN_TYPE);
		String firstMonth = ecommPage.getFirstMonthHeader();
		
		logger.info("Click Enterprise...");
		ecommPage.clickEnterpriseBU();
		
		logger.info("Click Go Back..");
		ecommPage.clickGoBack();
		
		String newFirstMonth = ecommPage.getFirstMonthHeader();
		logger.info("check Firth Month Header");
		Assert.assertEquals(newFirstMonth, firstMonth);
		
		logger.info("Click Enterprise..." );
		ecommPage.clickEnterpriseBU();
		
		String geoReportHeader = ecommPage.getGeoReportHeader();
		ecommPage.clickFirstGeo();
		ecommPage.clickGoBack();
		
		logger.info("check Geo Report Header " + geoReportHeader);
		Assert.assertEquals(ecommPage.getGeoReportHeader(), geoReportHeader);
		
		ecommPage.clickFirstGeo();
		ecommPage.clickGoBackTop();
		
		logger.info("check First Month Header ");
		Assert.assertEquals(ecommPage.getFirstMonthHeader(), firstMonth);
		
		driver.close();
		
    }  
   
}
