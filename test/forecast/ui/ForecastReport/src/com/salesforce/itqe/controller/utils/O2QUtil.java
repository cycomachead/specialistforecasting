package com.salesforce.itqe.controller.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * Utility class for O2Q automation
 * @author bob
 *
 */
public class O2QUtil {
	
	/**
	 * 
	 * @param str
	 * @return double value of input string
	 */
	public static double getDoubleValue(String str){
		if(str != null && str.length() > 0){
			for(int i =0; i< str.length(); i++){
				
			    if(str.charAt(i) >= '0' && str.charAt(i) <= '9')
				  return Double.parseDouble(str.substring(i).replaceAll(",", ""));
		    }
		}
		
		
		return 0.00;
	}
	
    /**
     * get current quarter
     * @return current quarter
     */
	public static String getCurrentQuarter() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String dateNow = formatter.format(currentDate.getTime());
		int month = Integer.parseInt(dateNow.substring(dateNow.indexOf('.') + 1, dateNow.lastIndexOf('.')));
	
		String currentQuarter = "";
		if(month >= 2 && month <= 4) {
			currentQuarter = "Q1";
		} else if(month >= 5 && month <= 7) {
			currentQuarter = "Q2";
		} else if(month >= 8 && month <= 10) {
			currentQuarter = "Q3";
		} else if(month == 11 || month == 12 || month == 1) {
			currentQuarter = "Q4";
		}
		return currentQuarter;
	}
    
	/**
	 * get current fiscal year
	 * @return current fiscal year
	 */
	public static String getCurrentFiscalYear() {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String dateNow = formatter.format(currentDate.getTime());
		int month = Integer.parseInt(dateNow.substring(dateNow.indexOf('.') + 1, dateNow.lastIndexOf('.')));
		String currentFiscalYear = "FY";
		String fYear = dateNow.substring(0, dateNow.indexOf('.'));
	
		fYear = fYear.substring(2);
		Integer nYear = Integer.valueOf(fYear); 
		if(month == 1) {
			currentFiscalYear = "FY" + nYear;
		} else {
			currentFiscalYear = "FY" + (nYear + 1);
		}
		return currentFiscalYear;
	}
	
	  /**
     * get next quarter
     * @return next quarter
     */
	public static String getNextQuarter() {
		String currentQuarter = getCurrentQuarter();
		if(currentQuarter.equalsIgnoreCase("Q1"))
			return "Q2";
		else if(currentQuarter.equalsIgnoreCase("Q2"))
			return "Q3";
		else if(currentQuarter.equalsIgnoreCase("Q3"))
			return "Q4";
		else 
			return "Q1";
		
	}
	
	 /**
     * get previous quarter
     * @return previous quarter
     */
	public static String getPreviousQuarter() {
		String currentQuarter = getCurrentQuarter();
		if(currentQuarter.equalsIgnoreCase("Q1"))
			return "Q4";
		else if(currentQuarter.equalsIgnoreCase("Q2"))
			return "Q1";
		else if(currentQuarter.equalsIgnoreCase("Q3"))
			return "Q2";
		else 
			return "Q3";
		
	}
    
	/**
	 * get current fiscal year
	 * @return current fiscal year
	 */
	public static String getNextFiscalYear() {
		String currentYear = getCurrentFiscalYear();
		int year = Integer.parseInt(currentYear.substring(2)) + 1;
		
		return "FY" + year;
	}
	
	public static void main(String arg[]){
		System.out.println("xxxxxxxxxxxxx " + getPreviousQuarter() );
	}

}
