package testcase.appstore.comergent.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import test.runner.Globals;
import test.runner.TestLabels;
import test.util.BaseTest;
import test.util.ComergentTestingUtil;
import test.util.ComergentUtil;
//import test.util.CreateBOM;

import com.comergent.www.webservices.SfdcMessageResults;
import com.comergent.www.webservices.SfdcMessages;
import com.comergent.www.webservices.SfdcQuoteValidatedResults;
import com.comergent.www.webservices.SfdcQuoteValidation;
//import com.sforce.soap.partner.QueryResult;
//import com.sforce.soap.partner.sobject.SObject;

/**
 * Contains test cases against comergent or quote:
 *
 * @author dgupta
 *
 */
public class NewBizAddProdAllLangPESFATest extends BaseTest {

    private static Properties props = null;

    // variable to access any comergentUtility methods
    private static ComergentUtil comergent = null;

    private ComergentTestingUtil utils = new ComergentTestingUtil();

    // validation object that can be tested against comergent
    private SfdcQuoteValidation qval = null;

    // quote Id of the quote with which this test will run
    private static String QUOTEID;

    // filename for the BOM. keep them same as the classname
    private String BOM_FILENAME = Globals.ROOT_DIR+"/xml/NewBizAddProdAllLangPEAllLicTest";

    // static varable to do presetup
    private static boolean flag = true;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private Calendar today = Calendar.getInstance();

    public NewBizAddProdAllLangPESFATest(String name) {
        super(name);
    }

    /**
     * Initial setUp to to create one sforce Stub and one comergent stub.
     *
     * @throws Exception
     */

    public void oneTimeSetUp() throws Exception {
        utils.doLogin();
        //new TestCaseInitialSetUp().doInitialize();
        //new LinkOrg().linkAllOrgs();
        comergent = new ComergentUtil();
        props = utils.loadProperties();
        QUOTEID = utils.getQuoteId(props.getProperty("NewBizAddProdAllLangPESFATest.Quote"));

        //QueryResult result = query("Select Name from sfquote__Quote__c where sfquote__Quote_Name__c='"+props.getProperty("NewBizAddProdAllLangPESFATest.Quote")+"'");
        //SObject[] resultRecords = result.getRecords();
        //String quote=resultRecords[0].getField("Name").toString();
        //System.out.println(quote);

        //new CreateBOM().create(quote, props.getProperty("NewBizAddProdAllLangPESFATest.Quote"));
        utils.getSuperBOM(QUOTEID, BOM_FILENAME);
    }

    /**
     * Sets the validation object with Quote and sessionID. Attaches super_bom
     * to the quote. Called before every test.
     */
    @Override
    public void ftestSetUp() throws Exception {
        if (flag) {
            oneTimeSetUp();
            flag = false;
        }
        // set Comergent headers for this request
        comergent.setHeaders();
        qval = new SfdcQuoteValidation();
        // attach quote
        qval.setQuoteId(QUOTEID);
        // attach session
        qval.setSfdcSessionId(utils.getSessionId());
        // set flow
        qval.setFlow("testFlow");
    }

    @TestLabels("extended")
    public void testAdminWksPEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdminWksPE", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAdminWksPEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdminWksPE", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAdminWksPEOutOfRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdminWksPE", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testNewFeatWksPEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksPE", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }

    }

    @TestLabels("extended")
    public void testNewFeatWksPEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksPE", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testNewFeatWksPEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksPE", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testPrtnrAdmWksPEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("PrtnrAdmWksPE", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testPrtnrAdmWksPEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("PrtnrAdmWksPE", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testPrtnrAdmWksPEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("PrtnrAdmWksPE", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testNewFeatWksClsrmPEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksClsrmPE", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testNewFeatWksClsrmPEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksClsrmPE", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testNewFeatWksClsrmPEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksClsrmPE", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSCntrlWksPEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("SCntrlWksPE", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSCntrlWksPEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("SCntrlWksPE", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSCntrlWksPEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("SCntrlWksPE", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXAPIWksPEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXAPIWksPE", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXAPIWksPEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXAPIWksPE", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXAPIWksPEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXAPIWksPE", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXDtaMigrWksPRInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXDtaMigrWksPR", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXDtaMigrWksPROnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXDtaMigrWksPR", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXDtaMigrWksPRGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXDtaMigrWksPR", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXLabWksPEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXLabWksPE", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXLabWksPEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXLabWksPE", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXLabWksPEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXLabWksPE", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXMobWksPEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXMobWksPE", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXMobWksPEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXMobWksPE", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXMobWksPEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXMobWksPE", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSFMktgPEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("SFMktgPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains(
                        "Item start date for Salesforce Marketing (formerly Campaign Tracking) (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testSFMktgPEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("SFMktgPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSFMktgPEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("SFMktgPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSFMktgPElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("SFMktgPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSFMktgPEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("SFMktgPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains(
                        "Item start date for Salesforce Marketing (formerly Campaign Tracking) (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testSFMktgPEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("SFMktgPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains(
                        "Item start date for Salesforce Marketing (formerly Campaign Tracking) (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testPrdSchdPEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("PrdSchdPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Products and Scheduling (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testPrdSchdPEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("PrdSchdPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testPrdSchdPEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("PrdSchdPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testPrdSchdPElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("PrdSchdPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testPrdSchdPEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("PrdSchdPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Products and Scheduling (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testPrdSchdPEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("PrdSchdPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Products and Scheduling (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testDeptPgLtPEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("DeptPgLtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Departmental Page Layouts (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testDeptPgLtPEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("DeptPgLtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testDeptPgLtPEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("DeptPgLtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testDeptPgLtPElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("DeptPgLtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testDeptPgLtPEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("DeptPgLtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Departmental Page Layouts (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testDeptPgLtPEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("DeptPgLtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Departmental Page Layouts (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }

    }

    @TestLabels("extended")
    public void testApiPEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("ApiPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for API (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testApiPEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("ApiPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void dotestApiPEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("ApiPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testApiPElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("ApiPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testApiPEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("ApiPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for API (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testApiPEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("ApiPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for API (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testOfflinePEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("OfflinePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Offline Edition - Professional Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testOfflinePEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("OfflinePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testOfflinePEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("OfflinePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testOfflinePElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("OfflinePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testOfflinePEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("OfflinePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Offline Edition - Professional Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testOfflinePEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("OfflinePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Offline Edition - Professional Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testAppExchMobilePEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("AppExchMobilePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for AppExchange Mobile (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testAppExchMobilePEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppExchMobilePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppExchMobilePEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("AppExchMobilePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppExchMobilePElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("AppExchMobilePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppExchMobilePEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("AppExchMobilePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for AppExchange Mobile (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testAppExchMobilePEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("AppExchMobilePE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for AppExchange Mobile (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testWklyExprtPEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("WklyExprtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Weekly Export Service (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testWklyExprtPEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("WklyExprtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testWklyExprtPEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("WklyExprtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testWklyExprtPElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("WklyExprtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testWklyExprtPEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("WklyExprtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Weekly Export Service (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testWklyExprtPEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("WklyExprtPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains("Item start date for Weekly Export Service (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbPEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor500MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains(
                        "Item start date for Additional Carrier-Class MMR Storage (500mb) - Professional Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbPEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbPEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("Stor500MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbPElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("Stor500MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbPEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor500MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains(
                        "Item start date for Additional Carrier-Class MMR Storage (500mb) - Professional Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbPEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("Stor500MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains(
                        "Item start date for Additional Carrier-Class MMR Storage (500mb) - Professional Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbPEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor50MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains(
                        "Item start date for Additional Carrier-Class MMR Storage (50mb) - Professional Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbPEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbPEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("Stor50MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbPElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("Stor50MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbPEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor50MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains(
                        "Item start date for Additional Carrier-Class MMR Storage (50mb) - Professional Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbPEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("Stor50MbPE", "uevValue", dateFormat.format(today.getTime()));

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains(
                        "Item start date for Additional Carrier-Class MMR Storage (50mb) - Professional Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testSfaAppExchMobilePEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppExchMobilePE", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSfaAppExchMobilePEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppExchMobilePE", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSfaAppExchMobilePEOutOfRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppExchMobilePE", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        if (results.isHasError()) {
            boolean error = false;
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                if (msg.getMsgValue().contains(
                        "Seat quantity for AppExchange Mobile cannot exceed platform seat quantity ("))
                    error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testSfaSssAppExchMobilePEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("CrmGenericSfaPE", "Quantity", "9");
        utils.changeAttribute("CrmGenericSssPE", "Quantity", "8");
        utils.changeAttribute("AppExchMobilePE", "Quantity", "17");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
        //Setting the quantities of Sfa and Sss back to what they were
        utils.changeAttribute("CrmGenericSfaPE", "Quantity", "23");
        utils.changeAttribute("CrmGenericSssPE", "Quantity", "1");
    }

    @TestLabels("extended")
    public void testSfaSssAppExchMobilePEOutOfRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("CrmGenericSfaPE", "Quantity", "9");
        utils.changeAttribute("CrmGenericSssPE", "Quantity", "8");
        utils.changeAttribute("AppExchMobilePE", "Quantity", "19");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
        // errors
        for (int i = 0; msgs.length > i; i++) {
            SfdcMessages msg = msgs[i];
            System.out.println(msg.getMsgValue());
        }
        //Setting the quantities of Sfa and Sss back to what they were
        utils.changeAttribute("CrmGenericSfaPE", "Quantity", "23");
        utils.changeAttribute("CrmGenericSssPE", "Quantity", "1");
    }

    @TestLabels("extended")
    public void testSfaSssRstrAppExchMobilePEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("CrmGenericSfaPE", "Quantity", "8");
        utils.changeAttribute("CrmGenericSssPE", "Quantity", "10");
        utils.changeAttribute("CrmSFaRstrPE", "Quantity", "2");
        utils.changeAttribute("AppExchMobilePE", "Quantity", "19");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
        //Setting the quantities of Sfa, Sss, and Rstr back to what they were
        utils.changeAttribute("CrmGenericSfaPE", "Quantity", "23");
        utils.changeAttribute("CrmGenericSssPE", "Quantity", "1");
        utils.changeAttribute("CrmSFaRstrPE", "Quantity", "1");
    }

    @TestLabels("extended")
    public void testSfaSssRstrAppExchMobilePEOutOfRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("CrmGenericSfaPE", "Quantity", "8");
        utils.changeAttribute("CrmGenericSssPE", "Quantity", "10");
        utils.changeAttribute("CrmSFaRstrPE", "Quantity", "2");
        utils.changeAttribute("AppExchMobilePE", "Quantity", "21");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertTrue("Does not have errors", results.isHasError());

        SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
        // errors
        for (int i = 0; msgs.length > i; i++) {
            SfdcMessages msg = msgs[i];
            System.out.println(msg.getMsgValue());
        }
        //Setting the quantities of Sfa, Sss, and Rstr back to what they were
        utils.changeAttribute("CrmGenericSfaPE", "Quantity", "23");
        utils.changeAttribute("CrmGenericSssPE", "Quantity", "1");
        utils.changeAttribute("CrmSFaRstrPE", "Quantity", "1");
    }

    @TestLabels("extended")
    public void testStor500MbPEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbPE", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbPEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbPE", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbPEOutOfRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbPE", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbPEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbPE", "Quantity", "24");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbPEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbPE", "Quantity", "25");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbPEOutOfRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbPE", "Quantity", "26");

        // update the bom.xml and attach to the quote
        utils.setBOM(QUOTEID);
        long t1 = System.currentTimeMillis();
        // send validation request
        SfdcMessageResults result = comergent.validate(qval);
        long t2 = System.currentTimeMillis();
        System.out.println("TimeTaken for the comergent API to validate :" + (t2 - t1));
        // get results
        SfdcQuoteValidatedResults results = result.getSfdcQuoteValidatedResults();

        assertFalse("Incorrectly has errors", results.isHasError());

        if (results.isHasError()) {
            SfdcMessages[] msgs = results.getSfdcMessagesList().getSfdcMessages(); // print
            // errors
            for (int i = 0; msgs.length > i; i++) {
                SfdcMessages msg = msgs[i];
                fail(msg.getMsgValue());
            }
        }
    }

    @Override
    public void ftestTearDown() throws Exception {
        // attach superbom
        utils.setSuperBOM(QUOTEID, BOM_FILENAME);
    }

}
