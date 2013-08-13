package com.salesforce.itqe.domain.ui.pageobjects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.itqe.controller.Controller;

public class AccountPage {

	private static Logger logger = LoggerFactory.getLogger(AccountPage.class); 
	
	private final WebDriver driver;

	private String accountID;
	
	private int opptySeq = 0;
	
	private String accountURL;
	
	private String accountObjID;
	
	private Controller controller;

	public String getAccountURL() {
		return accountURL;
	}

	public void setAccountURL(String accountURL) {
		this.accountURL = accountURL;
	}

	public int getOpptySeq() {
		return opptySeq;
	}

	public void setOpptySeq(int opptySeq) {
		this.opptySeq = opptySeq;
	}

	/**
	 * 
	 * @return
	 */
	public String getAccountID() {
		return accountID;
	}

	/**
	 * 
	 * @return
	 */
	public WebDriver getDriver() {
		return driver;
	}

	/**
	 * 
	 * @param driver
	 */
	public AccountPage(WebDriver driver, Controller controller) {
		generateNewAccountID();
		this.controller = controller;
		this.controller.setAccountId(getAccountID());
		this.driver = driver;
	}

	/**
	 * 
	 */
	public Boolean createAccountUI() {
		//populate account page
		if(controller.populate("accountpage", driver)){
			this.accountURL = driver.getCurrentUrl();
			setAccountObjID();
		}else{
			return false;
		}	
		return true;
	}

	/**
	 * 
	 */
	private void generateNewAccountID() {
		StringBuffer accID = new StringBuffer("AutoAcc_");		
		DateFormat formatter;
		Date date = new Date();
		formatter = new SimpleDateFormat("MM_dd_yyyy_HH_mm_ss");
		accID.append(formatter.format(date));
		this.accountID = accID.toString() + Thread.currentThread().getId();		
	}
	
	private void setAccountObjID(){
		String accStr = getAccountURL();
		this.accountObjID = accStr.substring(accStr.indexOf("/001")+1);
	}
	
	public String getAccountObjID() {
		return accountObjID;
	}

	/**
	 * 
	 * @return
	 */
	public int assignOpptySeq(){		
		return ++opptySeq;		
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

}
