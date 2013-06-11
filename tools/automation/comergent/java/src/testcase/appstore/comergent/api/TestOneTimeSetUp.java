package testcase.appstore.comergent.api;

import java.util.Properties;

import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;

import test.util.BaseTest;
import test.util.ComergentTestingUtil;
import test.util.CreateBOM;
import test.util.LinkOrg;

/** To create the initial test data for comergent tests */

public class TestOneTimeSetUp extends BaseTest {

    private static Properties props = null;

    public TestOneTimeSetUp(String name){
        super(name);
    }


    private ComergentTestingUtil utils = new ComergentTestingUtil();


    public void attachBom() throws Exception{

        props = utils.loadProperties();
        /* Attach the BOM to the quotes */

        //Get the Quote Number
        QueryResult result = query("Select Name from sfquote__Quote__c where sfquote__Quote_Name__c='"+props.getProperty("NewBizAddProdAllLangEEAllLicTest.Quote")+"'");
        SObject[] resultRecords = result.getRecords();
        String quote1=resultRecords[0].getField("Name").toString();
        System.out.println(quote1);

        //Attach the BOM
        new CreateBOM().create(quote1, props.getProperty("NewBizAddProdAllLangEEAllLicTest.Quote"));




        //Get the Quote Number
        result = query("Select Name from sfquote__Quote__c where sfquote__Quote_Name__c='"+props.getProperty("NewBizAddProdAllLangGELicTest.Quote")+"'");
        resultRecords = result.getRecords();
        String quote2=resultRecords[0].getField("Name").toString();
        System.out.println(quote2);

        //Attach the BOM
        new CreateBOM().create(quote2, props.getProperty("NewBizAddProdAllLangGELicTest.Quote"));




        //Get the Quote Number
        result = query("Select Name from sfquote__Quote__c where sfquote__Quote_Name__c='"+props.getProperty("NewBizAddProdAllLangPESFATest.Quote")+"'");
        resultRecords = result.getRecords();
        String quote3=resultRecords[0].getField("Name").toString();
        System.out.println(quote3);

        //Attach the BOM
        new CreateBOM().create(quote3, props.getProperty("NewBizAddProdAllLangPESFATest.Quote"));




        //Get the Quote Number
        result = query("Select Name from sfquote__Quote__c where sfquote__Quote_Name__c='"+props.getProperty("NewBizAddProdAllLangUEAllLicTest.Quote")+"'");
        resultRecords = result.getRecords();
        String quote4=resultRecords[0].getField("Name").toString();
        System.out.println(quote4);

        //Attach the BOM
        new CreateBOM().create(quote4, props.getProperty("NewBizAddProdAllLangUEAllLicTest.Quote"));

    }


    public void testSetUp() throws Exception {

        utils.doLogin();
        new TestCaseInitialSetUp().doInitialize();
        new LinkOrg().linkAllOrgs();
        attachBom();


    }

}
