package com.salesforce.itqe.controller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


import java.util.List;

/**
 * O2Q controller
 * @author bob
 * 
 */
public class O2QController extends Controller{
	
	
	/**
	 * 
	 * @param driver
	 * @param type
	 */
	public void chooseReport(WebDriver driver, String type){
		pickReportType(driver, type);
		viewReport(driver);
	}
	
	/**
	 * 
	 * @param driver
	 * @param type
	 * @param year
	 */
	public void chooseReport(WebDriver driver, String type, String year){
		pickReportType(driver, type);
		pickFiscalYear(driver, year);
		viewReport(driver);
	}
	
	 /**
     * 
     * @param driver
     * @param type
     * @param year
     * @param quarter
     */
	public void chooseReport(WebDriver driver, String type, String year, String quarter){
		pickReportType(driver, type);
		pickFiscalYear(driver, year);
		pickFiscalQuarter(driver, quarter);
		viewReport(driver);
	}
    
	/**
	 * 
	 * @param driver
	 */
	private void viewReport(WebDriver driver) {
		waitUntilTime(driver, Controller.WAIT_LOW);
		driver.findElement(By.id("j_id0:ReportForm:viewReport")).click();
		waitUntilTime(driver, Controller.WAIT_LOW);
	}

	/**
	 * 
	 * @param driver
	 * @param type
	 */
	private void pickReportType(WebDriver driver, String type) {
		WebElement select = driver.findElement(By.name("j_id0:ReportForm:j_id3"));
		List<WebElement> allOptions = select.findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
			String value = option.getAttribute("value");
			
			if(value.equalsIgnoreCase(type))
				option.click();
			
		
		}
	}
	
	/**
	 * 
	 * @param driver
	 * @param year
	 */
	private void pickFiscalYear(WebDriver driver, String year) {
		WebElement select = driver.findElement(By.name("j_id0:ReportForm:j_id6"));
		List<WebElement> allOptions = select.findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
			String value = option.getAttribute("value");
			
			if(value.equalsIgnoreCase(year))
				option.click();
			
		}
	}
	
	/**
	 * 
	 * @param driver
	 * @param quarter
	 */
	private void pickFiscalQuarter(WebDriver driver, String quarter) {
		WebElement select = driver.findElement(By.name("j_id0:ReportForm:j_id9"));
		List<WebElement> allOptions = select.findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
			String value = option.getAttribute("value");
			
			if(value.equalsIgnoreCase(quarter))
				option.click();
			
		}
	}
	
	/**
	 * 
	 * @param driver
	 * @param tabSettings
	 */
	public void chooseECOMMTabSettings(WebDriver driver, String tabSettings) {
		WebElement select = driver.findElement(By.name("page:console:j_id77:j_id78:j_id79:j_id101:j_id102:objects_tabs_detail:j_id120:0:j_id122:j_id123:j_id124:j_id130:j_id134"));
	
		List<WebElement> allOptions = select.findElements(By.tagName("option"));
		for (WebElement option : allOptions) {
			String value = option.getText();
			if(value.equalsIgnoreCase(tabSettings))
				option.click();
				
		}
	}
	
	/**
	 * 
	 * @param driver
	 * @param tabSettings
	 */
	public void chooseCorporateForecastObjectPermissions(WebDriver driver) {
		WebElement read = driver.findElement(By.id("page:console:j_id77:j_id78:j_id79:j_id101:j_id102:objects_tabs_detail:j_id120:0:j_id122:j_id123:j_id124:j_id174:j_id175:0:olp_check"));
	    read.click();
	    
	    WebElement create = driver.findElement(By.id("page:console:j_id77:j_id78:j_id79:j_id101:j_id102:objects_tabs_detail:j_id120:0:j_id122:j_id123:j_id124:j_id174:j_id175:1:olp_check"));
	    create.click();
	    
	    WebElement edit = driver.findElement(By.id("page:console:j_id77:j_id78:j_id79:j_id101:j_id102:objects_tabs_detail:j_id120:0:j_id122:j_id123:j_id124:j_id174:j_id175:2:olp_check"));
	    edit.click();
	    
	    WebElement delete = driver.findElement(By.id("page:console:j_id77:j_id78:j_id79:j_id101:j_id102:objects_tabs_detail:j_id120:0:j_id122:j_id123:j_id124:j_id174:j_id175:3:olp_check"));
	    delete.click();
	    
	    WebElement view = driver.findElement(By.id("page:console:j_id77:j_id78:j_id79:j_id101:j_id102:objects_tabs_detail:j_id120:0:j_id122:j_id123:j_id124:j_id174:j_id175:4:olp_check"));
	    view.click();
	    
	    WebElement modify = driver.findElement(By.id("page:console:j_id77:j_id78:j_id79:j_id101:j_id102:objects_tabs_detail:j_id120:0:j_id122:j_id123:j_id124:j_id174:j_id175:5:olp_check"));
	    modify.click();
		
	}

}
