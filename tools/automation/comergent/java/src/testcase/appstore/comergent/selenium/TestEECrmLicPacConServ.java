/** Has tests to check if Packaged Consulting Services are getting displayed on the
 * Additional products page when the quantity of CRM licences for Enterprise Edition
 * is greater than 25
 * @author hsodha
 */

package testcase.appstore.comergent.selenium;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;

import test.runner.TestLabels;
import test.util.SeleniumTest;
import test.util.SeleniumTest.BrowserType;
import test.util.SeleniumTest.TargetBrowsers;
import test.util.GetQuoteForTest;
import com.sforce.soap.partner.LoginResult;

@TargetBrowsers(BrowserType.CHROME)


public class TestEECrmLicPacConServ extends SeleniumTest{

    public TestEECrmLicPacConServ(String name) {
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
    public void testLessSfaSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

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
    public void testLessSfaEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sfa license
        click("CrmGenericSfaEE");

        //Quantity of Sfa license is less than 26
        type("CrmGenericSfaEE_qty", "25");

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
    public void testLessSssEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sss license
        click("CrmGenericSssEE");

        //Quantity of Sss license is less than 26
        type("CrmGenericSssEE_qty", "25");

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
    public void testLessRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Rstr license
        click("CrmSFaRstrEE");

        //Quantity of Rstr license is less than 26
        type("CrmSFaRstrEE_qty", "25");

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
    public void testLessSfaSssEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sfa and Sss licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");

        //Total Quantity of Sfa and Sss licenses is less than 26
        type("CrmGenericSfaEE_qty", "12");
        type("CrmGenericSssEE_qty", "11");

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
    public void testLessSfaRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sfa and Rstr licenses
        click("CrmGenericSfaEE");
        click("CrmSFaRstrEE");

        //Total Quantity of Sfa and Sss licenses is less than 26
        type("CrmGenericSfaEE_qty", "12");
        type("CrmSFaRstrEE_qty", "11");

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
    public void testLessSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sfa and Rstr licenses
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Total Quantity of Sfa and Sss licenses is less than 26
        type("CrmGenericSssEE_qty", "12");
        type("CrmSFaRstrEE_qty", "11");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are displayed
        assertTrue(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }

    /* Tests to check if the Packaged consulting services are NOT getting displayed on the
     * Addidional Produts page when total quantity of CRM licenses selected is greater than 25
     * Negetive test cases
     */

    //Test when quantity of Sfa license selected is greater than 25
    @TestLabels("extended")
    public void testGreaterSfaEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sfa license
        click("CrmGenericSfaEE");

        //Quantity of Sfa license is greater than 25
        type("CrmGenericSfaEE_qty", "26");

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
    public void testGreaterSssEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sss license
        click("CrmGenericSssEE");

        //Quantity of Sss license is greater than 25
        type("CrmGenericSssEE_qty", "26");

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
    public void testGreaterRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sss license
        click("CrmSFaRstrEE");

        //Quantity of Rstr license is greater than 25
        type("CrmSFaRstrEE_qty", "26");

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
    public void testGreaterSfaSssEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sfa and Sss license
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");

        //Total quantity of Sfa and Sss licenses is greater than 25
        type("CrmGenericSfaEE_qty", "15");
        type("CrmGenericSssEE_qty", "13");

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
    public void testGreaterSfaRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sfa and Rstr license
        click("CrmGenericSfaEE");
        click("CrmSFaRstrEE");

        //Total quantity of Sfa and Rstr licenses is greater than 25
        type("CrmGenericSfaEE_qty", "15");
        type("CrmSFaRstrEE_qty", "13");

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
    public void testGreaterSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sss and Rstr license
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Total quantity of Sss and Rstr licenses is greater than 25
        type("CrmGenericSssEE_qty", "15");
        type("CrmSFaRstrEE_qty", "13");

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
    public void testGreaterSfaSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Sfa, Sss, and Rstr licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Total quantity of Sfa, Sss, and Rstr licenses is greater than 25
        type("CrmGenericSfaEE_qty", "10");
        type("CrmGenericSssEE_qty", "15");
        type("CrmSFaRstrEE_qty", "13");

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
    public void testPortuLessSfaSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all Portuguese CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

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
    public void testPortuGreaterSfaSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Portuguese Sfa, Sss, and Rstr licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Total quantity of Sfa, Sss, and Rstr licenses is greater than 25
        type("CrmGenericSfaEE_qty", "10");
        type("CrmGenericSssEE_qty", "15");
        type("CrmSFaRstrEE_qty", "13");

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
    public void testSpanishLessSfaSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Spanish language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all Spanish CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

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
    public void testSpanishGreaterSfaSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Spanish language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Spanish Sfa, Sss, and Rstr licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Total quantity of Sfa, Sss, and Rstr licenses is greater than 25
        type("CrmGenericSfaEE_qty", "10");
        type("CrmGenericSssEE_qty", "15");
        type("CrmSFaRstrEE_qty", "13");

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
    public void testSChineseLessSfaSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Simplified Chinese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all Simplified Chinese CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

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
    public void testSChineseGreaterSfaSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Simplified Chinese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Simplified Chinese Sfa, Sss, and Rstr licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Total quantity of Sfa, Sss, and Rstr licenses is greater than 25
        type("CrmGenericSfaEE_qty", "10");
        type("CrmGenericSssEE_qty", "15");
        type("CrmSFaRstrEE_qty", "13");

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
    public void testTChineseLessSfaSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Traditional Chinese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select all Traditional Chinese CRM licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

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
    public void testTChineseGreaterSfaSssRstrEE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Traditional Chinese language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select Traditional Chinese Sfa, Sss, and Rstr licenses
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Total quantity of Sfa, Sss, and Rstr licenses is greater than 25
        type("CrmGenericSfaEE_qty", "10");
        type("CrmGenericSssEE_qty", "15");
        type("CrmSFaRstrEE_qty", "13");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the Packaged Consulting Services are NOT displayed
        assertFalse(isTextPresent("Packaged Consulting Services"));

        close();
        selectWindow("null");
    }


    public void testClose() throws Exception{

        stop();
    }

}