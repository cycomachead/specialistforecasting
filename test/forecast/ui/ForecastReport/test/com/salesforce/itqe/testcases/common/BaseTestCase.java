package com.salesforce.itqe.testcases.common;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import java.util.concurrent.TimeUnit;
import com.salesforce.it.qe.testng.SalesforceEnforcer;
import com.salesforce.itqe.controller.O2QController;
import com.salesforce.itqe.controller.Controller;


/**
 * 
 * @author bob
 * 
 */
@Listeners(SalesforceEnforcer.class)
public class BaseTestCase { 
	/* Initializing logger */
	private static Logger logger = LoggerFactory.getLogger(BaseTestCase.class);
	/* WebDriver Object */
	protected WebDriver driver;
	protected String baseUrl;
	protected String testEnv;
	protected O2QController controller;

	/**
	 * All basic setup for tests
	 */
	@BeforeClass
	public void setUp() {
		
		controller = new O2QController();
		String os = System.getProperty("os.name").toLowerCase();
		if (System.getProperty("webdriver.chrome.driver") == null) {
			if (os.indexOf("win") >= 0) {
				System.setProperty("webdriver.chrome.driver",
						Controller.WEBDRIVER_CHROME_DRIVER_WIN);
			} else {
				System.setProperty("webdriver.chrome.driver",
						Controller.WEBDRIVER_CHROME_DRIVER_LIN);
			}
		}
		logger.info("webdriver.chrome.driver :"
				+ System.getProperty("webdriver.chrome.driver"));

		if (System.getProperty("testenv") == null) {
			testEnv = Controller.DEFAULT_TEST_ENV;
		} else {
			testEnv = System.getProperty("testenv");
		}
		logger.info("Test ID Set To :" + controller.getTestID());
		logger.info("Test Environment Is: " + testEnv);
		baseUrl = "https://" + testEnv + ".soma.salesforce.com/";
		logger.info("Base Url: " + baseUrl);
		logger.info("Initial setup... is all done");
		
		initializeDriver();
		
	}

	/**
	 * initialize driver
	 */
	private void initializeDriver() {
		//initial driver
		 //FirefoxDriver(), ChromeDriver(), InternetExplorerDriver(), HtmlUnitDriver()
		 FirefoxProfile pf = new FirefoxProfile();
		 pf.setAssumeUntrustedCertificateIssuer(false);
		 driver = new FirefoxDriver(pf);
		
	     driver.manage().timeouts().implicitlyWait(Controller.GLOBAL_WAIT, TimeUnit.SECONDS);
	     driver.get(baseUrl); 
	}

}
