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
import com.salesforce.itqe.controller.utils.O2QUtil;
import com.salesforce.itqe.controller.utils.SfdcConstants;
import com.salesforce.itqe.domain.ui.pageobjects.ECOMMPage;

/**
 * Automation Test on ECOMM Report Sanity Check
 * @author bob
 *
 */
@Listeners(SalesforceEnforcer.class)
public class ForecastAutomationModel extends BaseTestCase{
	 /* Initializing logger */
    private static Logger logger = LoggerFactory.getLogger(ForecastAutomationModel.class); 
 
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
		//logoutPage = new LogOutPage(driver);
		loginPage.loginAs(LoginPage.getR6SysAdminUser(), LoginPage.getR6SysAdminUserPass());
		
		logger.info("Main Window Handle:" + driver.getWindowHandle());
		controller.updateWindowHandleMap("mainwindow", driver.getWindowHandle());
		ecommPage = new ECOMMPage(driver, controller);
	
		ecommPage.clickECOMMTab();
		logger.info("View Forecast with Outlook and Plan Report...");
		controller.chooseReport(driver, SfdcConstants.FORECAST_OUTLOOK_PLAN_TYPE);
	
		//check BU: Enterprise, Commercial, Public Sector, Data.com, Radian6 and Heroku Present
		Assert.assertTrue(ecommPage.isEnterpriseLinkPresent());
		Assert.assertTrue(ecommPage.isCommercialLinkPresent());
		Assert.assertTrue(ecommPage.isPublicSectorLinkPresent());
		Assert.assertTrue(ecommPage.isDataLinkPresent());
		Assert.assertTrue(ecommPage.isRadian6LinkPresent());
		Assert.assertTrue(ecommPage.isHerokuLinkPresent());
		
    }  
    

    /**
     *  ECOMM Report Basic Sanity Testing For Outlook+Plan Quarter Header, Text 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportBUSanityTest"}, alwaysRun=false)
    public void ecommReportOutlookPlanQuarterHeaderSanityTest(){  
    	logger.info("Executing ecommReportOutlookPlanQuarterHeaderSanityTest...");
		
		logger.info("View Forecast with Outlook and Plan Report, FY13, Q1...");
		controller.chooseReport(driver, SfdcConstants.FORECAST_OUTLOOK_PLAN_TYPE, "FY13", "Q1");
		
		//check Correct Report Type, Fiscal Year and Quarter
		Assert.assertEquals(ecommPage.getOutlookHeader(), SfdcConstants.FORECAST_OUTLOOK);
		Assert.assertEquals(ecommPage.getPlanHeader(), SfdcConstants.FORECAST_PLAN);
		
		Assert.assertEquals(ecommPage.getFirstMonthHeader(), "February");
		Assert.assertEquals(ecommPage.getSecondMonthHeader(), "March");
		Assert.assertEquals(ecommPage.getThirdMonthHeader(), "April");
		
		logger.info("View Forecast with Outlook and Plan Report, FY13, Q2...");
		controller.chooseReport(driver, SfdcConstants.FORECAST_OUTLOOK_PLAN_TYPE, "FY13", "Q2");
	    
		//check Correct Report Type, Fiscal Year and Quarter
		Assert.assertEquals(ecommPage.getOutlookHeader(), SfdcConstants.FORECAST_OUTLOOK);
		Assert.assertEquals(ecommPage.getPlanHeader(), SfdcConstants.FORECAST_PLAN);
		Assert.assertEquals(ecommPage.getFirstMonthHeader(), "May");
		Assert.assertEquals(ecommPage.getSecondMonthHeader(), "June");
		Assert.assertEquals(ecommPage.getThirdMonthHeader(), "July");
		
		logger.info("View Forecast with Outlook and Plan Report, FY13, Q3...");
		controller.chooseReport(driver, SfdcConstants.FORECAST_OUTLOOK_PLAN_TYPE, "FY13", "Q3");
	    
		//check Correct Report Type, Fiscal Year and Quarter
		Assert.assertEquals(ecommPage.getOutlookHeader(), SfdcConstants.FORECAST_OUTLOOK);
		Assert.assertEquals(ecommPage.getPlanHeader(), SfdcConstants.FORECAST_PLAN);
		Assert.assertEquals(ecommPage.getFirstMonthHeader(), "August");
		Assert.assertEquals(ecommPage.getSecondMonthHeader(), "September");
		Assert.assertEquals(ecommPage.getThirdMonthHeader(), "October");
		
    }  
    
    /**
     *  ECOMM Report Basic Sanity Testing For Outlook+Plan Calculation: TODO	
     */
 /*   @Test(groups = "unit",dependsOnMethods={"ecommReportOutlookPlanQuarterHeaderSanityTest"}, alwaysRun=false)
    public void ecommReportOutlookPlanCalculationSanityTest(){  
    	logger.info("Executing ecommReportOutlookPlanCalculationSanityTest...");
		
		//check Total(Closed) Calculation for this Quarter First Month of Forecast 
    	Assert.assertTrue( (ecommPage.getFirstMonthClosed() == ecommPage.getFirstMonthTotalClosed()) ||
    			           (ecommPage.getFirstMonthClosed() + 1 == ecommPage.getFirstMonthTotalClosed()));
    	
		//check Total(Outlook) Calculation  for this Quarter First Month of Outlook
		Assert.assertTrue((ecommPage.getFirstMonthOutlook() == ecommPage.getFirstMonthTotalOutlook()) ||
				          (ecommPage.getFirstMonthOutlook() + 1 == ecommPage.getFirstMonthTotalOutlook() ));
		
		//check Total(Plan) Calculation for this Quarter First Month of Plan
		Assert.assertTrue((ecommPage.getFirstMonthPlan()== ecommPage.getFirstMonthTotalPlan()) ||
				          (ecommPage.getFirstMonthPlan() + 1 == ecommPage.getFirstMonthTotalPlan() ));
		
        //check Quarter Total(Closed) Calculation for Enterprise Forecast
		Assert.assertTrue((ecommPage.getEnterpriseClosed()== ecommPage.getEnterpriseQuarterTotalClosed()) ||
				          (ecommPage.getEnterpriseClosed() + 1 == ecommPage.getEnterpriseQuarterTotalClosed()));
		
		//check Quarter Total(Outlook) Calculation for Enterprise Outlook
    	
		Assert.assertTrue( (ecommPage.getEnterpriseOutlook() == ecommPage.getEnterpriseQuarterTotalOutlook()) || 
				           (ecommPage.getEnterpriseOutlook() + 1 == ecommPage.getEnterpriseQuarterTotalOutlook()));
		
		//check Quarter Total(Plan) Calculation for Enterprise Plan 
		Assert.assertTrue((ecommPage.getEnterprisePlan() == ecommPage.getEnterpriseQuarterTotalPlan()) ||
				          (ecommPage.getEnterprisePlan() + 1 == ecommPage.getEnterpriseQuarterTotalPlan()));
		
    } */
    
    /**
     *  ECOMM Report last modified field test 		
     */
    //@Test(groups = "unit",dependsOnMethods={"ecommReportOutlookPlanQuarterHeaderSanityTest"}, alwaysRun=false)
    public void ecommReportLastModifiedTest(){  
    	logger.info("Executing ecommReportLastModifiedTest...");
		
    	logger.info("View Forecast with Outlook and Plan Report, Current Year, Quarter...");
		controller.chooseReport(driver, SfdcConstants.FORECAST_OUTLOOK_PLAN_TYPE, O2QUtil.getCurrentFiscalYear(), O2QUtil.getCurrentQuarter());
		
		//check forecast last modified present
		Assert.assertEquals(ecommPage.getForecastLastModifiedText(), SfdcConstants.FORECAST_LAST_MODIFIED);
		
		//check outlook last modified present
		Assert.assertEquals(ecommPage.getOutlookLastModifiedText(), SfdcConstants.OUTLOOK_LAST_MODIFIED);
		
		logger.info("View Forecast with Outlook and Plan Report, Current Year, Future Quarter...");
		controller.chooseReport(driver, SfdcConstants.FORECAST_OUTLOOK_PLAN_TYPE, O2QUtil.getCurrentFiscalYear(), O2QUtil.getNextQuarter());
		
		//check forecast last modified not present
		Assert.assertEquals(ecommPage.getForecastLastModifiedText(), SfdcConstants.FORECAST_LAST_MODIFIED);
		
		//check outlook last modified not present
		Assert.assertEquals(ecommPage.getOutlookLastModifiedText(), SfdcConstants.OUTLOOK_LAST_MODIFIED);
		
		driver.close();
    }  
    
   
}
