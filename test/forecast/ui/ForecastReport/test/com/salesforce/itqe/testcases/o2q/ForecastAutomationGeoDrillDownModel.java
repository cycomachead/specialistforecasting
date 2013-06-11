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
 * Automation Test on ECOMM Report Business Unit Geo Drill Down
 * @author bob
 *
 */ 
@Listeners(SalesforceEnforcer.class)
public class ForecastAutomationGeoDrillDownModel extends BaseTestCase{
	 /* Initializing logger */
    private static Logger logger = LoggerFactory.getLogger(ForecastAutomationGeoDrillDownModel.class); 

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
     *  ECOMM Report Basic Sanity Testing For BU 		
     */
    //@Test(groups = "unit")
    public void ecommReportBUSanityTest(){  
    	logger.info("Executing ecommReportBUSanityTest...");
    
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
		//check BU: Enterprise, Commercial, Public Sector, Data.com and Radian6 Present
		logger.info("Validating Business Unit ...");
		Assert.assertTrue(ecommPage.isEnterpriseLinkPresent());
		Assert.assertTrue(ecommPage.isCommercialLinkPresent());
		Assert.assertTrue(ecommPage.isPublicSectorLinkPresent());
		Assert.assertTrue(ecommPage.isDataLinkPresent());
		Assert.assertTrue(ecommPage.isRadian6LinkPresent());
		Assert.assertTrue(ecommPage.isHerokuLinkPresent());
    }  
    
    /**
     *  ECOMM Report Basic Geo Drill Down Sanity Testing For Enterprise BU 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportBUSanityTest"}, alwaysRun=false)
    public void ecommReportEnterpriseBUGeoDrillDownSanityTest(){  
    	logger.info("Executing ecommReportEnterpriseBUGeoDrillDownSanityTest...");
   
    	ecommPage.clickEnterpriseBU();
		
		//check BU: Enterprise's Geo AMER-FS, APAC-FS, EMEA-FS AND JP-FS Are Present
    	logger.info("Validating Geo for Enterprise ...");
		Assert.assertTrue(ecommPage.isAMERPresent());
		Assert.assertTrue(ecommPage.isAPACPresent());
		Assert.assertTrue(ecommPage.isEMEAPresent());
		Assert.assertTrue(ecommPage.isJPPresent());
		
    } 
    
    /**
     *  ECOMM Report Basic Geo Drill Down Sanity Testing For Commercial BU 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportEnterpriseBUGeoDrillDownSanityTest"}, alwaysRun=false)
    public void ecommReportCommercialBUGeoDrillDownSanityTest(){  
    	logger.info("Executing ecommReportCommercialBUGeoDrillDownSanityTest...");
   
    	ecommPage.clickCommercialBU();
		
		//check BU: Commercial's Geo AMER/EMEA-CS, APAC-CS AND JP-CS Are Present
    	logger.info("Validating Geo for Commercial ...");
		Assert.assertTrue(ecommPage.isAMER_EMEAPresent());
		Assert.assertTrue(ecommPage.isAPAC_CSPresent());
		Assert.assertTrue(ecommPage.isJP_CSPresent());
    } 
    
    /**
     *  ECOMM Report Basic Geo Drill Down Sanity Testing For Public Sector BU 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportCommercialBUGeoDrillDownSanityTest"}, alwaysRun=false)
    public void ecommReportPublicBUGeoDrillDownSanityTest(){  
    	logger.info("Executing ecommReportPublicBUDrillDownSanityTest...");
   
    	ecommPage.clickPublicBU();
		
		//check BU: Public's Geo AMER-PUBSEC is Present
    	logger.info("Validating Geo for Public Sector ...");
		Assert.assertTrue(ecommPage.isAMER_PUBSECPresent());
	
    } 
    
    /**
     *  ECOMM Report Basic Geo Drill Down Sanity Testing For Data.com BU 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportPublicBUGeoDrillDownSanityTest"}, alwaysRun=false)
    public void ecommReportDataBUGeoDrillDownSanityTest(){  
    	logger.info("Executing ecommReportDataBUDrillDownSanityTest...");
   
    	ecommPage.clickDataBU();
		
		//check BU: Data.com's Geo Data-Alliances, Data-Marketing, Data-SVP and Data-Services are Present
    	logger.info("Validating Geo for Data.com ...");
		Assert.assertTrue(ecommPage.isData_AlliancesPresent());
		Assert.assertTrue(ecommPage.isData_MarketingPresent());
		Assert.assertTrue(ecommPage.isData_SVPPresent());
		Assert.assertTrue(ecommPage.isData_ServicesPresent());
	
    }   
    
    /**
     *  ECOMM Report Basic Geo Drill Down Sanity Testing For Radian6 BU 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportDataBUGeoDrillDownSanityTest"}, alwaysRun=false)
    public void ecommRadian6BUGeoDrillDownSanityTest(){  
    	logger.info("Executing ecommReportRadian6BUDrillDownSanityTest...");
   
    	ecommPage.clickRadian6BU();
		
		//check BU: Radian6's Geo R6-COO is Present
    	logger.info("Validating Geo for Radian6 Sector ...");
		Assert.assertTrue(ecommPage.isR6_COOPresent());
		
		driver.close();
	
    }  
   
    
}
