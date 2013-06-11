package com.salesforce.itqe.domain.ui.pageobjects;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.salesforce.itqe.controller.Controller;

public class OpportunityPage {
	
	private static Logger logger = LoggerFactory.getLogger(OpportunityPage.class);

	//
	public static enum OPPTY_TYPE {
		NB, ADD, RN, UG;
	};

	//
	private final WebDriver driver;

	//
	private String opportunityID;

	//
	private String opptyAccount;

	//
	private int accOpptySeq;

	//
	private OPPTY_TYPE opptyTypeIS;
	
	//
	private int quoteSeq = 0;	
	
	private String opptyURL;
	
	private String accountURL;
	
	private String fileLocation;
	
	private Controller controller;

	public String getAccountURL() {
		return accountURL;
	}

	public void setAccountURL(String accountURL) {
		this.accountURL = accountURL;
	}

	public String getOpptyURL() {
		return opptyURL;
	}

	public void setOpptyURL(String opptyURL) {
		this.opptyURL = opptyURL;
	}

	/**
	 * 
	 * @return
	 */
	public String getOpportunityID() {
		return opportunityID;
	}

	/**
	 * 
	 */
	private void generateNewOpportunityID() {
		StringBuffer opptyID = new StringBuffer(getOpptyAccount().replaceAll(
				"Acc", "Oppty")).append("_").append(this.opptyTypeIS)
				.append("_").append(getAccOpptySeq());
		this.opportunityID = opptyID.toString();
	}

	/**
	 * 
	 * @return
	 */
	public String getOpptyAccount() {
		return opptyAccount;
	}

	/**
	 * 
	 * @return
	 */
	public int getAccOpptySeq() {
		return accOpptySeq;
	}

	/**
	 * 
	 * @return
	 */
	public OPPTY_TYPE getOpptyTypeIS() {
		return opptyTypeIS;
	}
	
	public int getQuoteSeq() {
		return quoteSeq;
	}
	
	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	/**
	 * 
	 * @param driver
	 * @param accountID
	 * @param accOpptySeq
	 * @param accountURL
	 * @param opptyType
	 */
	public OpportunityPage(WebDriver driver, String accountID,
			int accOpptySeq, String accountURL, OPPTY_TYPE opptyType, Controller controller) {

		this.opptyAccount = accountID;
		this.accOpptySeq = accOpptySeq;
		this.opptyTypeIS = opptyType;
		this.accountURL = accountURL;
		this.fileLocation = "";
		this.controller = controller;
		generateNewOpportunityID();
		this.controller.setOpportunityID(getOpportunityID());
		this.controller.setOpttyType(opptyType);
		this.driver = driver;
	}
	
	
	public OpportunityPage(WebDriver driver, String accountID,
			int accOpptySeq, String accountURL, OPPTY_TYPE opptyType, String fileLoc, Controller controller) {

		this.opptyAccount = accountID;
		this.accOpptySeq = accOpptySeq;
		this.opptyTypeIS = opptyType;
		this.accountURL = accountURL;
		this.fileLocation = fileLoc;
		this.controller = controller;
		generateNewOpportunityID();
		this.controller.setOpportunityID(getOpportunityID());
		this.controller.setOpttyType(opptyType);
		this.driver = driver;
	}

	/**
	 * 
	 * @return
	 */
	public Boolean createOpportunityUI() {
		
		//Locate Account
		if(!controller.checkIfTheURLIsOpen(driver, accountURL)){
			logger.info("Opening the AccountURL:" + accountURL);			
			controller.openObjectURL(driver, accountURL);
		}
		
		String opptyPageFile = null;
		//set oppty page file
		if(controller.getOpttyType() == OPPTY_TYPE.NB){
			opptyPageFile = "opportunitypage_nb";
		}else if (controller.getOpttyType() == OPPTY_TYPE.ADD){
			opptyPageFile = "opportunitypage_add";
		}else if (controller.getOpttyType() == OPPTY_TYPE.UG){
			opptyPageFile = "opportunitypage_ug";
		}else if (controller.getOpttyType() == OPPTY_TYPE.RN){
			opptyPageFile = "opportunitypage_rn";
		}
		//populate the oppty page		
	//		Controller.lookForObject(driver, getOpptyAccount());
			if(controller.populate(opptyPageFile, driver, fileLocation)){				
				this.opptyURL = driver.getCurrentUrl();	
			}else{
				return false;
			}			
		
			
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public int assignQuoteSeq(){		
		return ++quoteSeq;		
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
