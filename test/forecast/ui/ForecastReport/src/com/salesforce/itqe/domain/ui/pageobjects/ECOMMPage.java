package com.salesforce.itqe.domain.ui.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.salesforce.itqe.controller.Controller;
import com.salesforce.itqe.controller.utils.O2QUtil;
import com.salesforce.itqe.controller.utils.SfdcConstants;

/**
 * ECOMM Report Page Object
 * @author bob
 *
 */
public class ECOMMPage {
    private final WebDriver driver;

    private static Logger logger = LoggerFactory.getLogger(ECOMMPage.class); 
  
    private Controller controller;
    

	public ECOMMPage(WebDriver driver, Controller controller) {
        this.driver = driver;
        this.controller = controller;
    }

    /**
     * click ECOMM Reports tab
     */
    public Boolean clickECOMMTab() {
        
          /*Identify the elements to interact with */     
    	  logger.info("click ECOMM Reports tab..............");    	  
      
    	if(!controller.populate("ecommpage", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
     * click Company Wide Forecast
     */
    public Boolean clickCompanyWideForecast() {
        
          /*Identify the elements to interact with */     
    	  logger.info("click Company Wide Forecast..............");    	  
      
    	if(!controller.populate("ecommpage_forecast", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
     * drill down to Enterprise
     */
    public Boolean clickEnterpriseBU() {
        
          /*Identify the elements to interact with */     
    	  logger.info("click Enterprise drill down..............");    	  
      
    	if(!controller.populate("ecommpage_enterprise_bu", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
     * drill down to Commercial
     */
    public Boolean clickCommercialBU() {
        
          /*Identify the elements to interact with */     
    	  logger.info("click Commercial drill down..............");    	  
      
    	if(!controller.populate("ecommpage_commercial_bu", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
     * drill down to Public Sector
     */
    public Boolean clickPublicBU() {
        
          /*Identify the elements to interact with */     
    	  logger.info("click Public Sector drill down..............");    	  
      
    	if(!controller.populate("ecommpage_public_bu", driver))
  			return false;
  		
  		return true;    
    }
    
    /**
     * drill down to Data.com
     */
    public Boolean clickDataBU() {
        
          /*Identify the elements to interact with */     
    	  logger.info("click Data.com drill down..............");    	  
      
    	if(!controller.populate("ecommpage_data_bu", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
     * drill down to Radian6
     */
    public Boolean clickRadian6BU() {
        
          /*Identify the elements to interact with */     
    	  logger.info("click Radian6 drill down..............");    	  
      
    	if(!controller.populate("ecommpage_radian6_bu", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
     * drill down to Heroku
     */
    public Boolean clickHerokuBU() {
        
          /*Identify the elements to interact with */     
    	  logger.info("click Heroku drill down..............");    	  
      
    	if(!controller.populate("ecommpage_heroku_bu", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
     * click ECOMM Reports tab and choose Report Type, Fiscal Year and Quarter
     */
    public Boolean chooseReport() {
        
          /*Identify the elements to interact with */     
    	  logger.info("click ECOMM Reports tab and choose Report Type..............");    	  
      
    	if(!controller.populate("ecommpage_report", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
	 * check BU: Enterprise Present
	 */
	public Boolean isEnterpriseLinkPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.ENTERPRISE)) != null)
			return true;
		else
			return false;	
	}
   
	 /**
	 * check BU: Commercial Present
	 */
	public Boolean isCommercialLinkPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.COMMERCIAL)) != null)
			return true;
		else
			return false;	
	}
	
	/**
	 * check BU: Public Sector Present
	 */
	public Boolean isPublicSectorLinkPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.PUBLIC_SECTOR)) != null)
			return true;
		else
			return false;	
	}
   
	 /**
	 * check BU: Data.com Present
	 */
	public Boolean isDataLinkPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.DATA_COM)) != null)
			return true;
		else
			return false;	
	}
	
	 /**
	 * check BU: Radian6 Present
	 */
	public Boolean isRadian6LinkPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.RADIAN6)) != null)
			return true;
		else
			return false;	
	}
	
	 /**
	 * check BU: Heroku Present
	 */
	public Boolean isHerokuLinkPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.HEROKU)) != null)
			return true;
		else
			return false;	
	}
	
	 /**
	 * check Outlook Header Present
	 */
	public String getOutlookHeader() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/span"));
		String outLookText = element.getText();
	
		return outLookText;	
	}
	
	/**
	 * check Deal Report Header Present
	 */
	public String getDealReportHeader() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[4]/form/div/div/div/div/div/span"));
		String dealText = element.getText();
		
		return dealText;	
	}
	
	 /**
	 * check Plan Header Present
	 */
	public String getPlanHeader() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/span"));
		String planText = element.getText();
		
		return planText;	
	}
	
	//Geos for Enterprise
	 /**
	 * check Geo AMER-FS present
	 */
	public Boolean isAMERPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.AMER_FS)) != null)
			return true;
		else
			return false;	
	}
	
	/**
	 * check Geo APAC-FS Present
	 */
	public Boolean isAPACPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.APAC_FS)) != null)
			return true;
		else
			return false;	
	}
   
	 /**
	 * check Geo EMEA-FS Present
	 */
	public Boolean isEMEAPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.EMEA_FS)) != null)
			return true;
		else
			return false;	
	}
	
	 /**
	 * check Geo JP-FS Present
	 */
	public Boolean isJPPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.JP_FS)) != null)
			return true;
		else
			return false;	
	}
	
	//Geos for Commercial
	 /**
	 * check Geo AMER/EMEA-CS present
	 */
	public Boolean isAMER_EMEAPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.AMER_EMEA_CS)) != null)
			return true;
		else
			return false;	
	}
	
	/**
	 * check Geo APAC-CS Present
	 */
	public Boolean isAPAC_CSPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.APAC_CS)) != null)
			return true;
		else
			return false;	
	}
  
	 /**
	 * check Geo JP-CS Present
	 */
	public Boolean isJP_CSPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.JP_CS)) != null)
			return true;
		else
			return false;	
	}

	 /**
	 * check Geo AMER-PUBSEC Present
	 */
	public Boolean isAMER_PUBSECPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.AMER_PUBSEC)) != null)
			return true;
		else
			return false;	
	}
	
	 /**
	 * check Geo Data-Alliances Present
	 */
	public Boolean isData_AlliancesPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.DATA_ALLIANCES)) != null)
			return true;
		else
			return false;	
	}
	
	 /**
	 * check Geo Data-Marketing Present
	 */
	public Boolean isData_MarketingPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.DATA_MARKETING)) != null)
			return true;
		else
			return false;	
	}
	
	 /**
	 * check Geo Data-SVP Present
	 */
	public Boolean isData_SVPPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.DATA_SVP)) != null)
			return true;
		else
			return false;	
	}
	
	 /**
	 * check Geo Data-Services Present
	 */
	public Boolean isData_ServicesPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.DATA_SERVICES)) != null)
			return true;
		else
			return false;	
	}
	
	 /**
	 * check Geo R6-COO Present
	 */
	public Boolean isR6_COOPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.R6_COO)) != null)
			return true;
		else
			return false;	
	}
	
	 /**
	 * check First Month Header
	 */
	public String getFirstMonthHeader() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.id("j_id0:forecastForm:pgBlock:pgBlockSection1:falseHeader:j_id33header:sortDiv"));
		String firstMonth = element.getText();
		
		return firstMonth;	
	}
	
	 /**
	 * check Second Month Header
	 */
	public String getSecondMonthHeader() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.id("j_id0:forecastForm:pgBlock:pgBlockSection1:falseHeader:j_id71header:sortDiv"));
		String secondMonth = element.getText();
		
		return secondMonth;	
	}
	
	 /**
	 * check Third Month Header
	 */
	public String getThirdMonthHeader() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.id("j_id0:forecastForm:pgBlock:pgBlockSection1:falseHeader:j_id109header:sortDiv"));
		String thirdMonth = element.getText();
	
		return thirdMonth;	
	}
	
	 /**
	 * check First Month Closed For Each BU
	 */
	public double getFirstMonthClosed() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr/td/span"));
		String firstMonth_enterprise = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[2]/td/span"));
		String firstMonth_commercial = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[3]/td/span"));
		String firstMonth_public = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[5]/td/span"));
		String firstMonth_data = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[6]/td/span"));
		String firstMonth_radian6 = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[7]/td/span"));
		String firstMonth_heroku = element.getText();
		
		return  O2QUtil.getDoubleValue( firstMonth_enterprise) +  O2QUtil.getDoubleValue( firstMonth_commercial) +
		 O2QUtil.getDoubleValue( firstMonth_public) +  O2QUtil.getDoubleValue( firstMonth_data) +
		  O2QUtil.getDoubleValue( firstMonth_radian6);
	}
	
	 /**
	 * check First Month Total Closed 
	 */
	public double getFirstMonthTotalClosed() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[7]/td/span"));
		String firstMonth_total = element.getText();
		
		return  O2QUtil.getDoubleValue( firstMonth_total);
		
	}
	
	 /**
	 * check First Month Outlook For Each BU
	 */
	public double getFirstMonthOutlook() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr/td/span"));
		String firstMonth_enterprise = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[2]/td/span"));
		String firstMonth_commercial = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[3]/td/span"));
		String firstMonth_public = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[5]/td/span"));
		String firstMonth_data = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[6]/td/span"));
		String firstMonth_radian6 = element.getText();
		
		//element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[7]/td/span"));
		//String firstMonth_heroku = element.getText();
		
		return  O2QUtil.getDoubleValue( firstMonth_enterprise) +  O2QUtil.getDoubleValue( firstMonth_commercial) +
		 O2QUtil.getDoubleValue( firstMonth_public) +  O2QUtil.getDoubleValue( firstMonth_data) +
		  O2QUtil.getDoubleValue( firstMonth_radian6);
	}
	
	 /**
	 * check First Month Total Outlook 
	 */
	public double getFirstMonthTotalOutlook() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[7]/td/span"));
		String firstMonth_total = element.getText();
		
		return  O2QUtil.getDoubleValue( firstMonth_total);
		
	}
	
	 /**
	 * check First Month Plan For Each BU
	 */
	public double getFirstMonthPlan() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr/td/span"));
		String firstMonth_enterprise = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[2]/td/span"));
		String firstMonth_commercial = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[3]/td/span"));
		String firstMonth_public = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[5]/td/span"));
		String firstMonth_data = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[6]/td/span"));
		String firstMonth_radian6 = element.getText();
		
		//element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[7]/td/span"));
		//String firstMonth_heroku = element.getText();
		
		return  O2QUtil.getDoubleValue( firstMonth_enterprise) +  O2QUtil.getDoubleValue( firstMonth_commercial) +
		 O2QUtil.getDoubleValue( firstMonth_public) +  O2QUtil.getDoubleValue( firstMonth_data) +
		  O2QUtil.getDoubleValue( firstMonth_radian6) ;
	}
	
	 /**
	 * check First Month Total Plan 
	 */
	public double getFirstMonthTotalPlan() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr[7]/td/span"));
		String firstMonth_total = element.getText();
		
		return  O2QUtil.getDoubleValue( firstMonth_total);
		
	}
	
	 /**
	 * get Closed For Enterprise
	 */
	public double getEnterpriseClosed() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr/td/span"));
		String firstMonth = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr/td/span"));
		String secondMonth = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[4]/table/tbody/tr/td/span"));
		String thirdMonth = element.getText();
		
	
		
		return  O2QUtil.getDoubleValue( firstMonth) +  O2QUtil.getDoubleValue( secondMonth) +
		 O2QUtil.getDoubleValue( thirdMonth) ;
	}
	
	 /**
	 * check Quarter Total Closed for Enterprise
	 */
	public double getEnterpriseQuarterTotalClosed() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[5]/table/tbody/tr/td/span"));
		String quarter_total = element.getText();
		
		return  O2QUtil.getDoubleValue( quarter_total);
		
	}
	
	 /**
	 * get Outlook For Enterprise
	 */
	public double getEnterpriseOutlook() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr/td/span"));
		String firstMonth = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr/td/span"));
		String secondMonth = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[4]/table/tbody/tr/td/span"));
		String thirdMonth = element.getText();
		
	
		
		return  O2QUtil.getDoubleValue( firstMonth) +  O2QUtil.getDoubleValue( secondMonth) +
		 O2QUtil.getDoubleValue( thirdMonth) ;
	}
	
	 /**
	 * check Quarter Total Outlook for Enterprise
	 */
	public double getEnterpriseQuarterTotalOutlook() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[5]/table/tbody/tr/td/span"));
		String quarter_total = element.getText();
		
		return  O2QUtil.getDoubleValue( quarter_total);
		
	}
	
	 /**
	 * get Plan For Enterprise
	 */
	public double getEnterprisePlan() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[2]/table/tbody/tr/td/span"));
		String firstMonth = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[3]/table/tbody/tr/td/span"));
		String secondMonth = element.getText();
		
		element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[4]/table/tbody/tr/td/span"));
		String thirdMonth = element.getText();
		
	
		
		return  O2QUtil.getDoubleValue( firstMonth) +  O2QUtil.getDoubleValue( secondMonth) +
		 O2QUtil.getDoubleValue( thirdMonth) ;
	}
	
	 /**
	 * check Quarter Total Plan for Enterprise
	 */
	public double getEnterpriseQuarterTotalPlan() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[3]/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td[5]/table/tbody/tr/td/span"));
		String quarter_total = element.getText();
		
		return  O2QUtil.getDoubleValue( quarter_total);
		
	}
	
	/**
     * click Go Back
     */
    public boolean clickGoBack() {
        
          /*Identify the elements to interact with */     
    	  logger.info("click Go Back..............");    	  
      
    	if(!controller.populate("ecommpage_goback", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
     * click Go Back Top
     */
    public boolean clickGoBackTop() {
        
          /*Identify the elements to interact with */     
    	  logger.info("click Go Back Top..............");    	  
      
    	if(!controller.populate("ecommpage_gobacktop", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
	 * get Geo Report Header
	 */
	public String getGeoReportHeader() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/span"));
	
		return element.getText();	
	}
    
    //Regions drill down 
    /**
	 * click first Geo
	 */
	public void clickFirstGeo() {
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.id("j_id0:forecastForm:pgBlock:pgBlockSection1:falseHeader:0:forecastRows1:0:theCommandLink"));
		element.click();
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
	}
	
	 /**
	 * click second Geo
	 */
	public void clickSecondGeo() {
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.id("j_id0:forecastForm:pgBlock:pgBlockSection1:falseHeader:0:forecastRows1:1:theCommandLink"));
		element.click();
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
	}
	
	 /**
	 * click third Geo
	 */
	public void clickThirdGeo() {
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.id("j_id0:forecastForm:pgBlock:pgBlockSection1:falseHeader:0:forecastRows1:2:theCommandLink"));
		element.click();
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
	}
	
	/**
	 * click fouth Geo
	 */
	public void clickFouthGeo() {
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.id("j_id0:forecastForm:pgBlock:pgBlockSection1:falseHeader:0:forecastRows1:3:theCommandLink"));
		element.click();
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
	}
	
	 /**
	 * get First Region 
	 */
	public String getFirstRegion() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr/td/span"));
		if (element != null)
			return element.getText();
	
		return "";	
	}
	
	 /**
	 * get Second Region 
	 */
	public String getSecondRegion() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[2]/td/span"));
		if (element != null)
			return element.getText();
	
		return "";	
	}
	
	 /**
	 * get Third Region 
	 */
	public String getThirdRegion() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[3]/td/span"));
		if (element != null)
			return element.getText();
	
		return "";	
	}
	
	 /**
	 * get Fouth Region  
	 */
	public String getFouthRegion() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[4]/td/span"));
		if (element != null)
			return element.getText();

		return "";	
	}
	
	 /**
	 * get Fifth Region  
	 */
	public String getFifthRegion() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[5]/td/span"));
		if (element != null)
			return element.getText();

		return "";	
	}
	
	 /**
	 * get Sixth Region  
	 */
	public String getSixthRegion() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/div/div/table/tbody/tr/td/table/tbody/tr/td/table/tbody/tr[6]/td/span"));
		if (element != null)
			return element.getText();

		return "";	
	}
	
	//Security Test
	/**
	 * check ECOMM Object Link not Present
	 */
	public Boolean isEcommObjectPresent() {
		
		boolean _found = false;
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		try {
			if (driver.findElement(By.linkText(SfdcConstants.ECOMM_TAB)) != null)
				_found = true;
			
		} catch (Exception e) {
			;//Do nothing
		}
			return _found;
		
		
	}
	
	 /**
     * Test can't add ECOMM tab
     */
    public void clickAddTab() {
        
    	controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.className("allTabsArrow"));
		if (element != null)
		    element.click();
     
    }
    
    /**
     * Test can't add ECOMM tab
     */
    public Boolean clickSetup() {
        
          /*Identify the elements to interact with */     
     
    	if(!controller.populate("usersetuppage", driver))
  			return false;
  		
  		return true;
              
    }
    
    /**
     * Test can't add ECOMM tab
     */
    public Boolean clickProfile() {
        
          /*Identify the elements to interact with */     
     
    	if(!controller.populate("ecommpage_add", driver))
  			return false;
  		
  		return true;
              
    }
	
    /**
	 * Adding ECOMM Reports tab
	 */
	public void clickECOMMReportsTabForAdding() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div[3]/div[2]/table/tbody/tr[38]/td/a"));
		if (element != null)
			element.click();
	}
	
	 /**
	 * get Insufficient Privileges Message
	 */
	public String getInsufficientPrivilegesMsg() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/table/tbody/tr/td/span"));
		if (element != null)
			return element.getText();

		return "";	
	}
	
	 /**
	 * get forecast last modified 
	 * @return Forecast Last Modified
	 */
	public String getForecastLastModifiedText() {
		
		try {
			controller.waitUntilTime(driver, Controller.WAIT_LOW);
			WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div/form/div/div/div/div/div/i"));
			if (element != null)
				return element.getText();
		} catch (Exception e) {
			;
		}

		return "";	
	}
	
	 /**
	 * get outlook last modified
	 * @return Outlook Last Modified:
	 */
	public String getOutlookLastModifiedText() {
		
		try {
			controller.waitUntilTime(driver, Controller.WAIT_LOW);
			WebElement element = driver.findElement(By.xpath("/html/body/div/div[2]/table/tbody/tr/td[2]/div/div[2]/form/div/div/div/div/div/i"));
			if (element != null)
				return element.getText();
		} catch (Exception e) {
			;
		}

		return "";	
	}
	
	 /**
	 * check Company-Wide Forecast Present
	 */
	public Boolean isForecastLinkPresent() {
		
		controller.waitUntilTime(driver, Controller.WAIT_LOW);
		if (driver.findElement(By.linkText(SfdcConstants.COMPANY_WIDE_FORECAST)) != null)
			return true;
		else
			return false;	
	}
	

}