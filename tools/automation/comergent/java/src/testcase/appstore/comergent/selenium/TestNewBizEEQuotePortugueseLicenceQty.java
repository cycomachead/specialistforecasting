/** This class consists of the tests to check the quantity of Portuguese Language licences for EE for AE user
 * @author hsodha
 * */

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


public class TestNewBizEEQuotePortugueseLicenceQty extends SeleniumTest{

    public TestNewBizEEQuotePortugueseLicenceQty(String name) {
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


    /*Tests to check if the errors are thrown when appropriate
     * quantity of Portuguese licences are not selected in Enterprise Edition
     */
    //Test which checks if error is thrown when NO licences are selected
    @TestLabels("extended")
    public void testEENoPortuLicence() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Go to next tab without selecting any licence
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the error is displayed
        assertTrue(isTextPresent("Errors"));
        assertTrue(isTextPresent("You have ordered less Enterprise Edition users than the number registered in your organization (2). You can fulfill this requirement by selecting any combined quantity of the highlighted products."));
        close();
        selectWindow("null");
    }

    //Tests which checks if error is thrown when only one licence of quantity "1" is selected
    @TestLabels("extended")
    public void testEEOnePortuLicenceSfa() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");


        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select the first licence, now it has quantity "1"
        click("CrmGenericSfaEE");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the error is displayed
        assertTrue(isTextPresent("Errors"));
        assertTrue(isTextPresent("You have ordered less Enterprise Edition users than the number registered in your organization (2). You can fulfill this requirement by selecting any combined quantity of the highlighted products."));

        close();
        selectWindow("null");
    }

    @TestLabels("extended")
    public void testEEOnePortuLicenceSss() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select second licence, now it has quantity "1"
        click("CrmGenericSssEE");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the error is displayed
        assertTrue(isTextPresent("Errors"));
        assertTrue(isTextPresent("You have ordered less Enterprise Edition users than the number registered in your organization (2). You can fulfill this requirement by selecting any combined quantity of the highlighted products."));

        close();
        selectWindow("null");
    }

    @TestLabels("extended")
    public void testEEOnePortuLicenceRstr() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select third licence, now it has quantity "1"
        click("CrmSFaRstrEE");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if the error is displayed
        assertTrue(isTextPresent("Errors"));
        assertTrue(isTextPresent("You have ordered less Enterprise Edition users than the number registered in your organization (2). You can fulfill this requirement by selecting any combined quantity of the highlighted products."));

        close();
        selectWindow("null");
    }

    //Tests to select any 2 of the 3 licences and chek if errors are thrown
    @TestLabels("extended")
    public void testEESfaSssPortuLicence() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select the first and second licence, by default thier quantity will be 1
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if we reached on the next tab or it gave errors
        assertTrue(isTextPresent("Additional Products"));

        //Go back to the previou Edition tab and deselect the selected licences
        click("//td[2]/a/span");
        waitForPageToLoad("30000");

        close();
        selectWindow("null");

    }

    @TestLabels("extended")
    public void testEESfaRstrPortuLicence() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");


        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select the first and third licence, by default thier quantity will be 1
        click("CrmGenericSfaEE");
        click("CrmSFaRstrEE");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if we reached on the next tab or it gave errors
        assertTrue(isTextPresent("Additional Products"));

        //Go back to the previou Edition tab and deselect the selected licences
        click("//td[2]/a/span");
        waitForPageToLoad("30000");

        close();
        selectWindow("null");

    }

    @TestLabels("extended")
    public void testEESssRstrPortuLicence() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select the second and third licence, by default thier quantity will be 1
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //Go to next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if we reached on the next tab or it gave errors
        assertTrue(isTextPresent("Additional Products"));

        //Go back to the previou Edition tab and deselect the selected licences
        click("//td[2]/a/span");
        waitForPageToLoad("30000");

        close();
        selectWindow("null");

    }

    //Test to check when quantity of first licence selected is 2
    @TestLabels("extended")
    public void testEETwoSfaPortuLicence() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select second licence
        click("CrmGenericSfaEE");

        //make it of quantity 2
        type("CrmGenericSfaEE_qty", "2");

        //Go to the next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if we reach the next tab
        assertTrue(isTextPresent("Additional Products"));

        //Go back to the previou Edition tab and deselect the selected licences
        click("//td[2]/a/span");
        waitForPageToLoad("30000");

        close();
        selectWindow("null");
    }

    //Test to check when quantity of second licence selected is 2
    @TestLabels("extended")
    public void testEETwoSssPortuLicence() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select second licence
        click("CrmGenericSssEE");

        //make it of quantity 2
        type("CrmGenericSssEE_qty", "2");

        //Go to the next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if we reach the next tab
        assertTrue(isTextPresent("Additional Products"));

        //Go back to the previou Edition tab and deselect the selected licences
        click("//td[2]/a/span");
        waitForPageToLoad("30000");

        close();
        selectWindow("null");
    }

    //Test to check when quantity of third licence selected is 2
    @TestLabels("extended")
    public void testEETwoRstrPortuLicence() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select third licence
        click("CrmSFaRstrEE");

        //make it of quantity 2
        type("CrmSFaRstrEE_qty", "2");

        //Go to the next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if we reach the next tab
        assertTrue(isTextPresent("Additional Products"));

        //Go back to the previou Edition tab and deselect the selected licences
        click("//td[2]/a/span");
        waitForPageToLoad("30000");

        close();
        selectWindow("null");
    }

    /* Test to check when first licence is selected with quantity 2
     * and second licence with quantity 1
     */
    @TestLabels("extended")
    public void testEETwoSfaOneSssPortuLicence() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select first and second licence
        click("CrmGenericSfaEE");
        click("CrmGenericSssEE");

        //make quantity of first licence 2
        type("CrmGenericSfaEE_qty", "2");

        //Go to the next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if we reach the next tab
        assertTrue(isTextPresent("Additional Products"));

        //Go back to the previou Edition tab and deselect the selected licences
        click("//td[2]/a/span");
        waitForPageToLoad("30000");

        close();
        selectWindow("null");
    }

    /* Test to check when first licence is selected with quantity 2
     * and third licence with quantity 1
     */
    @TestLabels("extended")
    public void testEETwoSfaOneRstrPortuLicence() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");


        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select first and second licence
        click("CrmGenericSfaEE");
        click("CrmSFaRstrEE");

        //make quantity of first licence 2
        type("CrmGenericSfaEE_qty", "2");

        //Go to the next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if we reach the next tab
        assertTrue(isTextPresent("Additional Products"));

        //Go back to the previou Edition tab and deselect the selected licences
        click("//td[2]/a/span");
        waitForPageToLoad("30000");

        close();
        selectWindow("null");
    }

    /* Test to check when second licence is selected with quantity 2
     * and third licence with quantity 1
     */
    @TestLabels("extended")
    public void testEETwoSssOneRstrPortuLicence() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select first and second licence
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //make quantity of first licence 2
        type("CrmGenericSssEE_qty", "2");

        //Go to the next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if we reach the next tab
        assertTrue(isTextPresent("Additional Products"));

        //Go back to the previou Edition tab and deselect the selected licences
        click("//td[2]/a/span");
        waitForPageToLoad("30000");

        close();
        selectWindow("null");
    }

    /* Test to check when third licence is selected with quantity 2
     * and second licence with quantity 1
     */
    @TestLabels("extended")
    public void testEETwoRstrOneSssPortuLicence() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Portuguese Language
        select("SFDC.LanguageEditions.RestrictedLangEd", "label=regexp:Portuguese\\s+");
        waitForPageToLoad("30000");

        //Select Enterprise Edition
        click("EE");
        waitForPageToLoad("30000");

        //Select first and second licence
        click("CrmGenericSssEE");
        click("CrmSFaRstrEE");

        //make quantity of first licence 2
        type("CrmSFaRstrEE_qty", "2");

        //Go to the next tab
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Check if we reach the next tab
        assertTrue(isTextPresent("Additional Products"));

        //Go back to the previou Edition tab and deselect the selected licences
        click("//td[2]/a/span");
        waitForPageToLoad("30000");

        close();
        selectWindow("null");
    }

    public void testClose() throws Exception{

        close();
        stop();
    }
}