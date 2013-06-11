/** Verify the correct Premier Support sku bundle(s) is/are
 *  automatically selected based on the selected license(s)
 *  when Professional Edition is selected
 *  @hsodha
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


public class TestNewBizzPEPremiumSupport extends SeleniumTest{

    public TestNewBizzPEPremiumSupport(String name) {
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


    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected PE and ALL LANGUAGES
     * CRM licenses
     */
    //Test when SFA is selected
    @TestLabels("extended")
    public void testSfaPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Sfa license
        click("CrmGenericSfaPE");

        //Select the quantity of the SFA license
        type("CrmGenericSfaPE_qty", "3");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Professional Edition License(s)"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("3"));

        close();
        selectWindow("null");
    }

    //Test when Sss is selected
    @TestLabels("extended")
    public void testSssPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Sss license
        click("CrmGenericSssPE");

        //Select the quantity of the Sss license
        type("CrmGenericSssPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Professional Edition License(s) - Salesforce Service & Support"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("5"));

        close();
        selectWindow("null");
    }

    //Test when Rstr is selected
    @TestLabels("extended")
    public void testRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Rstr license
        click("CrmSFaRstrPE");

        //Select the quantity of the Rstr license
        type("CrmSFaRstrPE_qty", "7");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("7"));

        close();
        selectWindow("null");
    }

//	Test when Sfa and Sss are selected
    @TestLabels("extended")
    public void testSfaSssPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Sfa and Sss CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");

        //Select the quantity of the Sfa and Sss licenses
        type("CrmGenericSfaPE_qty", "7");
        type("CrmGenericSssPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Professional Edition License(s)"));
        assertTrue(isTextPresent("Professional Edition License(s) - Salesforce Service & Support"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("12"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Rstr are selected
    @TestLabels("extended")
    public void testSfaRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Sfa and Rstr CRM licenses
        click("CrmGenericSfaPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sfa and Rstr licenses
        type("CrmGenericSfaPE_qty", "6");
        type("CrmSFaRstrPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Professional Edition License(s)"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("11"));

        close();
        selectWindow("null");
    }

    //Test when Sss and Rstr are selected
    @TestLabels("extended")
    public void testSssRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Sss and Rstr CRM licenses
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sss and Rstr licenses
        type("CrmGenericSssPE_qty", "9");
        type("CrmSFaRstrPE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Professional Edition License(s) - Salesforce Service & Support"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("15"));

        close();
        selectWindow("null");
    }

    //Test when Sfa, Sss, and Rstr are selected
    @TestLabels("extended")
    public void testSfaSssRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sss and Rstr licenses
        type("CrmGenericSfaPE_qty", "5");
        type("CrmGenericSssPE_qty", "9");
        type("CrmSFaRstrPE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Professional Edition License(s)"));
        assertTrue(isTextPresent("Professional Edition License(s) - Salesforce Service & Support"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));

        close();
        selectWindow("null");
    }

    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected PE and PORTUGUESE LANGUAGE
     * CRM licenses
     */
    //Test when SFA is selected
    @TestLabels("extended")
    public void testPortuSfaPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Sfa license
        click("CrmGenericSfaPE");

        //Select the quantity of the SFA license
        type("CrmGenericSfaPE_qty", "3");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Portuguese Language Professional Edition"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("3"));

        close();
        selectWindow("null");
    }

    //Test when Sss is selected
    @TestLabels("extended")
    public void testPortuSssPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Sss license
        click("CrmGenericSssPE");

        //Select the quantity of the Sss license
        type("CrmGenericSssPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Portuguese Language Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("5"));

        close();
        selectWindow("null");
    }

    //Test when Rstr is selected
    @TestLabels("extended")
    public void testPortuRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Rstr license
        click("CrmSFaRstrPE");

        //Select the quantity of the Rstr license
        type("CrmSFaRstrPE_qty", "7");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("7"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Sss are selected
    @TestLabels("extended")
    public void testPortuSfaSssPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Sfa and Sss CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");

        //Select the quantity of the Sfa and Sss licenses
        type("CrmGenericSfaPE_qty", "7");
        type("CrmGenericSssPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Portuguese Language Professional Edition"));
        assertTrue(isTextPresent("Portuguese Language Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("12"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Rstr are selected
    @TestLabels("extended")
    public void testPortuSfaRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Sfa and Rstr CRM licenses
        click("CrmGenericSfaPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sfa and Rstr licenses
        type("CrmGenericSfaPE_qty", "6");
        type("CrmSFaRstrPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Portuguese Language Professional Edition"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("11"));

        close();
        selectWindow("null");
    }

    //Test when Sss and Rstr are selected
    @TestLabels("extended")
    public void testPortuSssRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Sss and Rstr CRM licenses
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sss and Rstr licenses
        type("CrmGenericSssPE_qty", "9");
        type("CrmSFaRstrPE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Portuguese Language Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("15"));

        close();
        selectWindow("null");
    }

    //Test when Sfa, Sss, and Rstr are selected
    @TestLabels("extended")
    public void testPortuSfaSssRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sss and Rstr licenses
        type("CrmGenericSfaPE_qty", "5");
        type("CrmGenericSssPE_qty", "9");
        type("CrmSFaRstrPE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Portuguese Language Professional Edition"));
        assertTrue(isTextPresent("Portuguese Language Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));

        close();
        selectWindow("null");
    }

    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected PE and SPANISH LANGUAGE
     * CRM licenses
     */
    //Test when SFA is selected
    @TestLabels("extended")
    public void testSpanishSfaPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Spanish Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select Sfa license
        click("CrmGenericSfaPE");

        //Select the quantity of the SFA license
        type("CrmGenericSfaPE_qty", "3");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Spanish Language Professional Edition"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("3"));

        close();
        selectWindow("null");
    }

    //Test when Sss is selected
    @TestLabels("extended")
    public void testSpanishSssPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Spanish Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select Sss license
        click("CrmGenericSssPE");

        //Select the quantity of the Sss license
        type("CrmGenericSssPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Spanish Language Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("5"));

        close();
        selectWindow("null");
    }

    //Test when Rstr is selected
    @TestLabels("extended")
    public void testSpanishRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Spanish Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select Rstr license
        click("CrmSFaRstrPE");

        //Select the quantity of the Rstr license
        type("CrmSFaRstrPE_qty", "7");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("7"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Sss are selected
    @TestLabels("extended")
    public void testSpanishSfaSssPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Spanish Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select Sfa and Sss CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");

        //Select the quantity of the Sfa and Sss licenses
        type("CrmGenericSfaPE_qty", "7");
        type("CrmGenericSssPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Spanish Language Professional Edition"));
        assertTrue(isTextPresent("Spanish Language Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("12"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Rstr are selected
    @TestLabels("extended")
    public void testSpanishSfaRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Spanish Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select Sfa and Rstr CRM licenses
        click("CrmGenericSfaPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sfa and Rstr licenses
        type("CrmGenericSfaPE_qty", "6");
        type("CrmSFaRstrPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Spanish Language Professional Edition"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("11"));

        close();
        selectWindow("null");
    }

    //Test when Sss and Rstr are selected
    @TestLabels("extended")
    public void testSpanishSssRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Spanish Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select Sss and Rstr CRM licenses
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sss and Rstr licenses
        type("CrmGenericSssPE_qty", "9");
        type("CrmSFaRstrPE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Spanish Language Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("15"));

        close();
        selectWindow("null");
    }

    //Test when Sfa, Sss, and Rstr are selected
    @TestLabels("extended")
    public void testSpanishSfaSssRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Spanish Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Spanish\\s+");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sss and Rstr licenses
        type("CrmGenericSfaPE_qty", "5");
        type("CrmGenericSssPE_qty", "9");
        type("CrmSFaRstrPE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Spanish Language Professional Edition"));
        assertTrue(isTextPresent("Spanish Language Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));

        close();
        selectWindow("null");
    }

    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected PE and Simplified Chinese LANGUAGE
     * CRM licenses
     */
    //Test when SFA is selected
    @TestLabels("extended")
    public void testSChineseSfaPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Simplified Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Sfa license
        click("CrmGenericSfaPE");

        //Select the quantity of the SFA license
        type("CrmGenericSfaPE_qty", "3");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Simplified Chinese Professional Edition"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("3"));

        close();
        selectWindow("null");
    }

    //Test when Sss is selected
    @TestLabels("extended")
    public void testSChineseSssPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Simplified Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Sss license
        click("CrmGenericSssPE");

        //Select the quantity of the Sss license
        type("CrmGenericSssPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Simplified Chinese Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("5"));

        close();
        selectWindow("null");
    }

    //Test when Rstr is selected
    @TestLabels("extended")
    public void testSChineseRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Simplified Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Rstr license
        click("CrmSFaRstrPE");

        //Select the quantity of the Rstr license
        type("CrmSFaRstrPE_qty", "7");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("7"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Sss are selected
    @TestLabels("extended")
    public void testSChineseSfaSssPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Simplified Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Sfa and Sss CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");

        //Select the quantity of the Sfa and Sss licenses
        type("CrmGenericSfaPE_qty", "7");
        type("CrmGenericSssPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Simplified Chinese Professional Edition"));
        assertTrue(isTextPresent("Simplified Chinese Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("12"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Rstr are selected
    @TestLabels("extended")
    public void testSChineseSfaRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Simplified Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Sfa and Rstr CRM licenses
        click("CrmGenericSfaPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sfa and Rstr licenses
        type("CrmGenericSfaPE_qty", "6");
        type("CrmSFaRstrPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Simplified Chinese Professional Edition"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("11"));

        close();
        selectWindow("null");
    }

    //Test when Sss and Rstr are selected
    @TestLabels("extended")
    public void testSChineseSssRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Simplified Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Sss and Rstr CRM licenses
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sss and Rstr licenses
        type("CrmGenericSssPE_qty", "9");
        type("CrmSFaRstrPE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Simplified Chinese Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("15"));

        close();
        selectWindow("null");
    }

    //Test when Sfa, Sss, and Rstr are selected
    @TestLabels("extended")
    public void testSChineseSfaSssRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Simplified Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Simplified Chinese\\s+");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sss and Rstr licenses
        type("CrmGenericSfaPE_qty", "5");
        type("CrmGenericSssPE_qty", "9");
        type("CrmSFaRstrPE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Simplified Chinese Professional Edition"));
        assertTrue(isTextPresent("Simplified Chinese Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));

        close();
        selectWindow("null");
    }

    /* Tests to Verify the correct Premier Support sku bundle(s) is/are
     * automatically selected based on the selected PE and Traditional Chinese LANGUAGE
     * CRM licenses
     */
    //Test when SFA is selected
    @TestLabels("extended")
    public void testTChineseSfaPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Traditional Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Sfa license
        click("CrmGenericSfaPE");

        //Select the quantity of the SFA license
        type("CrmGenericSfaPE_qty", "3");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Traditional Chinese Professional Edition"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("3"));

        close();
        selectWindow("null");
    }

    //Test when Sss is selected
    @TestLabels("extended")
    public void testTChineseSssPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Traditional Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Sss license
        click("CrmGenericSssPE");

        //Select the quantity of the Sss license
        type("CrmGenericSssPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Traditional Chinese Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("5"));

        close();
        selectWindow("null");
    }

    //Test when Rstr is selected
    @TestLabels("extended")
    public void testTChineseRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Traditional Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Rstr license
        click("CrmSFaRstrPE");

        //Select the quantity of the Rstr license
        type("CrmSFaRstrPE_qty", "7");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the quantity of Premier Support
        assertTrue(isTextPresent("7"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Sss are selected
    @TestLabels("extended")
    public void testTChineseSfaSssPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Traditional Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Sfa and Sss CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");

        //Select the quantity of the Sfa and Sss licenses
        type("CrmGenericSfaPE_qty", "7");
        type("CrmGenericSssPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Traditional Chinese Professional Edition"));
        assertTrue(isTextPresent("Traditional Chinese Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("12"));

        close();
        selectWindow("null");
    }

    //Test when Sfa and Rstr are selected
    @TestLabels("extended")
    public void testTChineseSfaRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Traditional Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Sfa and Rstr CRM licenses
        click("CrmGenericSfaPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sfa and Rstr licenses
        type("CrmGenericSfaPE_qty", "6");
        type("CrmSFaRstrPE_qty", "5");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Traditional Chinese Professional Edition"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("11"));

        close();
        selectWindow("null");
    }

    //Test when Sss and Rstr are selected
    @TestLabels("extended")
    public void testTChineseSssRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Traditional Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select Sss and Rstr CRM licenses
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sss and Rstr licenses
        type("CrmGenericSssPE_qty", "9");
        type("CrmSFaRstrPE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Traditional Chinese Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("15"));

        close();
        selectWindow("null");
    }

    //Test when Sfa, Sss, and Rstr are selected
    @TestLabels("extended")
    public void testTChineseSfaSssRstrPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select Traditional Chinese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Traditional Chinese\\s+");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Select the quantity of the Sss and Rstr licenses
        type("CrmGenericSfaPE_qty", "5");
        type("CrmGenericSssPE_qty", "9");
        type("CrmSFaRstrPE_qty", "6");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");

        //Go to the Quote Summary page
        click("top_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify if the correctness of Premier Support SKU
        assertTrue(isTextPresent("Traditional Chinese Professional Edition"));
        assertTrue(isTextPresent("Traditional Chinese Professional Edition - Salesforce Service & Support"));
        assertTrue(isTextPresent("Restricted Use Professional Edition License"));
        assertTrue(isTextPresent("Premier Support (24X7)"));

        //Verify the total quantity of Premier Support
        assertTrue(isTextPresent("20"));

        close();
        selectWindow("null");
    }

    //Test Dates of the Premier Support licenses selected on the Quote Summary Page
    @TestLabels("extended")
    public void testDateQuoteSummaryPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");


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
    public void testDateOrdrReviewPrmrSupPE() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Professional Edition
        click("PE");
        waitForPageToLoad("30000");

        //Select all CRM licenses
        click("CrmGenericSfaPE");
        click("CrmGenericSssPE");
        click("CrmSFaRstrPE");

        //Go to Additional Products Tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select the premier Support
        click("PremierSupport");
        click("control757");


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


    public void testClose() throws Exception{

        close();
        stop();
    }

}