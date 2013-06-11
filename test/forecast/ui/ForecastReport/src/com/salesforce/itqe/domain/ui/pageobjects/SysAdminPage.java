package com.salesforce.itqe.domain.ui.pageobjects;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.itqe.controller.Controller;
import com.salesforce.itqe.controller.utils.LinkedProperties;


public class SysAdminPage {
	
	 private final WebDriver driver;
	 
	  private static Logger logger = LoggerFactory.getLogger(SysAdminPage.class); 
	  
	  private String orgID;
	  
	  private static String defaultPassword;
	  
	  private Controller controller;
	  
	  static{
			LinkedProperties conProps = new LinkedProperties();		
			try{
				logger.info("Loading userdata...");
				conProps.load(Controller.class.getResourceAsStream("common/userdata.properties"));
			
				defaultPassword = conProps.getProperty("defaultPassword");
			
				logger.info("Userdata has been loaded...");
				
			}catch(IOException ioe){
				logger.error("Could not load userdata.properties");
			}				
		}

	 public String getDefaultPassword() {
		return defaultPassword;
	}

	public void setDefaultPassword(String defaultPassword) {
		this.defaultPassword = defaultPassword;
	}

	public String getOrgID() {
		return orgID;
	}

	public void setOrgID(String orgID) {
		this.orgID = orgID;
	}

	public SysAdminPage(WebDriver driver, Controller controller) {
    	this.controller = controller; 
		this.driver = driver;

         // Check that we're on the right page.
         if (!driver.getTitle().startsWith("SysAdmin: Home ~ salesforce.com ")) {
             // Alternatively, we could navigate to the login page, perhaps logging out first
        	 
        	 logger.error("This is not a sys admin page, the programme will now terminate");
             throw new IllegalStateException("This is not the Sys Admin page");
         }
    }
	 
	/**
	 * 
	 * @param orgName
	 * @param username
	 * @param email
	 * @param defaultPassword
	 * @param editionUri
	 * @return
	 */
	public SignUpPage createOrg(String orgType, String editionUri) {
		
		logger.info("setting up your org");
		driver.findElement(By.linkText("Signup")).click();
		driver.findElement(By.id("p2")).clear();
		driver.findElement(By.id("p2")).sendKeys(generateOrgID());
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys(getOrgID() + "@test.com");
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys(getOrgID() + "@test.com");
		driver.findElement(By.id("editionUri")).clear();
		driver.findElement(By.id("editionUri")).sendKeys(editionUri);
		driver.findElement(By.id("p19")).clear();
		driver.findElement(By.id("p19")).sendKeys(getDefaultPassword());
		driver.findElement(By.id("p22")).click();
		driver.findElement(By.cssSelector("#bottomButtonRow > input[name=\"save\"]")).click();
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.XPATH, "/html/body/div/div[2]/table/tbody/tr/td[2]/form/div/div[2]/div[2]/table/tbody/tr/td[2]/p/a");
		driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/form/div/div[2]/div[2]/table/tbody/tr/td[2]/p/a")).click();
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.XPATH, "/html/body/div/div[2]/table/tbody/tr/td[2]/div[4]/div[2]/div[2]/table/tbody/tr[2]/td[2]/a");
		driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div[4]/div[2]/div[2]/table/tbody/tr[2]/td[2]/a")).click();
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.ID, "accountid");
		driver.findElement(By.id("accountid")).clear();
		driver.findElement(By.id("accountid")).sendKeys("");
		driver.findElement(By.name("set")).click();
		/* Need to check the error page here, if the user already existed an error message should be returned*/
		
		logger.info("Your org is ready now, congratulations!!");
		return new SignUpPage(driver);
	}
	
/**
 * 	
 * @param orgType
 * @param editionUri
 * @param accObjID
 * @return
 */
public SignUpPage createOrgAndSetAccount(String orgType, String editionUri, String accObjID) {
		
		logger.info("setting up your org");
		driver.findElement(By.linkText("Signup")).click();
		driver.findElement(By.id("p2")).clear();
		driver.findElement(By.id("p2")).sendKeys(generateOrgID());
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys(getOrgID() + "@test.com");
		driver.findElement(By.id("email")).clear();
		driver.findElement(By.id("email")).sendKeys(getOrgID() + "@test.com");
		driver.findElement(By.id("editionUri")).clear();
		driver.findElement(By.id("editionUri")).sendKeys(editionUri);
		controller.selectOption(driver, driver.findElement(By.id("p6")), "Generic Template");
		driver.findElement(By.id("p19")).clear();
		driver.findElement(By.id("p19")).sendKeys(getDefaultPassword());
		driver.findElement(By.id("p22")).click();
		driver.findElement(By.cssSelector("#bottomButtonRow > input[name=\"save\"]")).click();
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.XPATH, "/html/body/div/div[2]/table/tbody/tr/td[2]/form/div/div[2]/div[2]/table/tbody/tr/td[2]/p/a");
		/*Controller.waitUntilTime(driver, Controller.WAITQ4);
		logger.info("Waited for 45 sec and expecting that the acc select link now present...");*/
		driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/form/div/div[2]/div[2]/table/tbody/tr/td[2]/p/a")).click();
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.XPATH, "/html/body/div/div[2]/table/tbody/tr/td[2]/div[4]/div[2]/div[2]/table/tbody/tr[2]/td");
		logger.info("Waititng for choose 'Account' link to be enabled on org profile...");
		int totalTime = 5;
		while(!controller.waitUntilTimeForElement(driver, Controller.ElementBy.XPATH, "/html/body/div/div[2]/table/tbody/tr/td[2]/div[4]/div[2]/div[2]/table/tbody/tr[2]/td[2]/a",5,false) && totalTime <=120){
			driver.navigate().refresh();
			totalTime = totalTime + 5;
		}
	
		logger.info("Choose 'Account' link enabled after :" + totalTime + " seconds");
		driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div[4]/div[2]/div[2]/table/tbody/tr[2]/td[2]/a")).click();
		controller.waitUntilTime(driver, Controller.WAIT_2NDLOW);
		if(driver.getWindowHandles().size() == 2){
				logger.info("No.of Windows open:" + driver.getWindowHandles().size());
				controller.addNewWindowHandler(driver, "orgAccLinkWindow");
				driver.switchTo().window(controller.readTheWindowHandle("orgAccLinkWindow"));
			}
		logger.info("***************************Page Info****************************");
		logger.info(driver.getCurrentUrl());
		logger.info(driver.getTitle());
		logger.info("****************************************************************");
		controller.waitUntilTimeForElement(driver, Controller.ElementBy.ID, "accountid");
		driver.findElement(By.id("accountid")).clear();
		driver.findElement(By.id("accountid")).sendKeys(accObjID);
		driver.findElement(By.name("set")).click();
		controller.waitUntilTime(driver, Controller.WAIT_2NDLOW);
		if(driver.getWindowHandles().size() == 2){			
			logger.info("Switching to main window...");
			driver.switchTo().window(controller.readTheWindowHandle("orgAccLinkWindow")).close();
			driver.switchTo().window(controller.readTheWindowHandle("mainwindow"));
			controller.removeTheWindowHandle("orgAccLinkWindow");
			logger.info("No.of open windows:" + driver.getWindowHandles().size());
		}
		
		logger.info("Your org is ready now, congratulations!!");
		return new SignUpPage(driver);
	}
	
	/**
	 * 
	 */
	private String generateOrgID() {
		StringBuffer orgID = new StringBuffer("AutoOrg_");
		DateFormat formatter;
		Date date = new Date();
		formatter = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
		orgID.append(formatter.format(date)).append(Thread.currentThread().getId());
		setOrgID(orgID.toString());
		
		return getOrgID();
	}

	/**
	 * @return the controller
	 */
	public Controller getController() {
		return controller;
	}

	/**
	 * @param controller the controller to set
	 */
	public void setController(Controller controller) {
		this.controller = controller;
	}
	

}
