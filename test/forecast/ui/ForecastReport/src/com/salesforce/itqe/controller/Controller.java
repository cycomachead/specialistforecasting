/**
 * 
 */
package com.salesforce.itqe.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.salesforce.itqe.controller.utils.LinkedProperties;
import com.salesforce.itqe.domain.ui.pageobjects.OpportunityPage.OPPTY_TYPE;

/**
 * @author mnellore
 * 
 */
public class Controller {
	
	private static Logger logger = LoggerFactory.getLogger(Controller.class);	
	
	public static int WAITQ1;
	public static int WAITQ2;
	public static int WAITQ3;
	public static int WAITQ4;
	public static int WAIT_LOW;
	public static int WAIT_2NDLOW;
	public static int WAIT_MAX;
	// Global default wait time
	public static Integer GLOBAL_WAIT;
	
	public static String WEBDRIVER_CHROME_DRIVER_WIN;
	public static String WEBDRIVER_CHROME_DRIVER_LIN;
	public static String DEFAULT_TEST_ENV;
		
/**
 * Controller 	
 */	
	static{
		LinkedProperties conProps = new LinkedProperties();		
		try{
			logger.info("Loading controller properties...");
			conProps.load(Controller.class.getResourceAsStream("common/controller.properties"));			
			WAITQ1 = Integer.parseInt(conProps.getProperty("WAITQ1"));
			WAITQ2 = Integer.parseInt(conProps.getProperty("WAITQ2"));
			WAITQ3 = Integer.parseInt(conProps.getProperty("WAITQ3"));
			WAITQ4 = Integer.parseInt(conProps.getProperty("WAITQ4"));
			WAIT_LOW = Integer.parseInt(conProps.getProperty("WAIT_LOW"));
			WAIT_2NDLOW = Integer.parseInt(conProps.getProperty("WAIT_2NDLOW"));
			WAIT_MAX = Integer.parseInt(conProps.getProperty("WAIT_MAX"));
			GLOBAL_WAIT = Integer.parseInt(conProps.getProperty("GLOBAL_WAIT"));
			WEBDRIVER_CHROME_DRIVER_WIN = conProps.getProperty("webdriver_chrome_driver_win");
			WEBDRIVER_CHROME_DRIVER_LIN = conProps.getProperty("webdriver_chrome_driver_lin");
			DEFAULT_TEST_ENV = conProps.getProperty("default_test_env");			
		}catch(IOException ioe){
			logger.error("Could not load controller.properties");
		}				
	}
	
	public enum ElementBy {
		ID, LINK, NAME, CSS_LOCATOR, XPATH, PAGE_LOAD, DUMMY_DUMMY
	};
	
	private ElementBy elementByLocator;
	private String elementWaitAction;
	private Map<String, String> windowHandlerMap = new HashMap<String, String>();
	
	private OPPTY_TYPE opttyType = OPPTY_TYPE.NB;
	

	private String testID;
	
	private String modelGroup;
	
	private String accountId;
	
	private String opportunityID;
	
	private String quoteID;
	
	private String extraTestString;

	public String getOpportunityID() {
		return opportunityID;
	}

	public void setOpportunityID(String opportunityID) {
		this.opportunityID = opportunityID;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	
	public OPPTY_TYPE getOpttyType() {
		return opttyType;
	}

	public void setOpttyType(OPPTY_TYPE opttyType) {
		this.opttyType = opttyType;
	}

	public String getQuoteID() {
		return quoteID;
	}

	public void setQuoteID(String quoteID) {
		this.quoteID = quoteID;
	}
	
	public String getTestID() {
		return testID;
	}

	public void setTestID(String testID) {
		this.testID = testID;
	}
	
	/**
	 * @return the extraTestString
	 */
	public String getExtraTestString() {
		return extraTestString;
	}

	/**
	 * @param extraTestString the extraTestString to set
	 */
	public void setExtraTestString(String extraTestString) {
		this.extraTestString = extraTestString;
	}
	
	/**
	 * @return the modelGroup
	 */
	public String getModelGroup() {
		return modelGroup;
	}

	/**
	 * @param modelGroup the modelGroup to set
	 */
	public void setModelGroup(String modelGroup) {
		this.modelGroup = modelGroup;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void updateWindowHandleMap(String key, String value) {
		windowHandlerMap.put(key, value);
		displayWindowHandleMap();
	}

	/**
	 * 
	 * @param windowHandleToCheck
	 * @return
	 */
	public Boolean isTheWindowHandleOpen(String windowHandleToCheck) {

		return windowHandlerMap.containsKey(windowHandleToCheck);
	}

	/**
	 * 
	 * @param windowHandleToRead
	 * @return
	 */
	public String readTheWindowHandle(String windowHandleToRead) {

		return windowHandlerMap.get(windowHandleToRead);
	}

	/**
	 * 
	 * @param windowHandleToRemove
	 * @return
	 */
	public String removeTheWindowHandle(String windowHandleToRemove) {

		return windowHandlerMap.remove(windowHandleToRemove);
	}

	/**
	 * 
	 */
	public void displayWindowHandleMap() {
		logger.info("displayWindowHandleMap:ElementInWindowHandlerMap ==> {" + Arrays.deepToString(windowHandlerMap.keySet()
				.toArray()) + "}");
	}	
	
	/**
	 * 
	 * @param driver
	 * @return
	 */
	public Boolean waitUntilAllWindowHandlesCloseExceptMain(WebDriver driver) {		
		logger.info("waitUntilAllWindowHandlesCloseExceptMain..." + "waiting time is upto {" + GLOBAL_WAIT + "}seconds");
		try {
			(new WebDriverWait(driver, GLOBAL_WAIT))
					.until(new ExpectedCondition<Boolean>() {
						public Boolean apply(WebDriver driver) {
							present = false;
							try {
								logger.info("Open Handles Count : " + driver.getWindowHandles().size());
								if (driver.getWindowHandles().size() == 1){
									present = true;
								}

							} catch (Exception e) {

							}
							return present;
						}
					});
		} catch (Exception e) {
			logger.info("waitUntilAllWindowHandlesCloseExceptMain - is not successful");
		}
		return present;

	}
	
	/**
	 * 
	 * @param driver
	 * @return
	 */
	public Boolean waitAndAcceptAlert(WebDriver driver) {		
		logger.info("waitAndAcceptAlert..." + "waiting time is upto {" + GLOBAL_WAIT + "}seconds");
		try {
			(new WebDriverWait(driver, GLOBAL_WAIT))
					.until(new ExpectedCondition<Boolean>() {
						public Boolean apply(WebDriver driver) {
							present = false;
							try {
								logger.info("Alert: Message :");
								try{
								logger.info(driver.switchTo().alert().getText());
								}catch(Exception e){
									
								}
								driver.switchTo().alert().accept();
								logger.info("*** Alert: Accepted ***");
								present = true;
							} catch (Exception e) {
								
							}
							return present;
						}
					});
		} catch (Exception e) {
			logger.info("waitAndAcceptAlert - is not successful");
			e.printStackTrace();
		}
		return present;

	}

	/**
	 * 
	 * @param pageObject
	 * @param driver
	 * @return
	 */
	public Boolean populate(String pageObject, WebDriver driver){
		
		return populate(pageObject, driver, "");
		
	}
	
	/**
	 * 
	 * @param pageObject
	 * @param driver
	 * @return
	 */
	public Boolean populate(String pageObject, WebDriver driver, String propLoc){

		LinkedProperties uiModelProp = new LinkedProperties();
		LinkedProperties uiTestDataProp = new LinkedProperties();
		String uiModelPropFile = pageObject + "_ui_model.properties";
		String uiTestDataPropFile = pageObject + "_ui_testdata.properties";	
		Boolean isModelFileSet = false;
		Boolean isTestDataFileSet = false;
		
		logger.info("Identifying Model file location for, " + uiModelPropFile);
		
		if(propLoc!=null && !propLoc.equalsIgnoreCase("")){
			logger.info("Properties Location has been set as:" + propLoc + ". So lets try searching the file in this location...");
			logger.info("Trying to load ui-model properties file..." + propLoc + "/" + uiModelPropFile);
			try{
			uiModelProp.load(Controller.class.getResourceAsStream(propLoc + "/" + uiModelPropFile));
			logger.info("=============>>>>>>>>Loaded ui-model properties file successful..." + propLoc + "/" + uiModelPropFile);
			isModelFileSet = true;
			}catch(Exception e){
				//logger.info("Unable to load ui-model properties file..." + propLoc + "/" + uiModelPropFile);
			}
			/**
			 * if model file found in propLoc then we should search the testdata file in the same location otherwise do not look for the testdata file in this location
			 */
			if(isModelFileSet){
				logger.info("Trying to load ui-testdata properties file..." + propLoc + "/" + uiTestDataPropFile);
				try{
					uiTestDataProp.load(Controller.class.getResourceAsStream(propLoc + "/" + uiTestDataPropFile));
					logger.info("=============>>>>>>>>Loaded ui-testdata properties file successful..." + propLoc + "/" + uiTestDataPropFile);
					isTestDataFileSet = true;
					}catch(Exception e){
						//logger.info("Unable to load ui-testdata properties file..." + propLoc + "/" + uiTestDataPropFile);
					}
			}
		}
		
		//Find Model file in ModelGroup location
		if(!isModelFileSet && getModelGroup()!=null && !getModelGroup().equalsIgnoreCase("")){			
			logger.info("Trying to load ui-model properties file from the ModelGroup..." + getModelGroup() + "/" + uiModelPropFile);
			try{
			uiModelProp.load(Controller.class.getResourceAsStream(propLoc + "/" + uiModelPropFile));
			logger.info("=============>>>>>>>>Loaded ui-model properties file successful..." + propLoc + "/" + uiModelPropFile);
			isModelFileSet = true;
			}catch(Exception e){
				//logger.info("Unable to load ui-model properties file from the ModelGroup..." + getModelGroup() + "/" + uiModelPropFile);
			}			
		}
		
		//Find Model file in testID location and common location as a last attempt 		 		
		if(!isModelFileSet){
			if(getExtraTestString()!=null && !getExtraTestString().equalsIgnoreCase("")){
				try{
					logger.info("Trying to load ui-model properties file with extra string that is set as..." + testID + "/" + getExtraTestString() + "_" + uiModelPropFile);
					uiModelProp.load(Controller.class.getResourceAsStream(testID + "/" + getExtraTestString() + "_" + uiModelPropFile));
					logger.info("=============>>>>>>>>Loaded ui-model properties file successful with corresponding extra string..." + testID + "/" + getExtraTestString() + "_" + uiModelPropFile);
					isModelFileSet = true;
					}catch(Exception e){
						//logger.info("Unable to load ui-model properties file successful with corresponding extra string..." + testID + "/" + getExtraTestString() + "_" + uiModelPropFile);
					}
			}
			
			if(!isModelFileSet){
				logger.info("Trying to load ui-model properties file..." + testID + "/" + uiModelPropFile);
				try{
					uiModelProp.load(Controller.class.getResourceAsStream(testID + "/" + uiModelPropFile));
					logger.info("=============>>>>>>>>Loaded ui-model properties file successful..." + testID + "/" + uiModelPropFile);
					isModelFileSet = true;
					}catch(Exception e){
						//logger.info("Unable to load ui-model properties file..." + testID + "/" + uiModelPropFile);
					}
			}			
				
			if(!isModelFileSet && !propLoc.equalsIgnoreCase("common")){
				logger.info("Finally trying to load ui-model properties file... common" + "/" + uiModelPropFile);
				try{
					uiModelProp.load(Controller.class.getResourceAsStream("common" + "/" + uiModelPropFile));
					logger.info("=============>>>>>>>>Loaded ui-model properties file successful..." + "common" + "/" + uiModelPropFile);
					isModelFileSet = true;
					}catch(Exception e){
						//logger.info("Unable to load ui-model properties file..." + "common" + "/" + uiModelPropFile);
					}				
			}
		}
		
		
		//Find TestData file in testID location and common location as a last attempt , if not found return false		 	
		if(!isModelFileSet){
			logger.info("Unable to locate the model properties file....." + uiModelPropFile + " under any known locations propLoc=" + propLoc + ";modelGroup=" + getModelGroup() + ";" + "testId=" + testID);
			return false;
		}else{
			if(!isTestDataFileSet){
				
				if(getExtraTestString()!=null && !getExtraTestString().equalsIgnoreCase("")){
					try{
						logger.info("Trying to load ui-testdata properties file with corresponding extra string..." + testID + "/" + getExtraTestString() + "_" + uiTestDataPropFile);
						uiTestDataProp.load(Controller.class.getResourceAsStream(testID + "/" + getExtraTestString() + "_" + uiTestDataPropFile));
						logger.info("=============>>>>>>>>Loaded ui-testdata properties file successful with corresponding extra string..." + testID + "/" + getExtraTestString() + "_" + uiTestDataPropFile);
						isTestDataFileSet = true;
						}catch(Exception e){
							//logger.info("Unable to load ui-testdata properties file with corresponding extra string..." + testID + "/" + getExtraTestString() + "_" + uiTestDataPropFile);
						}	
				}
				
				if(!isTestDataFileSet){
					logger.info("Trying to load ui-testdata properties file..." + testID + "/" + uiTestDataPropFile);
					try{
						uiTestDataProp.load(Controller.class.getResourceAsStream(testID + "/" + uiTestDataPropFile));
						logger.info("=============>>>>>>>>Loaded ui-testdata properties file successful..." + testID + "/" + uiTestDataPropFile);
						isTestDataFileSet = true;
						}catch(Exception e){
							//logger.info("Unable to load ui-testdata properties file..." + testID + "/" + uiTestDataPropFile);
						}
				}
				
					
				if(!isTestDataFileSet && !propLoc.equalsIgnoreCase("common")){
					logger.info("Trying to load ui-testdata properties file..." + "common" + "/" + uiTestDataPropFile);
					try{
						uiTestDataProp.load(Controller.class.getResourceAsStream("common" + "/" + uiTestDataPropFile));
						logger.info("=============>>>>>>>>Loaded ui-testdata properties file successful..." + "common" + "/" + uiTestDataPropFile);
						isTestDataFileSet = true;
						}catch(Exception e){
							//logger.info("Unable to load ui-testdata properties file..." + "common" + "/" + uiTestDataPropFile);
						}
				}else if(!isTestDataFileSet){
					logger.info("Unable to locate the model properties file....." + uiModelPropFile + " under any known locations propLoc=" + propLoc + ";modelGroup=" + getModelGroup() + ";" + "testId=" + testID);
					return false;
				}
			}
		}
		
		
		/*if(getModelGroup()!=null && !getModelGroup().equalsIgnoreCase("")){
			try{
				uiModelPropFile = pageObject + "_ui_model.properties";
				uiTestDataPropFile = pageObject + "_ui_testdata.properties";				
				logger.info("Loading ui-model properties file..." + getModelGroup() + "/" + uiModelPropFile);
				try{
					uiModelProp.load(Controller.class.getResourceAsStream(getModelGroup() + "/" + uiModelPropFile));
					logger.info("The model properties file is found successfully under..." + getModelGroup() + "/" + uiModelPropFile);
				}catch(Exception e){
					logger.info("The model properties file not found under..." + getModelGroup() + "/" + uiModelPropFile);
				}
				logger.info("Loading ui-testdata properties file..." + getModelGroup() + "/" + uiTestDataPropFile);
				uiTestDataProp.load(Controller.class.getResourceAsStream(getModelGroup() + "/" + uiTestDataPropFile));
				logger.info("The model group is set as:'" + getModelGroup() + "', And the files were found successfully");
			}catch(Exception e1){
				logger.info("No the properties files are not part of the common folder hence retrurning false to stop the process...");
				e1.printStackTrace();
				return false;
			}
		}*/
		
		/*if(propLoc.equalsIgnoreCase("common")){
			logger.info("PopLoc is set as: " + propLoc + " -- special case for example opportunity creation in aps environments: record type is visible for AE");
			try{
				uiModelPropFile = pageObject + "_ui_model.properties";
				uiTestDataPropFile = pageObject + "_ui_testdata.properties";				
				logger.info("Loading ui-model properties file..." + "common" + "/" + uiModelPropFile);
				uiModelProp.load(Controller.class.getResourceAsStream("common" + "/" + uiModelPropFile));	
				logger.info("Loading ui-testdata properties file..." + "common" + "/" + uiTestDataPropFile);
				uiTestDataProp.load(Controller.class.getResourceAsStream("common" + "/" + uiTestDataPropFile));
				logger.info("Yes the properties files are part of common folder and they were loaded properly so no issues...");
			}catch(Exception e1){
				logger.info("No the properties files are not part of the common folder hence retrurning false to stop the process...");
				e1.printStackTrace();
				return false;
			}			
		}else{
			try{
				logger.info("Loading ui-model properties file..." + testID + "/" + uiModelPropFile);
				uiModelProp.load(Controller.class.getResourceAsStream(testID + "/" + uiModelPropFile));	
				logger.info("Loading ui-testdata properties file..." + testID + "/" + uiTestDataPropFile);
				uiTestDataProp.load(Controller.class.getResourceAsStream(testID + "/" + uiTestDataPropFile));
				}catch(Exception e){
					logger.info("Error while loading the properties files...");
					logger.error(e.getMessage());
					logger.info("It may be possible that the specific test version file is not exists so I will check in common folder if it is part of common files bofore I exist the process...");
					
					try{
						uiModelPropFile = pageObject + "_ui_model.properties";
						uiTestDataPropFile = pageObject + "_ui_testdata.properties";				
						logger.info("Loading ui-model properties file..." + "common" + "/" + uiModelPropFile);
						uiModelProp.load(Controller.class.getResourceAsStream("common" + "/" + uiModelPropFile));	
						logger.info("Loading ui-testdata properties file..." + "common" + "/" + uiTestDataPropFile);
						uiTestDataProp.load(Controller.class.getResourceAsStream("common" + "/" + uiTestDataPropFile));
						logger.info("Yes the properties files are part of common folder and they were loaded properly so no issues...");
					}catch(Exception e1){
						logger.info("No the properties files are not part of the common folder hence retrurning false to stop the process...");
						e1.printStackTrace();
						return false;
					}			
				}
		}*/

		for (String key : uiModelProp.stringPropertyNames()) {
			String value = uiModelProp.getProperty(key);
			logger.info(key + " ==> " + value);
			
			StringTokenizer valueToken = new StringTokenizer(value,"$$");			
			String fieldType = null;
			String findByText =null;
			String findBy = null;
			String additionalType;
			String additionalStr;	
			WebElement element = null;
			
			if(valueToken.countTokens() >= 3){
				fieldType = valueToken.nextToken();
				findByText = valueToken.nextToken();
				findBy = valueToken.nextToken();
				
				element = getWebElement(driver, findByText, findBy);
				
				if(fieldType.equalsIgnoreCase("TEXT")){
					logger.info("getTagName():" + element.getTagName() + ";getText():" + element.getText());
					element.clear(); 
					if(key.equalsIgnoreCase("accountName") && uiTestDataProp.getProperty(key).equalsIgnoreCase("method.generate")){
						element.sendKeys(getAccountId());
					}else if(key.equalsIgnoreCase("opptyName") && uiTestDataProp.getProperty(key).equalsIgnoreCase("method.generate")){
						element.sendKeys(getOpportunityID());
					}else{
						element.sendKeys(uiTestDataProp.getProperty(key));
					}				
				}else if(fieldType.equalsIgnoreCase("CLEAR")){
					element.clear();
				}else if(fieldType.equalsIgnoreCase("CLICK")){
					element.click();
				}else if(fieldType.equalsIgnoreCase("BUTTON")){
					element.click();
				}else if(fieldType.equalsIgnoreCase("CHECKBOX")){
					if(findBy.equalsIgnoreCase("ID")){
						driver.findElement(By.id(findByText)).click();
						logger.info("CHECKBOX is Selected: " + driver.findElement(By.id(findByText)).isSelected());
					}else if(findBy.equalsIgnoreCase("NAME")){
						driver.findElement(By.name(findByText));						
						logger.info("CHECKBOX is Selected: " + driver.findElement(By.name(findByText)).isSelected());
					}else if(findBy.equalsIgnoreCase("CSS")){
						driver.findElement(By.cssSelector(findByText));
						logger.info("CHECKBOX is Selected: " + driver.findElement(By.cssSelector(findByText)).isSelected());
					}else if(findBy.equalsIgnoreCase("LINK")){
						driver.findElement(By.linkText(findByText));
						logger.info("CHECKBOX is Selected: " + driver.findElement(By.linkText(findByText)).isSelected());
					}else if(findBy.equalsIgnoreCase("XPATH")){
						driver.findElement(By.xpath(findByText));
						logger.info("CHECKBOX is Selected: " + driver.findElement(By.xpath(findByText)).isSelected());
					}else if(findBy.equalsIgnoreCase("TAGNAME")){
						driver.findElement(By.tagName(findByText));
						logger.info("CHECKBOX is Selected: " + driver.findElement(By.tagName(findByText)).isSelected());
					}
					/*element.click();
					logger.info("CHECKBOX element.isSelected() :" + element.isSelected());*/
				}else if(fieldType.equalsIgnoreCase("SELECT")){
					if(!selectOption(driver, element, uiTestDataProp.getProperty(key))){
						selectOptionByVisibleText(driver, element, uiTestDataProp.getProperty(key));
					}
				}else if(fieldType.equalsIgnoreCase("RENEWAL_POPUP")){
					logger.info("Looking for RENEWAL_POPUP...");
					if(addNewWindowHandler(driver, "renewalpopup")){
						logger.info("...RENEWAL_POPUP found.");
						driver.switchTo().window(readTheWindowHandle("renewalpopup"));
						if(findByText.equalsIgnoreCase("DISMISS")){
							driver.findElement(By.id("allBox")).click();
							driver.findElement(By.id("dismiss_all")).click();
							driver.switchTo().window(readTheWindowHandle("mainwindow"));
							removeTheWindowHandle("renewalpopup");
							logger.info("...DISMISSED all the RENEWAL_POPUP alert messages.");
						}
					}
					
				}else if(fieldType.equalsIgnoreCase("VALIDATE")){					
					if(element.getTagName().equals("select")){
						logger.info("VALIDATE: inside select");						
						if(new Select(element).getFirstSelectedOption().getText().trim().startsWith(uiTestDataProp.getProperty(key))){
							logger.info("VALIDATION PASS: Actual:<" + new Select(element).getFirstSelectedOption().getText() + ">**Expected:<" + uiTestDataProp.getProperty(key) + ">");
						}else{
							logger.info("VALIDATION FAIL: Actual:<" + new Select(element).getFirstSelectedOption().getText() + ">**Expected:<" + uiTestDataProp.getProperty(key) + ">");
							return false;
						}
					}else if(element.getText().startsWith(uiTestDataProp.getProperty(key)) || (stringToUnicode(element.getText().trim()).startsWith(uiTestDataProp.getProperty(key)))){  
							logger.info("VALIDATION PASS: Actual:<" + stringToUnicode(element.getText().trim()) + ">**Expected:<" + uiTestDataProp.getProperty(key) + ">");
					}else{					
							logger.info("VALIDATION FAIL: Actual:<" + element.getText() + ">**Expected:<" + uiTestDataProp.getProperty(key) + ">");
						return false;					}
				}else if (fieldType.equalsIgnoreCase("COMMON_PICKLIST")){
				}
			}
			
			if(valueToken.countTokens() == 5 || valueToken.countTokens() == 2){
				additionalType = valueToken.nextToken();
				additionalStr = valueToken.nextToken();
				if(additionalType.equalsIgnoreCase("ALERT")){
					if(additionalStr.equalsIgnoreCase("ACCEPT")){
						driver.switchTo().alert().accept();
					}else{
						driver.switchTo().alert().dismiss();
					}
				}else if(additionalType.startsWith("WAIT")){
					if(!waitForElement(driver,additionalType,additionalStr)){
						return false;
					}
				}else if (additionalType.equalsIgnoreCase("VALIDATE_PICKLIST")){
					if(validateCommonPickList(driver,element,additionalStr)==false){
						return false;
					}
				}
			}
			
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param strToUnicode
	 * @return
	 */
	public String stringToUnicode(String strToUnicode){
			logger.info("Original Language String before Unicode convertion	:" + strToUnicode);
			char[] characters = strToUnicode.toCharArray();
			StringBuffer sbStrToUnicode = new StringBuffer("");
			for (int i = 0; i < characters.length; i++) {
			char c = characters[i];			
			sbStrToUnicode.append((int) c);
			sbStrToUnicode.append(" ");
			}		
			logger.info("Converted Unicode String	:" + sbStrToUnicode.toString());
			return sbStrToUnicode.toString();
		}
	
	/**
	 * 
	 * @param driver
	 * @param waitType
	 * @param waitStr
	 */
	public Boolean waitForElement(WebDriver driver,String waitType, String waitStr){
		int waitTimeSec = 0;
		if(waitStr.startsWith("WAITQ")){
			if(waitStr.equalsIgnoreCase("WAITQ1")){
				waitTimeSec = WAITQ1;
			}else if(waitStr.equalsIgnoreCase("WAITQ2")){
				waitTimeSec = WAITQ2;
			}else if(waitStr.equalsIgnoreCase("WAITQ3")){
				waitTimeSec = WAITQ3;
			}else if(waitStr.equalsIgnoreCase("WAITQ4")){
				waitTimeSec = WAITQ4;
			}		
		}else if(waitStr.equalsIgnoreCase("WAIT_2NDLOW")){
			waitTimeSec = WAIT_2NDLOW;
		}else if(waitStr.equalsIgnoreCase("WAIT_LOW")){
			waitTimeSec = WAIT_LOW;
		}else if(waitStr.equalsIgnoreCase("WAIT_MAX")){
			waitTimeSec = WAIT_MAX;
		}	
		if(waitType.equalsIgnoreCase("PAGE_LOAD_WAIT")){
			return waitUntilTimeForElement(driver, Controller.ElementBy.PAGE_LOAD, waitStr, Controller.WAITQ4, false);
		}else if(waitType.equalsIgnoreCase("WAIT_SEC")){
			return waitUntilTime(driver,waitTimeSec);
		}else if(waitType.equalsIgnoreCase("WAIT_ELEMENT_LINK")){
			return waitUntilTimeForElement(driver, Controller.ElementBy.LINK, waitStr, Controller.WAITQ4, false);
		}else if(waitType.equalsIgnoreCase("WAIT_ELEMENT_ID")){
			return waitUntilTimeForElement(driver, Controller.ElementBy.ID, waitStr, Controller.WAITQ4, false);
		}else if(waitType.equalsIgnoreCase("WAIT_ELEMENT_NAME")){
			return waitUntilTimeForElement(driver, Controller.ElementBy.NAME, waitStr, Controller.WAITQ4, false);
		}else if(waitType.equalsIgnoreCase("WAIT_ELEMENT_CSS")){
			return waitUntilTimeForElement(driver, Controller.ElementBy.CSS_LOCATOR, waitStr, Controller.WAITQ4, false);
		}else if(waitType.equalsIgnoreCase("WAIT_ELEMENT_XPATH")){
			return waitUntilTimeForElement(driver, Controller.ElementBy.XPATH, waitStr, Controller.WAITQ4, false);
		}
		
		return false;
	}
	
	/**
	 * 
	 * @param driver
	 * @param findByText
	 * @param findBy
	 * @return
	 */
	public WebElement getWebElement(WebDriver driver, String findByText, String findBy){
		WebElement element = null;
		if(findBy.equalsIgnoreCase("ID")){
			element = driver.findElement(By.id(findByText));
		}else if(findBy.equalsIgnoreCase("NAME")){
			element = driver.findElement(By.name(findByText));
		}else if(findBy.equalsIgnoreCase("CSS")){
			element = driver.findElement(By.cssSelector(findByText));
		}else if(findBy.equalsIgnoreCase("LINK")){
			element = driver.findElement(By.linkText(findByText));
		}else if(findBy.equalsIgnoreCase("XPATH")){
			element = driver.findElement(By.xpath(findByText));
		}else if(findBy.equalsIgnoreCase("TAGNAME")){
			element = driver.findElement(By.tagName(findByText));
		}		
		return element;
	}
	
	/**
	 * 
	 * @param driver
	 * @param findByText
	 * @param findBy
	 * @return
	 */
	public WebElement getWebElement(WebDriver driver, String findByText, ElementBy findBy){
		WebElement element = null;
		if(findBy == ElementBy.ID){
			element = driver.findElement(By.id(findByText));
		}else if(findBy == ElementBy.NAME){
			element = driver.findElement(By.name(findByText));
		}else if(findBy == ElementBy.CSS_LOCATOR){
			element = driver.findElement(By.cssSelector(findByText));
		}else if(findBy == ElementBy.LINK){
			element = driver.findElement(By.linkText(findByText));
		}else if(findBy == ElementBy.XPATH){
			element = driver.findElement(By.xpath(findByText));
		}		
		return element;
	}
	

	String findElement = "";

	private boolean present = false;

	/**
	 * 
	 * @param driver
	 * @param findText
	 * @param elemtType
	 * @param waitTime
	 */
	public Boolean waitUntilTimeForElement(WebDriver driver,
			ElementBy elementBy, String locatorText, Integer waitTime) {
		findElement = locatorText;
		elementByLocator = elementBy;
		if(findElement.equalsIgnoreCase("dummydummy")){
			logger.info("Waiting upto time {" + waitTime + "} seconds");
		}else{
			logger.info("Wait until {" + findElement + "} is located... waiting time is upto {" + waitTime + "}seconds");
		}		
		(new WebDriverWait(driver, waitTime))
				.until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver driver) {
						present = false;
						try {
							if (elementByLocator == ElementBy.ID) {
								if (driver.findElement(By.id(findElement)) != null) {
									present = true;
								}
							} else if (elementByLocator == ElementBy.NAME) {
								if (driver.findElement(By.name(findElement)) != null) {
									present = true;
								}
							} else if (elementByLocator == ElementBy.LINK) {
								if (driver.findElement(By.linkText(findElement)) != null) {
									present = true;
								}

							} else if (elementByLocator == ElementBy.CSS_LOCATOR) {
								if (driver.findElement(By
										.cssSelector(findElement)) != null) {
									present = true;
								}
							} else if (elementByLocator == ElementBy.XPATH) {
								if (driver.findElement(By.xpath(findElement)) != null) {
									present = true;
								}
							} else if (elementByLocator == ElementBy.DUMMY_DUMMY) {
								if (driver.findElement(By
										.cssSelector(findElement)) != null) {
									present = true;
									logger.info("...{"
													+ findElement
													+ "} found. But this should not happen, please check the dummy text present.");
								}
							} else if (elementByLocator == ElementBy.PAGE_LOAD) {
								if(driver.getTitle()!=null){
									present = driver.getTitle().toLowerCase()
											.startsWith(findElement);
								}
							}
							
							if(present == true && !findElement.equalsIgnoreCase("dummydummy")){
								logger.info("... {" + findElement + "} found.");
							}

						} catch (Exception e) {
							// Exception thrown while the element is not located
							// But we are waiting till the time provided to keep
							// locating the element
						}
						return present;
					}
				});

		return present;

	}
	
	public Boolean waitAndActionElement(WebDriver driver,
			ElementBy elementBy, String locatorText, Integer waitTime, String action) {
		findElement = locatorText;
		elementByLocator = elementBy;
		elementWaitAction = action;
			logger.info("waitAndActionElement on {" + findElement + "} ... waiting time is upto {" + waitTime + "}seconds");
			driver.manage().timeouts().implicitlyWait(WAIT_LOW, TimeUnit.SECONDS);
			try{
		(new WebDriverWait(driver, waitTime))
				.until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver driver) {
						present = false;
						try {
							WebElement element = element = getWebElement(driver, findElement, elementByLocator);
							
							if(elementWaitAction.equalsIgnoreCase("CLEAR")){
								element.clear();
							}else if(elementWaitAction.equalsIgnoreCase("CLICK")){
								element.click();
							}else if(elementWaitAction.equalsIgnoreCase("BUTTON")){
								element.click();
							}else if(elementWaitAction.equalsIgnoreCase("CHECKBOX")){
								element.click();
								logger.info("CHECKBOX element.isSelected() :" + element.isSelected());
							}
							present = true;
						} catch (Exception e) {
							// Exception thrown while the element is not located
							// But we are waiting till the time provided to keep
							// locating the element
							present = false;
						}
						return present;
					}
				});
			}catch(Exception e){
				logger.info("waitAndActionElement unsuccessful for findElement:" + findElement + "|" + "elementByLocator:" +elementByLocator + "|" + "action:" + action);
				present = false;
			}
			driver.manage().timeouts().implicitlyWait(GLOBAL_WAIT, TimeUnit.SECONDS);

		return present;

	}


	/**
	 * 
	 * @param driver
	 * @param elementBy
	 * @param locatorText
	 * @param waitTime
	 * @param timeOutException
	 * @return
	 */
	public Boolean waitUntilTimeForElement(WebDriver driver,
			ElementBy elementBy, String locatorText, Integer waitTime,
			Boolean timeOutException) {
		driver.manage().timeouts().implicitlyWait(waitTime, TimeUnit.SECONDS);
		Boolean returnBoolean = false;
		if (timeOutException) {
			try{
			returnBoolean = waitUntilTimeForElement(driver, elementBy,
					locatorText, waitTime);
			}catch(TimeoutException tOE){
				Assert.assertTrue(returnBoolean, "Unable to locate the element by '" + elementBy.toString() + "' with locator text '" + locatorText + "'");
			}
		} else {
			try {
				returnBoolean = waitUntilTimeForElement(driver, elementBy,
						locatorText, waitTime);
			} catch (Exception e) {
				// As instructed I am ignoring the timeout exception
			}
		}
		driver.manage().timeouts().implicitlyWait(Controller.GLOBAL_WAIT, TimeUnit.SECONDS);
		return returnBoolean;
	}
	
	/**
	 * 
	 * @param driver
	 */
	public void waitUntilWindowClosed(WebDriver driver){
		(new WebDriverWait(driver, Controller.GLOBAL_WAIT))
		.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				logger.info("Window close: looking for h2 if it s null..." + driver.findElement(By.id("h2")));
				if (driver.findElement(By.id("h2")) == null) {
			          return true;
			        }
				return false;
			}
		});
	}
	
	/**
	 * 
	 * @param driver
	 * @param elementBy
	 * @param locatorText
	 * @return
	 */
	public Boolean waitUntilTimeForElement(WebDriver driver,
			ElementBy elementBy, String locatorText) {
		Boolean returnBoolean = false;
		try{
			returnBoolean = waitUntilTimeForElement(driver, elementBy, locatorText, GLOBAL_WAIT);	
		}catch(TimeoutException tOE){
			Assert.assertTrue(returnBoolean, "Unable to locate the element by '" + elementBy.toString() + "' with locator text '" + locatorText + "'");
		}
		return returnBoolean;
	}

	/**
	 * 
	 * @param driver
	 * @param dummyText
	 * @param waitTimeSec
	 */
	public void waitUntilXSec(WebDriver driver, String dummyText,
			Integer waitTimeSec) {
		waitUntilTimeForElement(driver, ElementBy.DUMMY_DUMMY, dummyText,
				waitTimeSec, false);

	}
	
	public void driverWindowHandles(WebDriver driver){
		logger.info("Printing Window Handles.... no of window handles available currently " + driver
				.getWindowHandles().size());
		for (String handle : driver
				.getWindowHandles()) {
			System.out.print("{" + handle + "}");
			}
			// 
	}
	
	/**
	 * 
	 * @param driver
	 * @param url
	 * @return
	 */
	public Boolean checkIfTheURLIsOpen(WebDriver driver, String url){		
		if(url !=null && !url.equalsIgnoreCase("") && (url.equals(driver.getCurrentUrl())||driver.getCurrentUrl().contains(url))){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param driver
	 * @param objectID
	 */
	public Boolean lookForObject(WebDriver driver, String objectID){
		driver.findElement(By.id("phSearchInput")).click();
		driver.findElement(By.id("phSearchInput")).clear();
		driver.findElement(By.id("phSearchInput")).sendKeys(objectID);
		driver.findElement(By.id("phSearchButton")).click();		
		int iterations = 1;
		while(!waitUntilTimeForElement(driver, Controller.ElementBy.LINK, objectID,Controller.WAIT_2NDLOW,false) && iterations <=10){
			driver.findElement(By.id("phSearchInput")).click();
			driver.findElement(By.id("phSearchInput")).clear();
			driver.findElement(By.id("phSearchInput")).sendKeys(objectID);
			driver.findElement(By.id("phSearchButton")).click();
			iterations++;
		}
		if(!waitUntilTimeForElement(driver, Controller.ElementBy.LINK, objectID,Controller.WAIT_2NDLOW,false)){
			logger.info("lookForObject couldn't find the object id:" + objectID  + "... even after " + (iterations-1) + " re-tries");			
			return false;
		}
		logger.info("Lookup found the object id:" + objectID);
		driver.findElement(By.linkText(objectID)).click();
		
		return true;
	}
	

	/**
	 * 
	 * @param driver
	 * @param objURL
	 */
	public void openObjectURL(WebDriver driver, String objURL){
		driver.navigate().to(objURL);
	}

	/**
	 * 
	 * @param driver
	 */
	public Boolean waitUntilTime(WebDriver driver, Integer timeInSec) {
		findElement = "dummydummy";
		driver.manage().timeouts().implicitlyWait(timeInSec, TimeUnit.SECONDS);
		waitUntilXSec(driver, findElement, timeInSec);
		driver.manage().timeouts().implicitlyWait(Controller.GLOBAL_WAIT, TimeUnit.SECONDS);		
		return true;
	}

	private String newWindowNameAsKey = "";

	/**
	 * 
	 * @param driver
	 * @param newWindowName
	 */
	public Boolean addNewWindowHandler(WebDriver driver,
			String newWindowName) {
		newWindowNameAsKey = newWindowName;
		try {
			(new WebDriverWait(driver, GLOBAL_WAIT))
					.until(new ExpectedCondition<Boolean>() {
						public Boolean apply(WebDriver driver) {
							present = false;
							try {
								logger.info("@addNewWindowHandler : Open Handles Count : " + driver.getWindowHandles().size());
								if (driver.getWindowHandles().size() > windowHandlerMap
										.size()) {
									for (String handle : driver
											.getWindowHandles()) {
										if (!windowHandlerMap
												.containsValue(handle)) {
											updateWindowHandleMap(
													newWindowNameAsKey, handle);									
										}
										
									}
									present = true;
								}

							} catch (Exception e) {

							}
							return present;
						}
					});
		} catch (Exception e) {
			logger.info("addNewWindowHandler - is not successful");
		}
		displayWindowHandleMap();
		return present;

	}

	/**
	 * 
	 * @param driver
	 * @param selectOptions
	 * @param elementId
	 * @return
	 */
	public Boolean selectOption(WebDriver driver,
			WebElement selectElement, String elementText) {
		List<WebElement> selectOptions = new Select(selectElement).getOptions();
		Boolean returnVal = false;
		try{
			for (Iterator iterator = selectOptions.iterator(); iterator.hasNext();) {
				WebElement webElement = (WebElement) iterator.next();
				if (webElement.getText().contains(elementText)) {
					webElement.click();
					returnVal = true;
					break; 
				}
			}
		}catch(Exception e){
			logger.error("Error while selecting element...");
			e.printStackTrace();
		}
		if(returnVal){
			logger.info("Selected {" + elementText + "}");
		}else{
			Assert.assertTrue(returnVal, "Unable to select element - '" + (selectElement.getAttribute("name")==null ? selectElement.getAttribute("id") : selectElement.getAttribute("name")) + "' with value '" + elementText + "'");			
		}
		return returnVal;
	}
	
	/**
	 * 
	 * @param driver
	 * @param selectElement
	 * @param index
	 * @return
	 */
	public Boolean selectOption(WebDriver driver,
			WebElement selectElement, Integer index) {
		List<WebElement> selectOptions = new Select(selectElement).getOptions();
		Boolean returnVal = false;
		try{Integer localIndex=1;
			for (Iterator<WebElement> iterator = selectOptions.iterator(); iterator.hasNext();) {
				WebElement webElement = (WebElement) iterator.next();
				if (localIndex == index) {
					webElement.click();
					returnVal = true;
					break;
				}
				localIndex++;
			}
		}catch(Exception e){
			logger.error("Error while selecting element...");
			e.printStackTrace();
		}
		if(returnVal){
			logger.info("Selected element at index {" + index + "}");
		}else{
			Assert.assertTrue(returnVal, "Unable to select element '" + (selectElement.getAttribute("name")==null ? selectElement.getAttribute("id") : selectElement.getAttribute("name")) + "' at index '" + index + "'");			
		}
		return returnVal;
	}
	
	/**
	 * 
	 * @param driver
	 * @param selectElement
	 * @param elementText
	 */
	public void selectOptionByVisibleText(WebDriver driver,
			WebElement selectElement, String elementText) {		
			Select selctWebElement = new Select(selectElement);
			selctWebElement.selectByVisibleText(elementText);			
			logger.info("Was able to select through selectOptionByVisibleText:" + selctWebElement.getAllSelectedOptions().toString());
	}
	
	/**
	 * 
	 * @param driver
	 * @param selectElement
	 * @param pickListType
	 */
	public boolean validateCommonPickList(WebDriver driver,
			WebElement selectElement,String pickListKey){
		String testDataFile = "common_list_ui_testdata.properties";	
		String value = "";
				LinkedProperties testDataProp = new LinkedProperties();				
				try{
					testDataProp.load(Controller.class.getResourceAsStream("common" + "/" + testDataFile));
				}catch(IOException ioe){
					logger.info("IOE:" + ioe.getMessage());
					return false;
				}				
				/*if(pickListType.equalsIgnoreCase("COUNTRIES")){	
					value = testDataProp.getProperty("countryList");
				}else if(pickListType.equalsIgnoreCase("AGENCY_EDITIONS")){
					value = testDataProp.getProperty("agencyEditionsList");
				}*/
				
				value = testDataProp.getProperty(pickListKey);
				
				logger.info(pickListKey + " ==> " + value);
				StringTokenizer valueToken = new StringTokenizer(value,"$");
				Select selctWebElement = new Select(selectElement);
				if(valueToken.countTokens()==selctWebElement.getOptions().size()){
					List<WebElement> options = selctWebElement.getOptions();
					for(WebElement wele:options){
						String tokenString = valueToken.nextElement().toString();
						if(wele.getText().trim().equals(tokenString)){
							logger.info(wele.getText().trim() + "==" + tokenString);	
						}else{
							logger.info(pickListKey + " doen't match...");
							logger.info("FAILED REASON:wele.getText().trim():" + wele.getText().trim() + "#expected to be equal to#" + tokenString);							
							return false;
						}
					}
				 }else{
					 logger.info(pickListKey + " doesn't match");
					 logger.info("FAILED REASON:valueToken.countTokens():" + valueToken.countTokens() + "!=" + "selctWebElement.getOptions().size():" + selctWebElement.getOptions().size());
					 return false;
				 }			
			return true;
	}	
	
	/**
	 * 
	 * @param windowHandleToClose
	 * @param windowHandleToSwith
	 */
	public void closeTheWindowHandle(WebDriver driver, String  windowHandleToClose, String windowHandleToSwith){
		if(windowHandleToClose!=null && windowHandleToSwith!=null && readTheWindowHandle(windowHandleToClose) != null && readTheWindowHandle(windowHandleToSwith) != null){
			driver.switchTo().window(readTheWindowHandle(windowHandleToClose)).close();
			removeTheWindowHandle(windowHandleToClose);
			driver.switchTo().window(windowHandleToSwith);
			waitUntilTime(driver, WAITQ1);
		}else{
			logger.info("closeTheWindowHandle: Condition did not met and it FAILED!");
		}
	}
	
	/**
	 * Handle the warning alert or unexpected alert 
	 * @param driver
	 */
	public Boolean handleTheWarningAlert(WebDriver driver){		
		try {
			(new WebDriverWait(driver, GLOBAL_WAIT))
					.until(new ExpectedCondition<Boolean>() {
						public Boolean apply(WebDriver driver) {
							present = false;
							try {
								if(driver.getWindowHandles().size()>1){
									logger.info("Alert: Message :" + driver.switchTo().alert().getText());
									driver.switchTo().alert().accept();
									logger.info("*** Alert: Accepted ***");
								}
								present = true;
							} catch (NoAlertPresentException e) {
								
							}
							return present;
						}
					});
		} catch (Exception e) {
			logger.info("waitAndAcceptAlert - is not successful. Please Ignore any way...");
		}
		return present;			
	}

}
