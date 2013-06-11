package com.salesforce.itqe.domain.ui.pageobjects;

import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.salesforce.itqe.controller.Controller;
import com.salesforce.itqe.controller.utils.LinkedProperties;
import com.salesforce.itqe.controller.utils.SfdcConstants;

/**
 * 
 * @author mnellore
 *
 */
public class LoginPage {
    private final WebDriver driver;

    private static Logger logger = LoggerFactory.getLogger(LoginPage.class); 
    
    private static String sysAdminUser;
    private static String sysAdminUserPass;
    private static String r6SysAdminUser;
    private static String r6SysAdminUserPass;
    private static String r6FS;
    private static String r6CS;
    private static String r6FSAgency;
    private static String r6CSAgency;
    private static String r6FSReseller;
    private static String r6CSReseller;
	private static String crmFS;
    private static String crmCS;
    private static String nmCSLocale;
    
    private static String securityTestUser;
    private static String securityTestUserPass;
    
    private static String o2qTestUser;
    private static String o2qTestUserPass;
    
    private String loggedInSysAdminUser;
    private String currentLoggedInUser;
    private static String devUser;
    private Controller controller;
    
    static{
		LinkedProperties conProps = new LinkedProperties();		
		try{
			logger.info("Loading userdata...");
			conProps.load(Controller.class.getResourceAsStream("common/userdata.properties"));
			sysAdminUser = conProps.getProperty("sysAdminUser");
			sysAdminUserPass = conProps.getProperty("sysAdminUserPass");
			r6SysAdminUser = conProps.getProperty("r6SysAdminUser");
			r6SysAdminUserPass = conProps.getProperty("r6SysAdminUserPass");
			r6FS = conProps.getProperty("r6FS");
			r6CS = conProps.getProperty("r6CS");
			r6FSAgency = conProps.getProperty("r6FSAgency");
			r6CSAgency = conProps.getProperty("r6CSAgency");
			r6FSReseller = conProps.getProperty("r6FSReseller");
			r6CSReseller = conProps.getProperty("r6CSReseller");
			crmFS = conProps.getProperty("crmFS");
			crmCS = conProps.getProperty("crmCS");
			nmCSLocale = conProps.getProperty("nmCSLocale");
			
			securityTestUser = conProps.getProperty("securityTestUser");
			securityTestUserPass = conProps.getProperty("securityTestUserPass");
			
			o2qTestUser = conProps.getProperty("o2qTestUser");
			o2qTestUserPass = conProps.getProperty("o2qTestUserPass");
			devUser = conProps.getProperty("devUser");
			logger.info("Userdata has been loaded...");
			
		}catch(IOException ioe){
			logger.error("Could not load userdata.properties");
		}				
	}

	public LoginPage(WebDriver driver, Controller controller) {
        this.driver = driver;
        this.controller = controller;
    }

    /**
     * 
     * @param username
     * @param password
     * @return
     */
    public Boolean loginAs(String username, String password) {
        // This is the only place in the test code that "knows" how to enter these details
          /*Identify the elements to interact with */     
    	  logger.info("Logging In as " + "{" + username + "}");    	  
          WebElement user = driver.findElement(By.id("username"));
          WebElement pass = driver.findElement(By.id("password"));
          WebElement login = driver.findElement(By.id("Login"));
          
          user.sendKeys(username);
          pass.sendKeys(password);
          
          login.click();  
          /**
           * Logic to validate successful login should be here
           */
          //Controller.waitUntilTimeForElement(driver, Controller.ElementBy.PAGE_LOAD, "salesforce.com");
          logger.info("-Sign In Successful- for the user{" + username +"}");
          
          setLoggedInSysAdminUser(username);
          setCurrentLoggedInUser(username);
          
        return true;
    }
    
    /**
     * 
     * @param userNameStr
     * @return
     */
    public Boolean loginAs(String userNameString){
    	OpenTab openTab = new OpenTab();
    	openTab.homePageTab(driver);
    	Assert.assertTrue(controller.lookForObject(driver, userNameString + " " + userNameString), "User '" + userNameString + "' Not Found");
    	driver.findElement(By.cssSelector("#USER_DETAIL > span")).click();
    	driver.findElement(By.name("login")).click();
    	logger.info("-Logged in as user{" + userNameString +"}");    	
    	setCurrentLoggedInUser(userNameString);
    	
    	return true;
    }
    
    /**
     * 
     * @param userPageObject
     * @param userNameString
     * @return
     */
    public Boolean changeUserSettingsAndLogin(String userPageObject, String userNameString){
    	OpenTab openTab = new OpenTab();
    	openTab.homePageTab(driver);
    	Assert.assertTrue(controller.lookForObject(driver, userNameString + " " + userNameString), "User '" + userNameString + "' Not Found");
    	controller.populate(userPageObject, driver);
    	logger.info("-Logged in as user{" + userNameString +"}");    	
    	setCurrentLoggedInUser(userNameString);
    	
    	return true;
    }
    
    

	public static String getSysAdminUser() {
		return sysAdminUser;
	}

	public static void setSysAdminUser(String sysAdminUser) {
		LoginPage.sysAdminUser = sysAdminUser;
	}

	public static String getSysAdminUserPass() {
		return sysAdminUserPass;
	}

	public static void setSysAdminUserPass(String sysAdminUserPass) {
		LoginPage.sysAdminUserPass = sysAdminUserPass;
	}
	
	public static String getR6SysAdminUser() {
		return r6SysAdminUser;
	}

	public static void setR6SysAdminUser(String r6SysAdminUser) {
		LoginPage.r6SysAdminUser = r6SysAdminUser;
	}

	public static String getR6SysAdminUserPass() {
		return r6SysAdminUserPass;
	}

	public static void setR6SysAdminUserPass(String r6SysAdminUserPass) {
		LoginPage.r6SysAdminUserPass = r6SysAdminUserPass;
	}

	public static String getR6FS() {
		return r6FS;
	}

	public static void setR6FS(String r6fs) {
		r6FS = r6fs;
	}

	public static String getR6CS() {
		return r6CS;
	}

	public static void setR6CS(String r6cs) {
		r6CS = r6cs;
	}

	public static String getR6FSAgency() {
		return r6FSAgency;
	}

	public static void setR6FSAgency(String r6fsAgency) {
		r6FSAgency = r6fsAgency;
	}

	public static String getR6CSAgency() {
		return r6CSAgency;
	}

	public static void setR6CSAgency(String r6csAgency) {
		r6CSAgency = r6csAgency;
	}

	public static String getR6FSReseller() {
		return r6FSReseller;
	}

	public static void setR6FSReseller(String r6fsReseller) {
		r6FSReseller = r6fsReseller;
	}
	
	public static String getR6CSReseller() {
		return r6CSReseller;
	}

	public static void setR6CSReseller(String r6csReseller) {
		r6CSReseller = r6csReseller;
	}

	public static String getCrmFS() {
		return crmFS;
	}

	public static void setCrmFS(String crmFS) {
		LoginPage.crmFS = crmFS;
	}

	public static String getCrmCS() {
		return crmCS;
	}

	public static void setCrmCS(String crmCS) {
		LoginPage.crmCS = crmCS;
	}
	
	/**
	 * @return the nmCSLocale
	 */
	public static String getNmCSLocale() {
		return nmCSLocale;
	}

	/**
	 * @param nmCSLocale the nmCSLocale to set
	 */
	public static void setNmCSLocale(String cnCSLocale) {
		LoginPage.nmCSLocale = cnCSLocale;
	}
	
	public String getLoggedInSysAdminUser() {
		return loggedInSysAdminUser;
	}

	public void setLoggedInSysAdminUser(String loggedInSysAdminUser) {
		this.loggedInSysAdminUser = loggedInSysAdminUser;
	}

	public String getCurrentLoggedInUser() {
		return currentLoggedInUser;
	}

	public void setCurrentLoggedInUser(String currentLoggedInUser) {
		this.currentLoggedInUser = currentLoggedInUser;
	}
	
	public static String getSecurityTestUser() {
		return securityTestUser;
	}

	public static void setSecurityTestUser(String securityTestUser) {
		LoginPage.securityTestUser = securityTestUser;
	}

	public static String getSecurityTestUserPass() {
		return securityTestUserPass;
	}

	public static void setSecurityTestUserPass(String securityTestUserPass) {
		LoginPage.securityTestUserPass = securityTestUserPass;
	}
	
	public static String getO2QTestUser() {
		return o2qTestUser;
	}

	public static void setO2QTestUser(String o2qTestUser) {
		LoginPage.o2qTestUser = o2qTestUser;
	}

	public static String getO2QTestUserPass() {
		return o2qTestUserPass;
	}

	public static void setO2QTestUserPass(String o2qTestUserPass) {
		LoginPage.o2qTestUserPass = o2qTestUserPass;
	}
	
	public static String getDevUser() {
		return devUser;
	}

	public static void setDevUser(String devUser) {
		LoginPage.devUser = devUser;
	}
	
	/**
	 * check ECOMM tab not Present
	 */
	public Boolean isEcommTabPresent() {
		
		boolean _found = false;
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		try {
			if (driver.findElement(By.linkText(SfdcConstants.ECOMM_TAB)) != null)
				_found = true;
			
		} catch (Exception e) {
			;//do nothing
		}
		
		return _found;
	}
	
}