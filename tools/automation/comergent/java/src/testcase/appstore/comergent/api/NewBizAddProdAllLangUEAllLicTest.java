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

public class NewBizAddProdAllLangUEAllLicTest extends BaseTest {

    private static Properties props = null;

    // variable to access any comergentUtility methods
    private static ComergentUtil comergent = null;

    private ComergentTestingUtil utils = new ComergentTestingUtil();

    // validation object that can be tested against comergent
    private SfdcQuoteValidation qval = null;

    // quote Id of the quote with which this test will run
    private static String QUOTEID;

    // filename for the BOM. keep them same as the classname
    private String BOM_FILENAME = Globals.ROOT_DIR+"/xml/NewBizAddProdAllLangUEAllLicTest";

    // static varable to do presetup
    private static boolean flag = true;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private Calendar today = Calendar.getInstance();

    public NewBizAddProdAllLangUEAllLicTest(String name) {
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
        QUOTEID = utils.getQuoteId(props.getProperty("NewBizAddProdAllLangUEAllLicTest.Quote"));

       //QueryResult result = query("Select Name from sfquote__Quote__c where sfquote__Quote_Name__c='"+props.getProperty("NewBizAddProdAllLangUEAllLicTest.Quote")+"'");
        //SObject[] resultRecords = result.getRecords();
        //String quote=resultRecords[0].getField("Name").toString();
        //System.out.println(quote);

        //new CreateBOM().create(quote, props.getProperty("NewBizAddProdAllLangUEAllLicTest.Quote"));
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
    public void testIntegSupLev1UEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("IntegSupLev1UE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for Custom Integration Support Level 1 (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }

    }

    @TestLabels("extended")
    public void testIntegSupLev1UEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("IntegSupLev1UE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }

    }

    @TestLabels("extended")
    public void testIntegSupLev1UEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("IntegSupLev1UE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }

    }

    @TestLabels("extended")
    public void testIntegSupLev1UElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("IntegSupLev1UE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testIntegSupLev1UEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("IntegSupLev1UE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for Custom Integration Support Level 1 (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }

    }

    @TestLabels("extended")
    public void testIntegSupLev1UEgrtEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("IntegSupLev1UE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for Custom Integration Support Level 1 (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }

    }

    @TestLabels("extended")
    public void testIntegSupLev2UEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("IntegSupLev2UE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for Custom Integration Support Level 2 (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }

    }

    @TestLabels("extended")
    public void testIntegSupLev2UEtodayDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("IntegSupLev2UE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }

    }

    @TestLabels("extended")
    public void testIntegSupLev2UEnextDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("IntegSupLev2UE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testIntegSupLev2UElessEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("IntegSupLev2UE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testIntegSupLev2UEonEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("IntegSupLev2UE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for Custom Integration Support Level 2 (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testIntegSupLev2UEgrtEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("IntegSupLev2UE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for Custom Integration Support Level 2 (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testIntegSupLev3UEprevDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("IntegSupLev3UE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for Custom Integration Support Level 3 (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testIntegSupLev3UEtodayDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("IntegSupLev3UE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testIntegSupLev3UEnextDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("IntegSupLev3UE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testIntegSupLev3UElessEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("IntegSupLev3UE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testIntegSupLev3UEonEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("IntegSupLev3UE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for Custom Integration Support Level 3 (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testIntegSupLev3UEgrtEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("IntegSupLev3UE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for Custom Integration Support Level 3 (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testAdminWksUEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdminWksUE", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAdminWksUEOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdminWksUE", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAdminWksUEOutOfRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdminWksUE", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAdmBtcmpUEMISSINGInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdmBtcmpUEMISSING", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAdmBtcmpUEMISSINGOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdmBtcmpUEMISSING", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAdmBtcmpUEMISSINGOutOfRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdmBtcmpUEMISSING", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testNewFeatWksUEInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksUE", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testNewFeatWksUEOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksUE", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testNewFeatWksUEGrtRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksUE", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testPrtnrAdmWksUEInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("PrtnrAdmWksUE", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testPrtnrAdmWksUEOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("PrtnrAdmWksUE", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testPrtnrAdmWksUEGrtRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("PrtnrAdmWksUE", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testNewFeatWksClsrmUEInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksClsrmUE", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testNewFeatWksClsrmUEOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksClsrmUE", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testNewFeatWksClsrmUEGrtRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksClsrmUE", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSCntrlWksUEInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("SCntrlWksUE", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSCntrlWksUEOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("SCntrlWksUE", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testSCntrlWksUEGrtRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("SCntrlWksUE", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXAPIWksUEInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXAPIWksUE", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXAPIWksUEOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXAPIWksUE", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXAPIWksUEGrtRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXAPIWksUE", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXDtaMigrWksPRInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXDtaMigrWksPR", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXDtaMigrWksPROnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXDtaMigrWksPR", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXDtaMigrWksPRGrtRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXDtaMigrWksPR", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXLabWksUEInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXLabWksUE", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXLabWksUEOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXLabWksUE", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXLabWksUEGrtRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXLabWksUE", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXMobWksUEInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXMobWksUE", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXMobWksUEOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXMobWksUE", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testAppXMobWksUEGrtRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXMobWksUE", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbUEprevDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor500MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (500mb) - Unlimited Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbUEtodayDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbUEnextDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("Stor500MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbUElessEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("Stor500MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbUEonEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor500MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (500mb) - Unlimited Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbUEgrtEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("Stor500MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (500mb) - Unlimited Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbUEprevDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor50MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (50mb) - Unlimited Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbUEtodayDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbUEnextDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("Stor50MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbUElessEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("Stor50MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbUEonEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor50MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (50mb) - Unlimited Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbUEgrtEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("Stor50MbUE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (50mb) - Unlimited Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPUEprevDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("ConSAPUE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for ConnectSAP - UE (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPUEtodayDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("ConSAPUE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPUEnextDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("ConSAPUE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPUElessEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("ConSAPUE", "uevValue", dateFormat.format(today.getTime()));

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPUEonEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("ConSAPUE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for ConnectSAP - UE (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPUEgrtEndDate() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("ConSAPUE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for ConnectSAP - UE (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbUEInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbUE", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbUEOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbUE", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbUEOutOfRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbUE", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbUEInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbUE", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbUEOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbUE", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbUEOutOfRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbUE", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPUEInRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("ConSAPUE", "Quantity", "49");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPUEOnRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("ConSAPUE", "Quantity", "50");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPUEOutOfRange() throws Exception {
        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("ConSAPUE", "Quantity", "51");

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
                System.out.println(msg.getMsgValue());
            }
        }
    }

    @Override
    public void ftestTearDown() {
        // attach superbom
        utils.setSuperBOM(QUOTEID, BOM_FILENAME);
    }

}
