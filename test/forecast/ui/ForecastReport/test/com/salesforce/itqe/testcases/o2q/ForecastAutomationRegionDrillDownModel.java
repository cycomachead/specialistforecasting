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
 * Automation Test on ECOMM Report Business Unit Region Drill Down
 * @author bob
 *
 */
@Listeners(SalesforceEnforcer.class)
public class ForecastAutomationRegionDrillDownModel extends BaseTestCase{
	 /* Initializing logger */
    private static Logger logger = LoggerFactory.getLogger(ForecastAutomationRegionDrillDownModel.class); 

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
     *  ECOMM Report Basic Sanity Testing For BU Links 		
     */
    //@Test(groups = "unit")
    public void ecommReportBULinkSanityTest(){  
    	logger.info("Executing ecommReportBULinkSanityTest...");
    
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
     *  ECOMM Report Basic Region Drill Down Sanity Testing For Enterprise BU 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportBULinkSanityTest"}, alwaysRun=false)
    public void ecommReportEnterpriseBURegionDrillDownSanityTest(){  
    	logger.info("Executing ecommReportEnterpriseBURegionDrillDownSanityTest...");
   
    	ecommPage.clickEnterpriseBU();
    	
		ecommPage.clickFirstGeo();
		//check BU: Enterprise AMER-FS's Region Central, ECS, East and West Are Present
    	logger.info("Validating Region for Enterprise AMER-FS...");
		Assert.assertEquals(ecommPage.getFirstRegion(),"Central");
		Assert.assertEquals(ecommPage.getSecondRegion(),"ECS");
		Assert.assertEquals(ecommPage.getThirdRegion(),"East");
		Assert.assertEquals(ecommPage.getFouthRegion(),"West");
		
        ecommPage.clickGoBack();
    	
		ecommPage.clickSecondGeo();
		//check BU: Enterprise APAC-FS's Region ANZ, ASEAN/GCR, CSS, India Are Present
    	logger.info("Validating Region for Enterprise APAC-FS...");
		Assert.assertEquals(ecommPage.getFirstRegion(),"ANZ");
		Assert.assertEquals(ecommPage.getSecondRegion(),"ASEAN/GCR");
		Assert.assertEquals(ecommPage.getThirdRegion(),"CSS");
		Assert.assertEquals(ecommPage.getFouthRegion(),"India");
		
		ecommPage.clickGoBack();
	    	
		ecommPage.clickThirdGeo();
		//check BU: Enterprise EMEA-FS's Region Central, North, South and UK/Ireland Are Present
    	logger.info("Validating Region for Enterprise EMEA-FS...");
		Assert.assertEquals(ecommPage.getFirstRegion(),"Central");
		Assert.assertEquals(ecommPage.getSecondRegion(),"North");
		Assert.assertEquals(ecommPage.getThirdRegion(),"South");
		Assert.assertEquals(ecommPage.getFouthRegion(),"UK/Ireland");
		
		ecommPage.clickGoBack();
    	
		ecommPage.clickFouthGeo();
		//check BU: Enterprise JP-FS's Region ECS, Strategic1, Strategic2, Strategic3  Are Present
    	logger.info("Validating Region for Enterprise JP-FS...");
		Assert.assertEquals(ecommPage.getFirstRegion(),"ECS");
		Assert.assertEquals(ecommPage.getSecondRegion(),"Strategic1");
		Assert.assertEquals(ecommPage.getThirdRegion(),"Strategic2");
		Assert.assertEquals(ecommPage.getFouthRegion(),"Strategic3");
		
		ecommPage.clickGoBack();
    } 
    
    /**
     *  ECOMM Report Basic Region Drill Down Sanity Testing For Commercial BU 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportEnterpriseBURegionDrillDownSanityTest"}, alwaysRun=false)
    public void ecommReportCommercialBURegionDrillDownSanityTest(){  
    	logger.info("Executing ecommReportCommercialBURegionDrillDownSanityTest...");
   
    	ecommPage.clickCommercialBU();
		
    	ecommPage.clickFirstGeo();
		//check BU: Commercial AMER/EMEA-CS's Region AMER-CS and EMEA-CS-SAVP Are Present
    	logger.info("Validating Region for Commercial AMER/EMEA-CS...");
		Assert.assertEquals(ecommPage.getFirstRegion(),"AMER-CS");
		Assert.assertEquals(ecommPage.getSecondRegion(),"EMEA-CS-SAVP");
	
        ecommPage.clickGoBack();
    	
		ecommPage.clickSecondGeo();
		//check BU: Commercial APAC-CS's Region ANZ, ANZ-NAMED, ASEAN/KOR, ESMB, GCR, India Are Present
    	logger.info("Validating Region for Commercial APAC-CS...");
		Assert.assertEquals(ecommPage.getFirstRegion(),"ANZ");
		Assert.assertEquals(ecommPage.getSecondRegion(),"ANZ-NAMED");
		Assert.assertEquals(ecommPage.getThirdRegion(),"ASEAN/KOR");
		Assert.assertEquals(ecommPage.getFouthRegion(),"ESMB");
		Assert.assertEquals(ecommPage.getFifthRegion(),"GCR");
		Assert.assertEquals(ecommPage.getSixthRegion(),"India");
		
		ecommPage.clickGoBack();
	    	
		ecommPage.clickThirdGeo();
		//check BU: Commercial JP-CS's Region MM/GB, OSK/RGN and TKO-ESMB Are Present
    	logger.info("Validating Region for Commercial JP-CS...");
		Assert.assertEquals(ecommPage.getFirstRegion(),"MM/GB");
		Assert.assertEquals(ecommPage.getSecondRegion(),"OSK/RGN");
		Assert.assertEquals(ecommPage.getThirdRegion(),"TKO-ESMB");
		
		ecommPage.clickGoBack();
    } 
    
    /**
     *  ECOMM Report Basic Region Drill Down Sanity Testing For Public Sector BU 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportCommercialBURegionDrillDownSanityTest"}, alwaysRun=false)
    public void ecommReportPublicBURegionDrillDownSanityTest(){  
    	logger.info("Executing ecommReportPublicBURegionDrillDownSanityTest...");
   
    	ecommPage.clickPublicBU();
		
    	ecommPage.clickFirstGeo();
		//check BU: Public AMER-PUBSEC's Region FED and S&L Are Present
    	logger.info("Validating Region for Public AMER-PUBSEC...");
    	Assert.assertEquals(ecommPage.getFirstRegion(),"FED");
		Assert.assertEquals(ecommPage.getSecondRegion(),"S&L");
		
		ecommPage.clickGoBack();
    } 
    
    /**
     *  ECOMM Report Basic Region Drill Down Sanity Testing For Data.com BU 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportPublicBURegionDrillDownSanityTest"}, alwaysRun=false)
    public void ecommReportDataBURegionDrillDownSanityTest(){  
    	logger.info("Executing ecommReportDataBURegionDrillDownSanityTest...");
   
    	ecommPage.clickDataBU();
		
		ecommPage.clickFirstGeo();
		//check BU: Data.com Data-Alliances's Region Data-Alliances-Mgr Present
    	logger.info("Validating Region for Data.com Data-Alliances...");
		Assert.assertEquals(ecommPage.getFirstRegion(),"Data-Alliances-Mgr");
		
        ecommPage.clickGoBack();
    	
		ecommPage.clickSecondGeo();
		//check BU: Data.com Data-Marketing's Region Present  //TODO
    	logger.info("Validating Region for Data.com Data-Marketing...");
		
		
		ecommPage.clickGoBack();
	    	
		ecommPage.clickThirdGeo();
		//check BU: Data.com Data-SVP's Region Data-AMER-CS and Data-AMER-FS Are Present
    	logger.info("Validating Region for Data.com Data-SVP...");
		Assert.assertEquals(ecommPage.getFirstRegion(),"Data-AMER-CS");
		Assert.assertEquals(ecommPage.getSecondRegion(),"Data-AMER-FS");
		
		
		ecommPage.clickGoBack();
    	
		ecommPage.clickFouthGeo();
		//check BU: Data.com Data-Services's Region Present //TODO
    	logger.info("Validating Region for Data.com Data-Services...");
		
		ecommPage.clickGoBack();
		
    }   
    
    /**
     *  ECOMM Report Basic Region Drill Down Sanity Testing For Radian6 BU 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportDataBURegionDrillDownSanityTest"}, alwaysRun=false)
    public void ecommRadian6PublicBURegionDrillDownSanityTest(){  
    	logger.info("Executing ecommReportRadian6BURegionDrillDownSanityTest...");
   
    	ecommPage.clickRadian6BU();
		
    	ecommPage.clickFirstGeo();
		//check BU: Radian6 R6-COO's Region R6-CCO is Present
    	logger.info("Validating Region for Radian6 R6-COO...");
		Assert.assertEquals(ecommPage.getFirstRegion(),"R6-CCO");
	
    }  
    
    /**
     *  ECOMM Report Basic Region Drill Down Sanity Testing For Heroku BU 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommRadian6PublicBURegionDrillDownSanityTest"}, alwaysRun=false)
    public void ecommHerokuPublicBURegionDrillDownSanityTest(){  
    	logger.info("Executing ecommHerokuPublicBURegionDrillDownSanityTest...");
   
    	Assert.assertTrue(ecommPage.clickHerokuBU());
		
    	//TODO
    	//ecommPage.clickFirstGeo();
		//check BU: Radian6 R6-COO's Region R6-CCO is Present
    	//logger.info("Validating Region for Radian6 R6-COO...");
		//Assert.assertEquals(ecommPage.getFirstRegion(),"R6-CCO");
		
		driver.close();
	
    }  
   
    
}
