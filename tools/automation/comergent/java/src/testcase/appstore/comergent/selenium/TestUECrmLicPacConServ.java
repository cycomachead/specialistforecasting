/** Has tests to check if Packaged Consulting Services are getting displayed on the
 * Additional products page when the quantity of CRM licences for Unlimited Edition
 * is greater than 25
 * @author hsodha
 */

package testcase.appstore.comergent.selenium;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;

import test.runner.TestLabels;
import test.util.SeleniumTest;
import test.util.SeleniumTest.BrowserType;
import test.util.SeleniumTest.TargetBrowsers;
import test.util.GetQuoteForTest;

@TargetBrowsers(BrowserType.CHROME)


public class TestUECrmLicPacConServ extends SeleniumTest{

    public TestUECrmLicPacConServ(String name) {
        super(name);
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    Calendar today = Calendar.getInstance();
    String d=dateFormat.format(today.getTime());


    static String username=null;
    static String password=null;
    static String quote=null;
    static String qId=null;
    static Boolean flag=true;
    LoginResult lr=null;

    public void doLogin() throws Exception{

        setup();
        //Open the relative URL in the browser by passing the login information
        open("/login.jsp/?un="+username+"&pw="+password);
        waitForPageToLoad("120000");

        //Get Quote Number
        QueryResult result = query("Select Name from sfquote__Quote__c where sfquote__Quote_Name__c='"+quote+"'");
        SObject[] resultRecords = result.getRecords();
        String qnumber=resultRecords[0].getField("Name").toString();
        System.out.println(qnumber);

        //Get quote Id
        QueryResult qr = query("select id from sfquote__Quote__c where Name = '"+qnumber+"'");
        SObject rquote = qr.getRecords()[0];
        qId = rquote.getId().substring(0, 15);
        System.out.println(qId);

        //Open the quote
        open("/"+qId);
        //open("//AppStore?cmd=sfdcCatalogParameterToConfig2&qid=a24x000000000wQ&sessionid=441100D000000000062%21SCFhYQVSjbEpO8NDWpuXifRHO._Ab9tEh3iAQfCd6W0RAhAcQtEpAljIy2wSr3mRpdC4lKgsY3EYCI22BAHVIldBqEUaribFYCjDt_Us16cImlhBdyQSkn.4_b2OS..GzOAflcMFSCY.bj1wvruSGXBBMosAlg%3D%3D&saveUrl=https%3A%2F%2Fna1-aps2.soma.salesforce.com%2Fapex%2FQuotePopup%3Furl%3Dhttps%253A%252F%252Fna1-aps2.soma.salesforce.com%252Fa24x000000000wQ%26close%3D1&laterUrl=https%3A%2F%2Fna1-aps2.soma.salesforce.com%2Fapex%2FQuotePopup%3Furl%3Dhttps%253A%252F%252Fna1-aps2.soma.salesforce.com%252Fa24x000000000wQ%26close%3D1&endUserUrl=https%3A%2F%2Fna1-aps2.soma.salesforce.com&flow=default");

        flag=false;
    }


    public void goEditionTab() throws Exception{

        if(flag){doLogin();}

        else{

                open("/login.jsp/?un="+username+"&pw="+password);
                waitForPageToLoad("120000");

                open("/"+qId);
            }

        //Launch the configurator
        click("cmgt_configure_quote_dummy");
        waitForPopUp("Configurator", "30000");

        //Shift the control to the new popped up window
        selectWindow("Configurator");

        //Enter the start date and go to the edition tab
        type("ContractStartDate", d);
        waitForPageToLoad("30000");
        click("//a/span");
        waitForPageToLoad("30000");
    }

    public void setup() throws Exception{

        try{
            HashMap[] allQuote = new GetQuoteForTest().getAllQuotes();

            for(int i = 0; i<allQuote.length;i++){

                if((allQuote[i].get("usertype").toString().equals("AE")) && (allQuote[i].get("quote").toString().equals("CMGT Automated Test NewBiz Selenium"))){

                    username=allQuote[i].get("username").toString();
                    password=allQuote[i].get("password").toString();
                    quote=allQuote[i].get("quote").toString();
                }
            }

        }catch (Exception e){}
    }

    /* Tests to check if the Packaged consulting services are getting displayed on the
     * Addidional Produts page when total quantity of CRM licenses selected is less than 26
     * Positive test cases
     */
    //Test when all the CRM licences are selected with quantity more than the number of users of Org and less than 26
    @TestLabels("extended")
    public void testLessSfaSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when Sfa license is selected with quantity more than the number of users of Org and less than 26
    @TestLabels("extended")
    public void testLessSfaUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Sfa license
        click("CrmGenericSfaUE");

        //Quantity of Sfa license is less than 26
        type("CrmGenericSfaUE_qty", "25");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when Sss license is selected with quantity more than the number of users of Org and less than 26
    @TestLabels("extended")
    public void testLessSssUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Sss license
        click("CrmGenericSssUE");

        //Quantity of Sss license is less than 26
        type("CrmGenericSssUE_qty", "25");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when Rstr license is selected with quantity more than the number of users of Org and less than 26
    @TestLabels("extended")
    public void testLessRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Rstr license
        click("CrmSFaRstrUE");

        //Quantity of Rstr license is less than 26
        type("CrmSFaRstrUE_qty", "25");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Sss licenses are selected with quantity more than the number of users of Org and less than 26
    @TestLabels("extended")
    public void testLessSfaSssUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Sfa and Sss licenses
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");

        //Total Quantity of Sfa and Sss licenses is less than 26
        type("CrmGenericSfaUE_qty", "12");
        type("CrmGenericSssUE_qty", "11");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Rstr licenses are selected with quantity more than the number of users of Org and less than 26
    @TestLabels("extended")
    public void testLessSfaRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Sfa and Rstr licenses
        click("CrmGenericSfaUE");
        click("CrmSFaRstrUE");

        //Total Quantity of Sfa and Sss licenses is less than 26
        type("CrmGenericSfaUE_qty", "12");
        type("CrmSFaRstrUE_qty", "11");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Rstr licenses are selected with quantity more than the number of users of Org and less than 26
    @TestLabels("extended")
    public void testLessSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Sfa and Rstr licenses
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Total Quantity of Sfa and Sss licenses is less than 26
        type("CrmGenericSssUE_qty", "12");
        type("CrmSFaRstrUE_qty", "11");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    /* Tests to check if the Packaged Consulting Services are NOT getting displayed on the
     * Addidional Produts page when total quantity of CRM licenses selected is greater than 25
     * Negative test cases
     */

    //Test when quantity of Sfa license selected is greater than 25
    @TestLabels("extended")
    public void testGreaterSfaUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Sfa license
        click("CrmGenericSfaUE");

        //Quantity of Sfa license is greater than 25
        type("CrmGenericSfaUE_qty", "26");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when quantity of Sss license selected is greater than 25
    @TestLabels("extended")
    public void testGreaterSssUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Sss license
        click("CrmGenericSssUE");

        //Quantity of Sss license is greater than 25
        type("CrmGenericSssUE_qty", "26");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when quantity of Rstr license selected is greater than 25
    @TestLabels("extended")
    public void testGreaterRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Rstr license
        click("CrmSFaRstrUE");

        //Quantity of Rstr license is greater than 25
        type("CrmSFaRstrUE_qty", "26");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when quantity of Sfa and Sss licenses selected is greater than 25
    @TestLabels("extended")
    public void testGreaterSfaSssUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Sfa and Sss license
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");

        //Total quantity of Sfa and Sss licenses is greater than 25
        type("CrmGenericSfaUE_qty", "15");
        type("CrmGenericSssUE_qty", "13");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when quantity of Sfa and Rstr licenses selected is greater than 25
    @TestLabels("extended")
    public void testGreaterSfaRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Sfa and Rstr license
        click("CrmGenericSfaUE");
        click("CrmSFaRstrUE");

        //Total quantity of Sfa and Rstr licenses is greater than 25
        type("CrmGenericSfaUE_qty", "15");
        type("CrmSFaRstrUE_qty", "13");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when quantity of Sss and Rstr licenses selected is greater than 25
    @TestLabels("extended")
    public void testGreaterSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Sss and Rstr license
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Total quantity of Sss and Rstr licenses is greater than 25
        type("CrmGenericSssUE_qty", "15");
        type("CrmSFaRstrUE_qty", "13");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when total quantity of Sfa, Sss, and Rstr licenses selected is greater than 25
    @TestLabels("extended")
    public void testGreaterSfaSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Sfa, Sss, and Rstr licenses
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Total quantity of Sfa, Sss, and Rstr licenses is greater than 25
        type("CrmGenericSfaUE_qty", "10");
        type("CrmGenericSssUE_qty", "15");
        type("CrmSFaRstrUE_qty", "13");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    /* Tests for checking if the Packaged Consulting Services
     * are getting displayed when the language is changed to Portuguese
     */
    //Test when all the CRM licenses are selected with their total quantity is greater than number of users in Org and less than 26
    @TestLabels("extended")
    public void testPortuLessSfaSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select all Portuguese CRM licenses
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when total quantity of Sfa, Sss, and Rstr licenses selected is greater than 25
    @TestLabels("extended")
    public void testPortuGreaterSfaSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Portuguese Sfa, Sss, and Rstr licenses
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Total quantity of Sfa, Sss, and Rstr licenses is greater than 25
        type("CrmGenericSfaUE_qty", "10");
        type("CrmGenericSssUE_qty", "15");
        type("CrmSFaRstrUE_qty", "13");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    /* Tests for checking if the Packaged Consulting Services
     * are getting displayed when the language is changed to Spanish
     */
    //Test when all the CRM licenses are selected with their total quantity is greater than number of users in Org and less than 26
    @TestLabels("extended")
    public void testSpanishLessSfaSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Spanish language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select all Spanish CRM licenses
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when total quantity of Sfa, Sss, and Rstr licenses selected is greater than 25
    @TestLabels("extended")
    public void testSpanishGreaterSfaSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Spanish language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Spanish Sfa, Sss, and Rstr licenses
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Total quantity of Sfa, Sss, and Rstr licenses is greater than 25
        type("CrmGenericSfaUE_qty", "10");
        type("CrmGenericSssUE_qty", "15");
        type("CrmSFaRstrUE_qty", "13");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    /* Tests for checking if the Packaged Consulting Services
     * are getting displayed when the language is changed to Simplified Chinese
     */
    //Test when all the CRM licenses are selected with their total quantity is greater than number of users in Org and less than 26
    @TestLabels("extended")
    public void testSChineseLessSfaSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Simplified Chinese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select all Simplified Chinese CRM licenses
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when total quantity of Sfa, Sss, and Rstr licenses selected is greater than 25
    @TestLabels("extended")
    public void testSChineseGreaterSfaSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Simplified Chinese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Simplified Chinese Sfa, Sss, and Rstr licenses
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Total quantity of Sfa, Sss, and Rstr licenses is greater than 25
        type("CrmGenericSfaUE_qty", "10");
        type("CrmGenericSssUE_qty", "15");
        type("CrmSFaRstrUE_qty", "13");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    /* Tests for checking if the Packaged Consulting Services
     * are getting displayed when the language is changed to Traditional Chinese
     */
    //Test when all the CRM licenses are selected with their total quantity is greater than number of users in Org and less than 26
    @TestLabels("extended")
    public void testTChineseLessSfaSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Traditional Chinese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select all Traditional Chinese CRM licenses
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    //Test when total quantity of Sfa, Sss, and Rstr licenses selected is greater than 25
    @TestLabels("extended")
    public void testTChineseGreaterSfaSssRstrUE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Traditional Chinese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select Traditional Chinese Sfa, Sss, and Rstr licenses
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");

        //Total quantity of Sfa, Sss, and Rstr licenses is greater than 25
        type("CrmGenericSfaUE_qty", "10");
        type("CrmGenericSssUE_qty", "15");
        type("CrmSFaRstrUE_qty", "13");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    public void testClose() throws Exception{

        close();
        stop();
    }

}