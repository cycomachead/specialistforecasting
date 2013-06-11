/** Verify the correct Premier Support sku bundle(s) is/are
 *  automatically selected based on the selected license(s)
 *  when Professional Edition is selected
 *  @hsodha
*/

package testcase.appstore.comergent.selenium;

import java.text.SimpleDateFormat;
import java.util.Calendar;
//import java.util.Date;
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


public class TestNewBizzEEPremiumSupport extends SeleniumTest{

    public TestNewBizzEEPremiumSupport(String name) {
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

        type("QuoteValidDate", d);
        waitForPageToLoad("30000");

        click("//a/span");
        waitForPageToLoad("30000");

    }

    public void setup() throws Exception{

            HashMap[] allQuote = new GetQuoteForTest().getAllQuotes();

            for(int i = 0; i<allQuote.length;i++){

                if((allQuote[i].get("usertype").toString().equals("AE")) && (allQuote[i].get("quote").toString().equals("CMGT Automated Test NewBiz Selenium"))){

                    username=allQuote[i].get("username").toString();
                    password=allQuote[i].get("password").toString();
                    quote=allQuote[i].get("quote").toString();
                }
            }

    }


    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected EE, ALL LANGUAGES
     * CRM licenses
     */
    //Test when SFA is selected
    @TestLabels("extended")
    public void testSfaPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sfa license
        click("CrmGenericSfaEE");

        //Select the quantity of the SFA license
        type("CrmGenericSfaEE_qty", "3");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("3"));

        close();
        selectWindow("null");
    }

    //Test when All CRM licenses are selected
    @TestLabels("extended")
    public void testSfaSssRstrPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        // Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");


        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("20"));

        close();
        selectWindow("null");
    }


    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected EE and PORTUGUESE LANGUAGE
     * CRM licenses
     */
    //Test when All CRM licenses are selected
    @TestLabels("extended")
    public void testPortuSfaSssRstrPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select the quantity of all CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Portuguese Language Enterprise Edition");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Portuguese Language Enterprise Edition - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));

        close();
        selectWindow("null");
    }

    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected EE, SPANISH LANGUAGE
     * CRM licenses
     */
    //Test when all CRM licenses are selected
    @TestLabels("extended")
    public void testSpanishSfaSssRstrPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Spanish Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select the quantity of all the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Spanish Language Enterprise Edition");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Spanish Language Enterprise Edition - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));

        close();
        selectWindow("null");
    }

    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected EE, Simplified Chinese LANGUAGE
     * CRM licenses
     */

    //Test when All CRm licenses are selected
    @TestLabels("extended")
    public void testSChineseSfaSssRstrPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Simplified Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select the quantity of All the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

//      Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Simplified Chinese Enterprise Edition");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Simplified Chinese Enterprise Edition - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));

        close();
        selectWindow("null");
    }

    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected EE and Traditional Chinese LANGUAGE
     * CRM licenses
     */

    //Test when all CRM licenses are selected
    @TestLabels("extended")
    public void testTChineseSfaSssRstrPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Traditional Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select the quantity of the Sss and Rstr licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Traditional Chinese Enterprise Edition");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Traditional Chinese Enterprise Edition - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));

        close();
        selectWindow("null");
    }

    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected CRM licenses and
     * Platform Edition Licenses
     */
    //Test when CRM and one Platform license Aul are selected
    @TestLabels("extended")
    public void testCRMAulPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select Platform License
        click("AulEE");


        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of the AUL license
        type("AulEE_qty", "4");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Platform Edition");

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("4"));

        close();
        selectWindow("null");
    }

    //Test when CRM and one Platform license Aul1 are selected
    @TestLabels("extended")
    public void testCRMAul1PrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select Platform License
        click("Aul1EE");


        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of the AUL1 license
        type("Aul1EE_qty", "4");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Platform Edition 1-App");

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("4"));

        close();
        selectWindow("null");
    }

    //Test when all CRM and Platform licenses are selected
    @TestLabels("extended")
    public void testCRMAulAul1PrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select Platform Licenses
        click("AulEE");
        click("Aul1EE");


        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of the AUL licenses
        type("AulEE_qty", "3");
        type("Aul1EE_qty", "4");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Platform Edition - EE");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Platform Edition - EE - Restricted - 1 App");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[8]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Platform Edition");
        assertEquals(getText("//tr[9]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Platform Edition 1-App");

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("4"));
        assertTrue(isTextPresent("3"));

        close();
        selectWindow("null");
    }


    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected CRM licenses, Partner,
     * and Customer Licenses
     */

    //  Test when all CRM and Strategic Partner portal licenses are selected
    @TestLabels("extended")
    public void testCRMPRMStrtPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select Partner Portal License
        click("PRMSeatsStrtEE");


        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of the PRM license
        type("PRMSeatsStrtEE_qty", "4");


        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Strategic Partner Portal for EE");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Strategic Partner Portal");


        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("4"));

        close();
        selectWindow("null");
    }


    //Test when all CRM and Standard Partner portal licenses are selected
    @TestLabels("extended")
    public void testCRMPRMStdPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select Partner Portal License
        click("PRMSeatsStdEE");


        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of the PRM license
        type("PRMSeatsStdEE_qty", "4");


        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Standard Partner Portal for EE");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Standard Partner Portal");


        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("4"));

        close();
        selectWindow("null");
    }


    //Test when all CRM and all Partner portal licenses are selected
    @TestLabels("extended")
    public void testCRMPRMStrtStdPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select Partner Portal License
        click("PRMSeatsStrtEE");
        click("PRMSeatsStdEE");


        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of the PRm licenses
        type("PRMSeatsStrtEE_qty", "7");
        type("PRMSeatsStdEE_qty", "4");


        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Strategic Partner Portal for EE");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Standard Partner Portal for EE");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[8]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Strategic Partner Portal");
        assertEquals(getText("//tr[9]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Standard Partner Portal");

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("7"));
        assertTrue(isTextPresent("4"));

        close();
        selectWindow("null");
    }

    //Test when all CRM and Standard Customer portal licenses are selected
    @TestLabels("extended")
    public void testCRMPrtlStdPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select Partner Portal License
        click("PrtlStdEE");


        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of the Customer portal license
        type("PrtlStdEE_qty", "4");


        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Standard Customer Portal for EE");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Standard Customer Portal");


        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("4"));

        close();
        selectWindow("null");
    }


    //Test when all CRM and Basic Customer portal licenses are selected
    @TestLabels("extended")
    public void testCRMPrtlBscEEPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select Partner Portal License
        click("PrtlBscEE");


        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of the Customer portal license
        type("PrtlBscEE_qty", "4");


        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Basic Customer Portal for EE");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Basic Customer Portal");


        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("4"));

        close();
        selectWindow("null");
    }


    //Test when all CRM and Customer portal licenses are selected
    @TestLabels("extended")
    public void testCRMPrtlStdBscEEPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select Partner Portal License
        click("PrtlStdEE");
        click("PrtlBscEE");


        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of the Customer portal licenses
        type("PrtlStdEE_qty", "7");
        type("PrtlBscEE_qty", "4");


        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Standard Customer Portal for EE");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Basic Customer Portal for EE");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[8]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Standard Customer Portal");
        assertEquals(getText("//tr[9]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Basic Customer Portal");


        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("7"));
        assertTrue(isTextPresent("4"));

        close();
        selectWindow("null");
    }


    //Test when all CRM and All the portal licenses are selected
    @TestLabels("extended")
    public void testCRMPortalEEPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select All Portal License
        click("PRMSeatsStrtEE");
        click("PRMSeatsStdEE");
        click("PrtlStdEE");
        click("PrtlBscEE");


        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of all the portal licenses
        type("PRMSeatsStrtEE_qty", "7");
        type("PRMSeatsStdEE_qty", "4");
        type("PrtlStdEE_qty", "3");
        type("PrtlBscEE_qty", "2");


        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Strategic Partner Portal for EE");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Standard Partner Portal for EE");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Standard Customer Portal for EE");
        assertEquals(getText("//tr[8]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Basic Customer Portal for EE");
        assertEquals(getText("//tr[9]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[10]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Standard Customer Portal");
        assertEquals(getText("//tr[11]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Basic Customer Portal");
        assertEquals(getText("//tr[12]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Strategic Partner Portal");
        assertEquals(getText("//tr[13]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Standard Partner Portal");

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("7"));
        assertTrue(isTextPresent("4"));
        assertTrue(isTextPresent("3"));
        assertTrue(isTextPresent("2"));

        close();
        selectWindow("null");
    }


    // Test when all All the CRM, Platform, and portal licenses are selected
    @TestLabels("extended")
    public void testCRMPlatformPortalEEPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select All Platform and Portal Licenses
        click("AulEE");
        click("Aul1EE");
        click("PRMSeatsStrtEE");
        click("PRMSeatsStdEE");
        click("PrtlStdEE");
        click("PrtlBscEE");

        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of all licenses
        type("AulEE_qty", "7");
        type("Aul1EE_qty", "4");
        type("PRMSeatsStrtEE_qty", "3");
        type("PRMSeatsStdEE_qty", "2");
        type("PrtlBscEE_qty", "8");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Platform Edition - EE");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Platform Edition - EE - Restricted - 1 App");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Strategic Partner Portal for EE");
        assertEquals(getText("//tr[8]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Standard Partner Portal for EE");
        assertEquals(getText("//tr[9]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Standard Customer Portal for EE");
        assertEquals(getText("//tr[10]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Basic Customer Portal for EE");
        assertEquals(getText("//tr[11]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[12]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Platform Edition");
        assertEquals(getText("//tr[13]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Platform Edition 1-App");
        assertEquals(getText("//tr[14]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Standard Customer Portal");
        assertEquals(getText("//tr[15]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Basic Customer Portal");
        assertEquals(getText("//tr[16]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Strategic Partner Portal");
        assertEquals(getText("//tr[17]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Standard Partner Portal");


        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("7"));
        assertTrue(isTextPresent("4"));
        assertTrue(isTextPresent("3"));
        assertTrue(isTextPresent("2"));
        assertTrue(isTextPresent("8"));
        assertTrue(isTextPresent("1"));

        close();
        selectWindow("null");
    }


    // Test when all All the CRM, Platform, and portal licenses are selected on the Order Review
    @TestLabels("extended")
    public void testCRMPlatformPortalEEPrmrSupOrderEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Select All Platform and Portal Licenses
        click("AulEE");
        click("Aul1EE");
        click("PRMSeatsStrtEE");
        click("PRMSeatsStdEE");
        click("PrtlStdEE");
        click("PrtlBscEE");

        //Select the quantity of the CRM licenses
        type("CrmGenericSfaEE_qty", "5");
        type("CrmGenericSssEE_qty", "9");
        type("CrmSFaRstrEE_qty", "6");

        //Select the quantity of all licenses
        type("AulEE_qty", "7");
        type("Aul1EE_qty", "4");
        type("PRMSeatsStrtEE_qty", "3");
        type("PRMSeatsStdEE_qty", "2");
        type("PrtlBscEE_qty", "8");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        click("//li/a/span");
        waitForPageToLoad("30000");

        //Fill the addresss information
        type("SFDC.AddressPage.BillAddr.BillAddr.Street","One Market Street");
        select("SFDC.AddressPage.BillAddr.BillAddr.State", "label=CA");
        type("SFDC.AddressPage.BillAddr.BillAddr.City", "San Francisco");
        type("SFDC.AddressPage.BillAddr.BillAddr.Postal", "94105");
        click("CopyBilling");
        waitForPageToLoad("30000");

        //Go to payment info page
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select payment info
        click("DirectDebit");
        waitForPageToLoad("30000");

        //Go to order review page
        click("//li/a/span");
        waitForPageToLoad("30000");


        //Verify if the correctness of Premier Support SKU
        assertEquals(getText("//tr[2]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License");
        assertEquals(getText("//tr[3]/td[@id='border']/table/tbody/tr/td[2]"), "Enterprise License - Salesforce Service & Support");
        assertEquals(getText("//tr[4]/td[@id='border']/table/tbody/tr/td[2]"), "Restricted Use Enterprise Edition License");
        assertEquals(getText("//tr[5]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Platform Edition - EE");
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Platform Edition - EE - Restricted - 1 App");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Strategic Partner Portal for EE");
        assertEquals(getText("//tr[8]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Standard Partner Portal for EE");
        assertEquals(getText("//tr[9]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Standard Customer Portal for EE");
        assertEquals(getText("//tr[10]/td[@id='border']/table/tbody/tr/td[2]"), "Salesforce Basic Customer Portal for EE");
        assertEquals(getText("//tr[11]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - EE");
        assertEquals(getText("//tr[12]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Platform Edition");
        assertEquals(getText("//tr[13]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Platform Edition 1-App");
        assertEquals(getText("//tr[14]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Standard Customer Portal");
        assertEquals(getText("//tr[15]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Basic Customer Portal");
        assertEquals(getText("//tr[16]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Strategic Partner Portal");
        assertEquals(getText("//tr[17]/td[@id='border']/table/tbody/tr/td[2]"), "Premier Support (24X7) - Standard Partner Portal");


        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));
        assertTrue(isTextPresent("7"));
        assertTrue(isTextPresent("4"));
        assertTrue(isTextPresent("3"));
        assertTrue(isTextPresent("2"));
        assertTrue(isTextPresent("8"));
        assertTrue(isTextPresent("1"));

        close();
        selectWindow("null");
    }


    //Test Dates of the Premier Support licenses selected on the Additional products page
    @TestLabels("extended")
    public void testDateAddPrdtPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");


        today.add(Calendar.DATE, 365);
        String d2=dateFormat.format(today.getTime());
        System.out.println(d);
        System.out.println(d2);

        //Verify Start date of Premier Support
        assertEquals(getValue("//input[@id='PremierSupport_StartDateUev_uev']"), d);

        //Verify End date of Premier Support
        assertEquals(getText("//div[@id='tablechartdefault']/table/tbody/tr[2]/td[3]"), d2);

        close();
        selectWindow("null");
    }

    //Test Dates of the Premier Support licenses selected on the Quote Summary Page
    @TestLabels("extended")
    public void testDateQuoteSummaryPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");


        today.add(Calendar.DATE, 365);
        String d2=dateFormat.format(today.getTime());

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Checking the Start and the End dates on the Quote Summary page
        assertEquals(getText("//tr[4]/td/div[@id='contentbox3']/div[@id='tablechartdefault']/table/tbody/tr[5]/td[3]"), d);
        assertTrue(isTextPresent(d2));

        close();
        selectWindow("null");
    }

    //Test Dates of the Premier Support licenses selected on the Order Review page
    @TestLabels("extended")
    public void testDateOrdrReviewPrmrSupEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");


        today.add(Calendar.DATE, 365);
        String d2=dateFormat.format(today.getTime());

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        click("//li/a/span");
        waitForPageToLoad("30000");

        //Fill the addresss information
        type("SFDC.AddressPage.BillAddr.BillAddr.Street","One Market Street");
        select("SFDC.AddressPage.BillAddr.BillAddr.State", "label=CA");
        type("SFDC.AddressPage.BillAddr.BillAddr.City", "San Francisco");
        type("SFDC.AddressPage.BillAddr.BillAddr.Postal", "94105");
        click("CopyBilling");
        waitForPageToLoad("30000");

        //Go to payment info page
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select payment info
        click("DirectDebit");
        waitForPageToLoad("30000");

        //Go to order review page
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Checking the Start and the End dates on the Order review page
        assertEquals(getText("//tr[4]/td/div[@id='contentbox3']/div[@id='tablechartdefault']/table/tbody/tr[5]/td[2]"), d);
        assertTrue(isTextPresent(d2));

        close();
        selectWindow("null");
    }


    //Test to check the integration support gets displayed on the Additional Products oage
    @TestLabels("extended")
    public void testIntegrationSupportEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Check if the Custom Integration Support gets displayed
        assertTrue(isTextPresent("Custom Integration Support Level 1"));
        assertTrue(isTextPresent("Custom Integration Support Level 2"));
        assertTrue(isTextPresent("Custom Integration Support Level 3"));

        close();
        selectWindow("null");
    }


    //Test to check if the selected Custom Integration Support gets displayed in Quote Summary
    @TestLabels("extended")
    public void testIntegrationSupportQuoteSummEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control601");

        //Select Custom Integrtion Supports 1 & 2
        click("IntegSupLev1EE");
        click("IntegSupLev2EE");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Check if the selected Custom Integration Supports are getting displayed
        assertEquals(getText("//tr[6]/td[@id='border']/table/tbody/tr/td[2]"), "Custom Integration Support Level 1");
        assertEquals(getText("//tr[7]/td[@id='border']/table/tbody/tr/td[2]"), "Custom Integration Support Level 2");


        close();
        selectWindow("null");
    }


    public void testClose() throws Exception{

        stop();
    }

}