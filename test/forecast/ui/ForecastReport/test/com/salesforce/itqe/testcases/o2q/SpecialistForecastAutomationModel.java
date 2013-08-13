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
import com.salesforce.itqe.domain.ui.pageobjects.SpecPage;

import com.salesforce.itqe.testcases.common.BaseTestCase;
import com.salesforce.itqe.controller.utils.O2QUtil;
import com.salesforce.itqe.controller.utils.SfdcConstants;

/**
 * Automation Test on Specialist Forecast Report Sanity Check
 * @author bob
 *
 */
@Listeners(SalesforceEnforcer.class)
public class SpecialistForecastAutomationModel extends BaseTestCase{
	 /* Initializing logger */
    private static Logger logger = LoggerFactory.getLogger(SpecialistForecastAutomationModel.class); 
 
	/* LoginPage Object */
	LoginPage loginPage;
	/* LogoutPage Object */
	LogOutPage logoutPage;
	/* SpecPage Object */
	SpecPage specPage;
    
    @BeforeClass    
    public void setUp(){    
    	super.setUp();
		controller.setTestID("specialist");
    }
    
    /**
     *  Specialist Forecast Report Basic Sanity Testing	
     */
    @Test(groups = "unit")
    public void specForecastReportSanityTest(){  
    	logger.info("Executing specForecastReportSanityTest...");
    
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.PAGE_LOAD, "salesforce.com");
		logger.info("Logging in...");
		loginPage = new LoginPage(driver,controller);	
		logoutPage = new LogOutPage(driver);
		loginPage.loginAs(LoginPage.getR6SysAdminUser(), LoginPage.getR6SysAdminUserPass());
		logger.info("Main Window Handle:" + driver.getWindowHandle());
		controller.updateWindowHandleMap("mainwindow", driver.getWindowHandle());
		specPage = new SpecPage(driver, controller);
		specPage.clickSpecialistForecastReportsTab();
		//check title
		Assert.assertEquals(specPage.getSpecialistForecastTitle(), SfdcConstants.SPECIALIST_FORECAST);
		//check default user
		Assert.assertEquals(specPage.getDefaultUser(), controller.getLogginUserLabel(driver));
		//check default fiscal year
		Assert.assertEquals(specPage.getFiscalYear(), O2QUtil.getCurrentFiscalYear());
		//check default fiscal quarter
		Assert.assertEquals(specPage.getFiscalQuarter(), O2QUtil.getCurrentQuarter());
		//check view report button present
		Assert.assertTrue(specPage.isViewReportButtonPresent());
		
    }  
    

    /**
     *  Specialist Forecast Report Body Sanity Testing 		
     */
    @Test(groups = "unit",dependsOnMethods={"specForecastReportSanityTest"}, alwaysRun=false)
    public void specForecastReportBodySanityTest(){  
    	logger.info("Executing specForecastReportBodySanityTest...");
    	
    	//check user with report header
		Assert.assertTrue(specPage.getSpecialistForecastHeader().contains(specPage.getDefaultUser()));
		//check report column headers
		Assert.assertEquals(specPage.getUserRoleColumn().trim(), SfdcConstants.USER_ROLE);
		Assert.assertEquals(specPage.getClosedColumn().trim(), SfdcConstants.CLOSED);
		Assert.assertEquals(specPage.getMyCommitColumn().trim(), SfdcConstants.MY_COMMIT);
		Assert.assertEquals(specPage.getMyDirectCommitColumn().trim(), SfdcConstants.MY_DIRECT_COMMIT);
		//Assert.assertEquals(specPage.getMyBestCaseColumn().trim(), SfdcConstants.MY_BEST_CASE);
		Assert.assertEquals(specPage.getMyDirectBestCaseColumn().trim(), SfdcConstants.MY_DIRECT_BEST_CASE);
		Assert.assertEquals(specPage.getPipelineColumn().trim(), SfdcConstants.PIPELINE);
		Assert.assertEquals(specPage.getQuotaColumn().trim(), SfdcConstants.QUOTA);
    }  
    
    /**
     *  Specialist Forecast Report Calculation Sanity Testing 		
     */
    @Test(groups = "unit",dependsOnMethods={"specForecastReportBodySanityTest"}, alwaysRun=false)
    public void specForecastReportCalculationSanityTest(){  
    	logger.info("Executing specForecastReportCalculationSanityTest...");
    	String tableLocatorId = "pg:spforecastForm:pgBlock:pgBlockSection2:forecastRows:tb";
		//check total of closed, commit, direct commit, best case, direct best case and pipeline
		Assert.assertTrue(controller.getTableCellSumValue(driver, tableLocatorId, 1)  == specPage.getTotalClosed());
		Assert.assertTrue(controller.getTableCellSumValue(driver, tableLocatorId, 2)  == specPage.getTotalMyCommit());
		Assert.assertTrue(controller.getTableCellSumValue(driver, tableLocatorId, 3)  == specPage.getTotalMyDirectCommit());
		Assert.assertTrue(controller.getTableCellSumValue(driver, tableLocatorId, 4)  == specPage.getTotalMyBestCase());
		Assert.assertTrue(controller.getTableCellSumValue(driver, tableLocatorId, 5)  == specPage.getTotalMyDirectBestCase());
		Assert.assertTrue(controller.getTableCellSumValue(driver, tableLocatorId, 6)  == specPage.getTotalPipeline());
		Assert.assertTrue(controller.getTableCellSumValue(driver, tableLocatorId, 7)  == specPage.getTotalQuota());
		
		driver.close();
    }  
    
   
}
