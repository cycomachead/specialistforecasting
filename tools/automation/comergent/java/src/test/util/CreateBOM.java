package test.util;


import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.thoughtworks.selenium.*;


/** Creates the BOM and attaches it to the required Quote
 * @author hsodha
 */

public class CreateBOM {

    private static HashMap[] allBom=null;
    private static boolean flag=false;

//	call the default selenium and pass URL which is required to be opened
    static Selenium selenium = new DefaultSelenium("localhost", 4444, "*chrome", "http://na1-aps2.soma.salesforce.com/");

    public void doLogin(){

        selenium.start();
        selenium.open("/login.jsp/?un=ae.cs.demo@salesforce.com&pw=123456");
        selenium.waitForPageToLoad("30000");
        selenium.click("link=Quotes");
        selenium.waitForPageToLoad("30000");
    }

    //Searches the required quote
    public void searchQuote(HashMap bom, String quote)throws Exception{


        //Enter the Quote which you want to attach the BOM file to
        selenium.type("sbstr", quote);

        //Search the quote
        selenium.click("search");
        selenium.waitForPageToLoad("30000");


        selenium.click("link="+quote);
        selenium.waitForPageToLoad("60000");

        //Launch the configurator
        selenium.click("cmgt_configure_quote_dummy");
        selenium.waitForPopUp("Configurator", "30000");

        //Shift the control to the new popped up window
        selenium.selectWindow("Configurator");

    }

    //Selects licences and thier quantities
    public void selectLicence(HashMap bom)throws Exception{

        //Select the licences
        selenium.click(bom.get("licence1").toString());

        if(bom.get("edition").toString().equals("EE")||bom.get("edition").toString().equals("UE")||bom.get("edition").toString().equals("PE")){

            selenium.click(bom.get("licence2").toString());
            selenium.click(bom.get("licence3").toString());


            if(bom.get("edition").toString().equals("EE")||bom.get("edition").toString().equals("UE")){

                    selenium.click(bom.get("licence4").toString());
                    selenium.click(bom.get("licence5").toString());

                    if(bom.get("edition").toString().equals("EE")){

                        selenium.click(bom.get("licence6").toString());
                        selenium.click(bom.get("licence7").toString());
                        selenium.click(bom.get("licence8").toString());
                        selenium.click(bom.get("licence9").toString());
                        //selenium.click(bom.get("licence10").toString());
                    }

            }
        }

        if(bom.get("edition").toString().equals("PE")||bom.get("edition").toString().equals("EE")||bom.get("edition").toString().equals("UE")){

            selenium.type(bom.get("licence1").toString()+"_qty", bom.get("licenceqty1").toString());

            if(bom.get("edition").toString().equals("EE")||bom.get("edition").toString().equals("UE")){

                selenium.type(bom.get("licence2").toString()+"_qty", bom.get("licenceqty2").toString());
                selenium.type(bom.get("licence3").toString()+"_qty", bom.get("licenceqty3").toString());
                selenium.type(bom.get("licence4").toString()+"_qty", bom.get("licenceqty4").toString());
                selenium.type(bom.get("licence5").toString()+"_qty", bom.get("licenceqty5").toString());
                //selenium.type(bom.get("licence6").toString()+"_qty", bom.get("licenceqty6").toString());
            }
        }
    }

    //Select the additional products
    public void additionalProducts(HashMap bom)throws Exception{

//		Select Additional Products
        selenium.click(bom.get("product1").toString());
        selenium.click(bom.get("product2").toString());
        selenium.click(bom.get("product3").toString());
        selenium.click(bom.get("product4").toString());
        selenium.click(bom.get("product5").toString());
        selenium.click(bom.get("product6").toString());
        selenium.click(bom.get("product7").toString());
        selenium.click(bom.get("product8").toString());
        selenium.click(bom.get("product9").toString());

        if(bom.get("edition").toString().equals("PE")||bom.get("edition").toString().equals("EE")||bom.get("edition").toString().equals("UE")){

            selenium.click(bom.get("product10").toString());
            selenium.click(bom.get("product11").toString());
            selenium.click(bom.get("product12").toString());
            selenium.click(bom.get("product13").toString());

                    if(bom.get("edition").toString().equals("PE")||bom.get("edition").toString().equals("UE")){

                        selenium.click(bom.get("product14").toString());
                        selenium.click(bom.get("product15").toString());

                            if(bom.get("edition").toString().equals("PE")){

                                selenium.click(bom.get("product16").toString());
                                selenium.click(bom.get("product17").toString());
                                selenium.click(bom.get("product18").toString());
                                selenium.click(bom.get("product19").toString());
                                selenium.click(bom.get("product20").toString());
                                selenium.click(bom.get("product21").toString());
                                selenium.click(bom.get("product22").toString());
                                selenium.click(bom.get("product23").toString());
                            }
                    }

            }
    }

    //Travel from first tab to last one
    public void travelTab(HashMap bom) throws Exception{

        //Go to the Editions tab
        selenium.click("//a/span");
        selenium.waitForPageToLoad("30000");

        //Select Edition
        selenium.click(bom.get("edition").toString());
        selenium.waitForPageToLoad("30000");

        //Selects licence and their quantities
        selectLicence(bom);

        //Go to the Additional products tab
        selenium.click("//li/a/span");
        selenium.waitForPageToLoad("30000");

        //Select the additional products
        additionalProducts(bom);

        //Go to Quote Summary tab
        selenium.click("top_javascript:jumpToTab()");
        selenium.waitForPageToLoad("30000");

        //Go to Check out tab
        selenium.click("//li/a/span");
        selenium.waitForPageToLoad("30000");

        if(bom.get("quote").equals("CMGT Automated Test NewBiz TE")){

            //Features to be deleted
            selenium.click("Acknowledge");
            selenium.click("//li/a/span");
            selenium.waitForPageToLoad("30000");
        }

        //Fill the addresss information
        selenium.type("SFDC.AddressPage.BillAddr.BillAddr.Street", bom.get("street").toString());
        selenium.select("SFDC.AddressPage.BillAddr.BillAddr.State", "label="+bom.get("state").toString());
        selenium.type("SFDC.AddressPage.BillAddr.BillAddr.City", bom.get("city").toString());
        selenium.type("SFDC.AddressPage.BillAddr.BillAddr.Postal", bom.get("postal").toString());
        selenium.click(bom.get("copy").toString());
        selenium.waitForPageToLoad("30000");


        //Go to Payment information tab
        selenium.click("//li/a/span");
        selenium.waitForPageToLoad("30000");


        //Payment Method
        selenium.click(bom.get("paymethod").toString());
        selenium.waitForPageToLoad("30000");

        //Payment Terms
        selenium.click(bom.get("payterms").toString());
        selenium.waitForPageToLoad("30000");

        //Go to Order review
        selenium.click("//li/a/span");
        selenium.waitForPageToLoad("30000");

        //Place the order which will attach the BOM to the quote
        selenium.click("//li/a/span");
        selenium.waitForPageToLoad("30000");

    }

    //Attaches the BOM to the quote
    public void attachBOM(HashMap bom, String quote)throws Exception{

        //Search for the quote to which you want to attach the BOM
        searchQuote(bom, quote);

        //Enter current date as contract start date on the basic Information tab
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("MM/dd/yyyy");
        Date today = new Date();
        String d = simpleFormatter.format (today);

        selenium.type("ContractStartDate", d);
        selenium.waitForPageToLoad("30000");

        selenium.type("QuoteValidDate", d);
        selenium.waitForPageToLoad("30000");

        //Travel from first tab to last
        travelTab(bom);
    }

    public void create(String quote, String qname)throws Exception{


        //This is to get the data from Bom.xml only once
        if(!flag){

            allBom = new PickBOM().createAllBoms();
            flag=true;

            }

            doLogin();
        //Function to attach the BOM to quotes
            for(int i = 0; i<allBom.length;i++){

                if(allBom[i].get("quote").toString().equals(qname)){

                    attachBOM(allBom[i], quote);
                    selenium.selectWindow("null");
                }
            }
            selenium.stop();
    }
}