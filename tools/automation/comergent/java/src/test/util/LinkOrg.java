package test.util;

/**Links Orgs to the required account
 * @author hsodha
 */

//TODO: can also be done through API

import com.thoughtworks.selenium.*;
import java.util.HashMap;

public class LinkOrg {

    //call the default selenium and pass URL which is required to be opened
    static Selenium selenium = new DefaultSelenium("localhost", 4444, "*chrome", "http://www-aps2.soma.salesforce.com");

    //Do login as admin
    public  void doLogin() throws Exception{

        //open the relative URL to the URL which we had passed to the default selenium constructor
        selenium.start();
        selenium.open("/login.jsp/?un=hsodha@salesforce.com&pw=123456");
        selenium.waitForPageToLoad("30000");
    }


    //Go to the SysAdmin Tab
    public  void goSysAdminTab() throws Exception{

        //Click the SysAdmin Tab
        selenium.click("link=exact:SysAdmin: NA1");
        selenium.waitForPageToLoad("30000");
    }

    /*Searches for the Org which is to be linked to the account.
     * The org and account information is stored in Org.xml
     */
    public  void searchOrg(HashMap org) throws Exception{

        //Search for the Org by the username
        selenium.type("//input[@id='search']",org.get("username").toString());
        selenium.click("//form[@id='adminsearchform']/input[2]");
        selenium.waitForPageToLoad("120000");

        //Select the Org
        selenium.click("link=comergent");
        selenium.waitForPageToLoad("30000");
    }


    public  void linkOrgAccount(HashMap org)throws Exception{

        goSysAdminTab();

        //Search for the org to be linked
        searchOrg(org);

        //Linking the org to an account
        //Click to choose the account

        selenium.click("link=[Choose]");
        selenium.waitForPageToLoad("30000");

        //Search for the account by its name

        selenium.type("search", org.get("account").toString());
        selenium.click("document.accountsearch.search[1]");
        selenium.waitForPageToLoad("30000");

        //Link it to the Org

        selenium.click("link=Select");
        selenium.waitForPageToLoad("30000");

    }

    public void linkAllOrgs()throws Exception{

        HashMap[] allOrg = new PickOrg().createAllOrgs();
        doLogin();

        for(int i = 0; i<allOrg.length;i++){
            linkOrgAccount(allOrg[i]);
        }
        selenium.stop();
    }

}
