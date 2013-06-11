
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

/** It consists of all the test cases on the "Editions" Tab
 * @author hsodha
 * */

public class TestNewBizEditionLangSded extends SeleniumTest{

    public TestNewBizEditionLangSded(String name) {
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

    //Select the Professional Edition and select the required licences and thier quantities
    public void selectPELicences() throws Exception{

        //Select the Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select the licences and thier quantities
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");
        type("CrmGenericSfaPE_qty", "20");
        type("CrmGenericSssPE_qty", "5");
        type("CrmSFaRstrPE_qty", "5");
    }

    //Select the Enterprise Editiom and select the required licences and thier quantities
    public void selectEELicences() throws Exception{

        //Select the Professional Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select the licences and thier quantities
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");
        click("AulEE");
        click("Aul1EE");
        //click("PRMSeatsEE");
        type("CrmGenericSfaEE_qty", "20");
        type("CrmGenericSssEE_qty", "10");
        type("CrmSFaRstrEE_qty", "5");
        type("AulEE_qty", "5");
        type("Aul1EE_qty", "5");
        //type("PRMSeatsEE_qty", "5");
    }

    //Select the Unlimited Editiom and select the required licences and thier quantities
    public void selectUELicences() throws Exception{

        //Select the Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Select the licences and thier quantities
        click("CrmGenericSfaUE");
        click("CrmGenericSssUE");
        click("CrmSFaRstrUE");
        click("AulUE");
        click("Aul1UE");
        //click("PRMSeatsUE");
        type("CrmGenericSfaUE_qty", "20");
        type("CrmGenericSssUE_qty", "10");
        type("CrmSFaRstrUE_qty", "5");
        type("AulUE_qty", "5");
        type("Aul1UE_qty", "5");
        //type("PRMSeatsUE_qty", "5");
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


    /* Tests for Professional Edition */
    //Test to check when language is Portuguese
    @TestLabels("extended")
    public void testPEPortuguese() throws Exception{

        goEditionTab();
        selectPELicences();

        //Select the language Portuguese
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Portuguese Language Professional Edition"));
        assertTrue(isTextPresent("Portuguese Language Professional Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaPE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssPE_qty']"),"5");
        assertEquals(getValue("//input[@id='CrmSFaRstrPE_qty']"),"5");
        close();
        selectWindow("null");
    }

    //Test to check when language is Spanish
    @TestLabels("extended")
    public void testPESpanish() throws Exception{

        goEditionTab();
        selectPELicences();

        //Select the language Spanish
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Spanish Language Professional Edition"));
        assertTrue(isTextPresent("Spanish Language Professional Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaPE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssPE_qty']"),"5");
        assertEquals(getValue("//input[@id='CrmSFaRstrPE_qty']"),"5");
        close();
        selectWindow("null");

    }

    //Test to check when language is Simple Chinese
    @TestLabels("extended")
    public void testPESChinese() throws Exception{

        goEditionTab();
        selectPELicences();

        //Select the language Simplified Chinese
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Simplified Chinese Professional Edition"));
        assertTrue(isTextPresent("Simplified Chinese Professional Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaPE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssPE_qty']"),"5");
        assertEquals(getValue("//input[@id='CrmSFaRstrPE_qty']"),"5");
        close();
        selectWindow("null");

    }

    //Test to check when language is Traditional Chinese
    @TestLabels("extended")
    public void testPETChinese() throws Exception{

        goEditionTab();
        selectPELicences();

        //Select the language Traditional Chinese
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Traditional Chinese Professional Edition"));
        assertTrue(isTextPresent("Traditional Chinese Professional Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaPE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssPE_qty']"),"5");
        assertEquals(getValue("//input[@id='CrmSFaRstrPE_qty']"),"5");
        close();
        selectWindow("null");


    }

    //Test to check when All Languages
    @TestLabels("extended")
    public void testPEAllLang() throws Exception{

        goEditionTab();
        selectPELicences();

        //Select the All languages
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:All Languages\\s+");

        assertTrue(isTextPresent("Professional Edition License(s)"));
        assertTrue(isTextPresent("Professional Edition License(s) - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaPE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssPE_qty']"),"5");
        assertEquals(getValue("//input[@id='CrmSFaRstrPE_qty']"),"5");
        close();
        selectWindow("null");
    }

    /* Tests for Enterprise Edition */
    //Test to check when language is Portuguese
    @TestLabels("extended")
    public void testEEPortuguese() throws Exception{

        goEditionTab();
        selectEELicences();

        //Select the language Portuguese
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Portuguese Language Enterprise Edition"));
        assertTrue(isTextPresent("Portuguese Language Enterprise Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaEE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssEE_qty']"),"10");
        assertEquals(getValue("//input[@id='CrmSFaRstrEE_qty']"),"5");
        assertEquals(getValue("//input[@id='AulEE_qty']"),"5");
        assertEquals(getValue("//input[@id='Aul1EE_qty']"),"5");
        //assertEquals(getValue("//input[@id='PRMSeatsEE_qty']"),"5");
        close();
        selectWindow("null");
    }

    //Test to check when language is Spanish
    @TestLabels("extended")
    public void testEESpanish() throws Exception{

        goEditionTab();
        selectEELicences();

        //Select the language Spanish
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Spanish Language Enterprise Edition"));
        assertTrue(isTextPresent("Spanish Language Enterprise Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaEE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssEE_qty']"),"10");
        assertEquals(getValue("//input[@id='CrmSFaRstrEE_qty']"),"5");
        assertEquals(getValue("//input[@id='AulEE_qty']"),"5");
        assertEquals(getValue("//input[@id='Aul1EE_qty']"),"5");
        //assertEquals(getValue("//input[@id='PRMSeatsEE_qty']"),"5");
        close();
        selectWindow("null");
    }

    //Test to check when language is Simplified Chinese
    @TestLabels("extended")
    public void testEESChinese() throws Exception{

        goEditionTab();
        selectEELicences();

        //Select the language Spanish
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Simplified Chinese Enterprise Edition"));
        assertTrue(isTextPresent("Simplified Chinese Enterprise Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaEE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssEE_qty']"),"10");
        assertEquals(getValue("//input[@id='CrmSFaRstrEE_qty']"),"5");
        assertEquals(getValue("//input[@id='AulEE_qty']"),"5");
        assertEquals(getValue("//input[@id='Aul1EE_qty']"),"5");
        //assertEquals(getValue("//input[@id='PRMSeatsEE_qty']"),"5");
        close();
        selectWindow("null");
    }

    //Test to check when language is Traditional Chinese
    @TestLabels("extended")
    public void testEETChinese() throws Exception{

        goEditionTab();
        selectEELicences();

        //Select the language Spanish
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Traditional Chinese Enterprise Edition"));
        assertTrue(isTextPresent("Traditional Chinese Enterprise Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaEE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssEE_qty']"),"10");
        assertEquals(getValue("//input[@id='CrmSFaRstrEE_qty']"),"5");
        assertEquals(getValue("//input[@id='AulEE_qty']"),"5");
        assertEquals(getValue("//input[@id='Aul1EE_qty']"),"5");
        //assertEquals(getValue("//input[@id='PRMSeatsEE_qty']"),"5");
        close();
        selectWindow("null");
    }

   //Test to check when All languages
    @TestLabels("extended")
    public void testEEAllLang() throws Exception{

        goEditionTab();
        selectEELicences();

        //Select the language Spanish
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:All Languages\\s+");
        //waitForPageToLoad("30000");

        assertTrue(isTextPresent("Enterprise Edition License - Sales Force Automation"));
        assertTrue(isTextPresent("Enterprise Edition License - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaEE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssEE_qty']"),"10");
        assertEquals(getValue("//input[@id='CrmSFaRstrEE_qty']"),"5");
        assertEquals(getValue("//input[@id='AulEE_qty']"),"5");
        assertEquals(getValue("//input[@id='Aul1EE_qty']"),"5");
        //assertEquals(getValue("//input[@id='PRMSeatsEE_qty']"),"5");
        close();
        selectWindow("null");
    }

    /* Tests for Unlimited Edition */
    //test to check when language is Portuguese
    @TestLabels("extended")
    public void testUEPortuguese() throws Exception{

        goEditionTab();
        selectUELicences();

        //Select the language Portuguese
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Portuguese Language Unlimited Edition"));
        assertTrue(isTextPresent("Portuguese Language Unlimited Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaUE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssUE_qty']"),"10");
        assertEquals(getValue("//input[@id='CrmSFaRstrUE_qty']"),"5");
        assertEquals(getValue("//input[@id='AulUE_qty']"),"5");
        assertEquals(getValue("//input[@id='Aul1UE_qty']"),"5");
        //assertEquals(getValue("//input[@id='PRMSeatsUE_qty']"),"5");
        close();
        selectWindow("null");
    }

    //Test to check when language is Spanish
    @TestLabels("extended")
    public void testUESpanish() throws Exception{

        goEditionTab();
        selectUELicences();

        //Select the language Spanish
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Spanish Language Unlimited Edition"));
        assertTrue(isTextPresent("Spanish Language Unlimited Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaUE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssUE_qty']"),"10");
        assertEquals(getValue("//input[@id='CrmSFaRstrUE_qty']"),"5");
        assertEquals(getValue("//input[@id='AulUE_qty']"),"5");
        assertEquals(getValue("//input[@id='Aul1UE_qty']"),"5");
        //assertEquals(getValue("//input[@id='PRMSeatsUE_qty']"),"5");
        close();
        selectWindow("null");
    }

    //Test to check when language is Simplified Chinese
    @TestLabels("extended")
    public void testUESChinese() throws Exception{

        goEditionTab();
        selectUELicences();

        //Select the language Simplified Chinese
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Simplified Chinese Unlimited Edition"));
        assertTrue(isTextPresent("Simplified Chinese Unlimited Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaUE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssUE_qty']"),"10");
        assertEquals(getValue("//input[@id='CrmSFaRstrUE_qty']"),"5");
        assertEquals(getValue("//input[@id='AulUE_qty']"),"5");
        assertEquals(getValue("//input[@id='Aul1UE_qty']"),"5");
        //assertEquals(getValue("//input[@id='PRMSeatsUE_qty']"),"5");
        close();
        selectWindow("null");
    }

    //Test to check when language is Traditional Chinese
    @TestLabels("extended")
    public void testUETChinese() throws Exception{

        goEditionTab();
        selectUELicences();

        //Select the language Traditional Chinese
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        assertTrue(isTextPresent("Traditional Chinese Unlimited Edition"));
        assertTrue(isTextPresent("Traditional Chinese Unlimited Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaUE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssUE_qty']"),"10");
        assertEquals(getValue("//input[@id='CrmSFaRstrUE_qty']"),"5");
        assertEquals(getValue("//input[@id='AulUE_qty']"),"5");
        assertEquals(getValue("//input[@id='Aul1UE_qty']"),"5");
        //assertEquals(getValue("//input[@id='PRMSeatsUE_qty']"),"5");
        close();
        selectWindow("null");
    }

    //Test to check when language is Traditional Chinese
    @TestLabels("extended")
    public void testUEAllLang() throws Exception{

        goEditionTab();
        selectUELicences();

        //Select the All Languages
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:All Languages\\s+");
        //waitForPageToLoad("30000");

        assertTrue(isTextPresent("Unlimited Edition"));
        assertTrue(isTextPresent("Unlimited Edition - Salesforce Service & Support"));
        assertEquals(getValue("//input[@id='CrmGenericSfaUE_qty']"),"20");
        assertEquals(getValue("//input[@id='CrmGenericSssUE_qty']"),"10");
        assertEquals(getValue("//input[@id='CrmSFaRstrUE_qty']"),"5");
        assertEquals(getValue("//input[@id='AulUE_qty']"),"5");
        assertEquals(getValue("//input[@id='Aul1UE_qty']"),"5");
        //assertEquals(getValue("//input[@id='PRMSeatsUE_qty']"),"5");
        close();
        selectWindow("null");
    }

    /* Tests for Group Edition */
    //Test to check that the field for selecting languages is NOT present
    @TestLabels("extended")
    public void testGENoLang() throws Exception{

        goEditionTab();

        //Select the Group Edition
        click("TE");
        waitForPageToLoad("30000");

        //Check for the presence of the Language Selection
        assertTrue(isTextPresent( "Choose Language"));
        assertTrue(isTextPresent("All Languages"));
        close();
        selectWindow("null");
    }

    //Test to check if we get error when we change the selection from "All Languages" to any other language
    @TestLabels("extended")
    public void testGELangErr() throws Exception{

        goEditionTab();

        //Go back to Unlimited Edition
        click("UE");
        waitForPageToLoad("30000");

        //Since we have Unlimited Edition already selected, select the language as say Portuguese
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Now select Group Edition
        click("TE");
        waitForPageToLoad("30000");

        //Check if we get error
        assertTrue(isTextPresent("No Restricted Language version is available with Team Edition."));
        close();
        selectWindow("null");
    }

    /* Tests to check the Start Dates and End Dates based on the
     * Service term specified on the Basic Information tab
     */
    //Test for Unlimited Edition
    @TestLabels("extended")
    public void testUEsded() throws Exception{

        goEditionTab();

        //Select Unlimited Edition and select All Languages
        click("UE");
        waitForPageToLoad("30000");

        //Go to Basic Information Tab
        click("//a/span");
        waitForPageToLoad("30000");

        //Change the Contract Start Date
        type("ContractStartDate", "8/21/2008");
        waitForPageToLoad("30000");

        //Change the Contract Term
        type("SFDC.BasicInfo.BasicInfo.ContractTermUEVs2.ContractTerm", "8");

        //Go to the Editions Tab
        click("//a/span");
        waitForPageToLoad("30000");

        //Check if the Start Date and End Date are as expected
        assertTrue(isTextPresent("8/21/2008"));
        assertTrue(isTextPresent("4/20/2009"));
        close();
        selectWindow("null");
    }

    //Test for Enterprise Edition
    @TestLabels("extended")
    public void testEEsded() throws Exception{

        goEditionTab();

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Go to the BAsic Information Tab
        click("//a/span");
        waitForPageToLoad("30000");

        //Change the contract start date
        type("ContractStartDate", "8/18/2008");
        waitForPageToLoad("30000");

        //Change the Service Term
        type("SFDC.BasicInfo.BasicInfo.ContractTermUEVs2.ContractTerm", "7");

        //Go to Editions tab
        click("//a/span");
        waitForPageToLoad("30000");

        //Check if the Start Date and End Date are as expected
        assertTrue(isTextPresent("8/18/2008"));
        assertTrue(isTextPresent("3/17/2009"));
        close();
        selectWindow("null");
    }

    //Test for Professional Edition
    @TestLabels("extended")
    public void testPEsded() throws Exception{

        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Go to the Basic Information Tab
        click("//a/span");
        waitForPageToLoad("30000");

        //Change the Contract Start Date
        type("ContractStartDate", "8/25/2008");
        waitForPageToLoad("30000");

        //Change the Service Term
        type("SFDC.BasicInfo.BasicInfo.ContractTermUEVs2.ContractTerm", "5");

        //Go to Editions tab
        click("//a/span");
        waitForPageToLoad("30000");

        //Check if the Start Date and End Date are as expected
        assertTrue(isTextPresent("8/25/2008"));
        assertTrue(isTextPresent("1/24/2009"));
        close();
        selectWindow("null");
    }

    //Test for Group Edition
    @TestLabels("extended")
    public void testTEsded() throws Exception{

        goEditionTab();

        //Select Group Edition
        click("TE");
        waitForPageToLoad("30000");

        //Go to the Basic Information Tab
        click("//a/span");
        waitForPageToLoad("30000");

        //Change the contract start date
        type("ContractStartDate", "8/28/2008");
        waitForPageToLoad("30000");

        //Change the Service Term
        type("SFDC.BasicInfo.BasicInfo.ContractTermUEVs2.ContractTerm", "9");

        //Go to Editions tab
        click("//a/span");
        waitForPageToLoad("30000");

        //Check if the Start Date and End Date are as expected
        assertTrue(isTextPresent("8/28/2008"));
        assertTrue(isTextPresent("5/27/2009"));
        close();
        selectWindow("null");
    }

    /* Tests to check if the Suggestions are displayed when we change from one edition to other */

    //Close the pop up
    public void testClose() throws Exception{

        //close();
        stop();
    }

}
