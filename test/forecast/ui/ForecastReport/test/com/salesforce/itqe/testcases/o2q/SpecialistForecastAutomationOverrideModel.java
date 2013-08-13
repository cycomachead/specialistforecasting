package com.salesforce.itqe.testcases.o2q;


import java.util.Set;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;


import com.salesforce.it.qe.testng.SalesforceEnforcer;
import com.salesforce.itqe.controller.Controller;
import com.salesforce.itqe.domain.ui.pageobjects.OverridePage;
import com.salesforce.itqe.domain.ui.pageobjects.LoginPage;
import com.salesforce.itqe.domain.ui.pageobjects.SpecPage;

import com.salesforce.itqe.testcases.common.BaseTestCase;
import com.salesforce.itqe.controller.utils.O2QUtil;
import com.salesforce.itqe.controller.utils.SfdcConstants;
import org.openqa.selenium.WebDriver;
/**
 * Automation Test on Specialist Forecast Report Override
 * @author bob
 *
 */
@Listeners(SalesforceEnforcer.class)
public class SpecialistForecastAutomationOverrideModel extends BaseTestCase{
	 /* Initializing logger */
    private static Logger logger = LoggerFactory.getLogger(SpecialistForecastAutomationOverrideModel.class); 
 
	/* LoginPage Object */
	LoginPage loginPage;
	/* LogoutPage Object */
	//LogOutPage logoutPage;
	/* SpecPage Object */
	SpecPage specPage;
	/* OverridePage Object */
	OverridePage overridePage;
    
    @BeforeClass    
    public void setUp(){    
    	super.setUp();
		controller.setTestID("specialist");
    }
    
    /**
     *  Specialist Forecast Report submit Testing	
     */
    //@Test(groups = "unit")
    public void specForecastReportSubmitTest(){  
    	logger.info("Executing specForecastReportSubmitTest...");
    
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.PAGE_LOAD, "salesforce.com");
		logger.info("Logging in...");
		loginPage = new LoginPage(driver,controller);	
		
		loginPage.loginAs(LoginPage.getSysAdminUser(), LoginPage.getSysAdminUserPass());
		logger.info("Main Window Handle:" + driver.getWindowHandle());
		controller.updateWindowHandleMap("mainwindow", driver.getWindowHandle());
		specPage = new SpecPage(driver, controller);
		specPage.clickSpecialistForecastReportsTab();
		
		logger.info("Click submit...");
		specPage.clickSubmit();
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		//check submit successfully
		Assert.assertTrue(specPage.getSubmitConfirmationMsg().contains(SfdcConstants.SUBMIT_MSG));
    }  
    
    /**
     *  Specialist Forecast Report Override Commit Testing	
     */
    //@Test(groups = "unit")
  /*  public void specForecastReportOverrideCommitTest(){  
    	logger.info("Executing specForecastReportOverrideCommitTest...");
    
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.PAGE_LOAD, "salesforce.com");
		logger.info("Logging in...");
		loginPage = new LoginPage(driver,controller);	
		//logoutPage = new LogOutPage(driver);
		loginPage.loginAs(LoginPage.getR6SysAdminUser(), LoginPage.getR6SysAdminUserPass());
		logger.info("Main Window Handle:" + driver.getWindowHandle());
		controller.updateWindowHandleMap("mainwindow", driver.getWindowHandle());
		specPage = new SpecPage(driver, controller);
		specPage.clickSpecialistForecastReportsTab();
		//Check different user, default year, quarter
		//specPage.enterData(LoginPage.getDevUser(), O2QUtil.getCurrentFiscalYear(), O2QUtil.getCurrentQuarter());
		//specPage.clickViewReport();
		logger.info("Click override icon...");
		specPage.clickOverrideCommit();
		
	
		  String parentWindowHandle = driver.getWindowHandle(); // save the current window handle.
	      WebDriver popup = null;
	      Set<String> set_win = driver.getWindowHandles();
	      Iterator<String> windowIterator = set_win.iterator();
	      while(windowIterator.hasNext()) { 
	        String windowHandle = windowIterator.next(); 
	        popup = driver.switchTo().window(windowHandle);
	     
	        if (popup.getTitle().equals("")) {
	          break;
	        }
	      }
		
		//controller.updateWindowHandleMap("mainwindow", driver.getWindowHandle());
		
		overridePage = new OverridePage(popup, controller);
		logger.info("Enter override data...");
		//overridePage.clickOverrideCommit();
		overridePage.enterOverrideData("10000", "test");
		
		//popup.close();
		//controller.updateWindowHandleMap("mainwindow", parentWindowHandle);
		
    }  */
    

    /**
     *  Specialist Forecast Report Override Best Case Testing 		
     */
    //@Test(groups = "unit",dependsOnMethods={"specForecastReportOverrideCommitTest"}, alwaysRun=false)
  /*  public void specForecastReportOverrideBestcaseTest(){  
    	logger.info("Executing specForecastReportOverrideBestcaseTest...");
    	
 
    }  */
    
   
}
