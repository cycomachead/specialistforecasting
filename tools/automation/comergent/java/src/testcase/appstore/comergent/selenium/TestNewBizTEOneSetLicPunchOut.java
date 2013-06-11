/** Verifies the punch out works when Group Edition and GE license with One set of users is selected
 * @author hsodha
 */

package testcase.appstore.comergent.selenium;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;

import test.util.SeleniumTest;
import test.util.SeleniumTest.BrowserType;
import test.util.SeleniumTest.TargetBrowsers;
import test.util.GetQuoteForTest;


@TargetBrowsers(BrowserType.CHROME)


public class TestNewBizTEOneSetLicPunchOut extends SeleniumTest{

    public TestNewBizTEOneSetLicPunchOut(String name) {
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


        //Select the Country as Switzerland
        select("SFDC.BasicInfo.BasicInfo.BillCountry.Country", "label=Switzerland");
        waitForPageToLoad("30000");

        //Select the Currency as Euro
        select("SFDC.BasicInfo.BasicInfo.Currency", "label=regexp:EUR - Euro\\s+");
        waitForPageToLoad("30000");

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

                if((allQuote[i].get("usertype").toString().equals("AE")) && (allQuote[i].get("quote").toString().equals("CMGT Automated Test NewBiz GE Selenium"))){

                    username=allQuote[i].get("username").toString();
                    password=allQuote[i].get("password").toString();
                    quote=allQuote[i].get("quote").toString();
                }
            }

        }catch (Exception e){}
    }

    /* Tests to verify if punch out works when Group Edition is selected
     * and License which has One set of users is selected and appropriate
     * selections of additional products is shown up on the Quote Summary
     * and on Order Review page
     */
    //Test to verify if appropriate Currency is getting displayed in the listed prices of the Licenses
    public void dotestCurrOfLic() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Group Edition
        click("TE");
        waitForPageToLoad("30000");

        //Verify the currency of the lsited prices of the license
        assertEquals("Monthly List Price (EUR)", getText("//div[@id='tablechartdefault']/table/tbody/tr[1]/th[5]"));


        close();
        selectWindow("null");
    }

    //Test to verify the currency of the Listed prices of the additional products
    public void dotestCurrOfAdditionalProducts() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Group Edition
        click("TE");
        waitForPageToLoad("30000");

        //Select the License for 1 set of users
        click("CrmSFaQty1TE");

        //Go to additional products page
        click("//li/a/span");
        waitForPageToLoad("30000");


        //Verify the currency of the lsited prices of the Additional products
        assertEquals("Monthly List Price (EUR)", getText("//div[@id='tablechartdefault']/table/tbody/tr[1]/th[5]"));
        assertEquals("One-Time List Price (EUR)", getText("//table[4]/tbody/tr/td/div[@id='contentbox3']/div[@id='tablechartdefault']/table/tbody/tr[1]/th[3]"));

        close();
        selectWindow("null");
    }

    /* Test to verify if the Additional products selected get
     * listed under appropriate sections on the Quote Summary page
     */
    public void dotestPositionOfAdditionalProducts() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Group Edition
        click("TE");
        waitForPageToLoad("30000");

        //Select the License for 1 set of users
        click("CrmSFaQty1TE");

        //Go to additional products page
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select one product from Packaged consulting services
        click("QSImpSuccCoachTE");

        //Select one product from Addition features
        click("Stor500MbTE");

        //Go to Quote Summary page
        click("btm_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify the Position of the Products and license
        assertTrue(isTextPresent("Group Edition featuring Google AdWords"));
        assertTrue(isTextPresent("Additional Carrier-Class MMR Storage (500mb) - Group Edition"));
        assertTrue(isTextPresent("SFA Success Coach"));


        close();
        selectWindow("null");
    }

    /* Test to verify if the Billing Country, Currency, Dates
     * and Term are displayed on the Quote Summary page
     */
    public void dotestBillCouCurrDateTerm() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Group Edition
        click("TE");
        waitForPageToLoad("30000");

        //Select the License for 1 set of users
        click("CrmSFaQty1TE");

        //Go to Quote Summary page
        click("btm_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Verify the Billing Country
        assertEquals("Billing Country", getText("//div[@id='contentbox3']/table/tbody/tr[1]/td[1]"));
        assertEquals("CH", getText("//div[@id='contentbox3']/table/tbody/tr[1]/td[2]"));

        //Verify Currency type
        assertEquals("Currency Type", getText("//div[@id='contentbox3']/table/tbody/tr[2]/td[1]"));
        assertEquals("EUR", getText("//div[@id='contentbox3']/table/tbody/tr[2]/td[2]"));

        //Verify Service Start Date
        assertEquals("Service Start Date", getText("//div[@id='contentbox3']/table/tbody/tr[3]/td[1]"));
        assertEquals(d, getText("//div[@id='contentbox3']/table/tbody/tr[3]/td[2]"));

        //Verify Service Term
        assertEquals("Service Term", getText("//div[@id='contentbox3']/table/tbody/tr[4]/td[1]"));
        assertEquals("12 Months", getText("//div[@id='contentbox3']/table/tbody/tr[4]/td[2]"));

        close();
        selectWindow("null");
    }

    /* Test to verify if the Discount can be given on the License
     * Checking functionality of Reprice button
     */
    public void dotestDiscountOnLic() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Group Edition
        click("TE");
        waitForPageToLoad("30000");

        //Select the License for 1 set of users whose Listed Price is EUR 50.00
        click("CrmSFaQty1TE");

        //Go to Quote Summary page
        click("btm_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        /* Here on Quote Summary page the List price of the selected license is EUR 50.00
         * The Sales Price and total Monthly Price is also  EUR 50.00 and discount is 0%
         * We give a discout of 50% and do Reprice so that Sales Price and total Monthly
         * Prices will be changed to EUR 25.00
         */

        //Change the discount on the License from 0.00% to 50.00%
        type("SfdcSummaryDiscount_100000255", "50");

        //Hit the Reprice button
        click("//tr[3]/td/div/li/a/span");

        //Veriying the changed Sales Price
        assertEquals("25.00000000", getValue("//input[@id='SfdcSummarySaleprice_100000255']"));

        //verifying the changed Total monthly Price
        assertEquals("EUR 25.00", getText("//div[@id='tablechartdefault']/table/tbody/tr[3]/td/span/span"));

        close();
        selectWindow("null");

    }

    //Test to verify if the License and the selected Additional Products get displayed on Order Review page
    public void dotestLicProductsOnOrderReview() throws Exception{

        //Go to the Editions tab
        goEditionTab();

        //Select Group Edition
        click("TE");
        waitForPageToLoad("30000");

        //Select the License for 1 set of users whose Listed Price is EUR 50.00
        click("CrmSFaQty1TE");

        //Go to Additional Products page
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Select some products from Packaged consulting services
        click("QSImpSuccCoachTE");
        click("QSAsstOnsiteTE");

        //Select one product from Addition features
        click("Stor500MbTE");

        //Go to Quote Summary page
        click("btm_javascript:jumpToTab()");
        waitForPageToLoad("30000");

        //Go to subsequent pges after filing up necessary details
        click("//li/a/span");
        waitForPageToLoad("30000");
        click("Acknowledge");
        click("//a/span");
        waitForPageToLoad("30000");

        //Fill address information
        type("SFDC.AddressPage.BillAddr.BillAddr.Street", "one market street");
        type("SFDC.AddressPage.BillAddr.BillAddr.City", "san francisco");
        type("SFDC.AddressPage.BillAddr.BillAddr.State", "ca");
        type("SFDC.AddressPage.BillAddr.BillAddr.Postal", "9410");
        click("CopyBilling");
        waitForPageToLoad("30000");
        type("SFDC.AddressPage.OtherInfo.AddrUEVs2.VATNum", "1234");
        click("//table[3]/tbody/tr/td[1]/div/li/a/span");
        waitForPageToLoad("30000");

        //Fill payment information
        click("CreditCard");
        waitForPageToLoad("30000");
        click("//li/a/span");
        waitForPageToLoad("30000");

        //Now on Order Review page verify if the License and the Products selected are showed up
        assertTrue(isTextPresent("Group Edition featuring Google AdWords"));
        assertTrue(isTextPresent("Additional Carrier-Class MMR Storage (500mb) - Group Edition"));
        assertTrue(isTextPresent("Implementation Success Coach"));
        assertTrue(isTextPresent("QuickStart Assist Onsite"));

        close();
        selectWindow("null");
    }

}