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


public class NewBizAddProdAllLangGELicTest extends BaseTest{

    private static Properties props = null;

    // variable to access any comergentUtility methods
    private static ComergentUtil comergent = null;

    private ComergentTestingUtil utils= new ComergentTestingUtil();

    // validation object that can be tested against comergent
    private SfdcQuoteValidation qval = null;

    // quote Id of the quote with which this test will run
    private static String QUOTEID;

    // filename for the BOM. keep them same as the classname
    private String BOM_FILENAME = Globals.ROOT_DIR+"/xml/NewBizAddProdAllLangGEAllLicTest";

    // static varable to do presetup
    private static boolean flag = true;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private Calendar today = Calendar.getInstance();


    public NewBizAddProdAllLangGELicTest(String name) {
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

        QUOTEID = utils.getQuoteId(props.getProperty("NewBizAddProdAllLangGELicTest.Quote"));

        //QueryResult result = query("Select Name from sfquote__Quote__c where sfquote__Quote_Name__c='"+props.getProperty("NewBizAddProdAllLangGELicTest.Quote")+"'");
        //SObject[] resultRecords = result.getRecords();
        //String quote=resultRecords[0].getField("Name").toString();
        //System.out.println(quote);

        //new CreateBOM().create(quote, props.getProperty("NewBizAddProdAllLangGELicTest.Quote"));
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
    public void testStor500MbTEprevDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            today.add(Calendar.DATE, -1);
            utils.changeAttribute("Stor500MbTE", "uevValue", dateFormat.format(today.getTime()));

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
                    if (msg
                            .getMsgValue()
                            .contains(
                                    "Item start date for Additional Carrier-Class MMR Storage (500mb) - Group Edition (")
                            && msg.getMsgValue().contains(") must occur on or after contract start (")
                            && msg.getMsgValue().contains(") and before contract end date (")) error = true;
                }
                if (!error) {
                    fail("Error msg not correct");
                }
            }
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor500MbTEtodayDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            utils.changeAttribute("Stor500MbTE", "uevValue", dateFormat.format(today.getTime()));

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor500MbTEnextDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            today.add(Calendar.DATE, 1);
            utils.changeAttribute("Stor500MbTE", "uevValue", dateFormat.format(today.getTime()));

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor500MbTElessEndDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            today.add(Calendar.YEAR, 1);
            today.add(Calendar.DATE, -2);
            utils.changeAttribute("Stor500MbTE", "uevValue", dateFormat.format(today.getTime()));

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor500MbTEonEndDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            today.add(Calendar.YEAR, 1);
            today.add(Calendar.DATE, -1);
            utils.changeAttribute("Stor500MbTE", "uevValue", dateFormat.format(today.getTime()));

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
                    if (msg
                            .getMsgValue()
                            .contains(
                                    "Item start date for Additional Carrier-Class MMR Storage (500mb) - Group Edition (")
                            && msg.getMsgValue().contains(") must occur on or after contract start (")
                            && msg.getMsgValue().contains(") and before contract end date (")) error = true;
                }
                if (!error) {
                    fail("Error msg not correct");
                }
            }
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor500MbTEgrtEndDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            today.add(Calendar.YEAR, 1);
            utils.changeAttribute("Stor500MbTE", "uevValue", dateFormat.format(today.getTime()));

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
                    if (msg
                            .getMsgValue()
                            .contains(
                                    "Item start date for Additional Carrier-Class MMR Storage (500mb) - Group Edition (")
                            && msg.getMsgValue().contains(") must occur on or after contract start (")
                            && msg.getMsgValue().contains(") and before contract end date (")) error = true;
                }
                if (!error) {
                    fail("Error msg not correct");
                }
            }
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor50MbTEprevDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            today.add(Calendar.DATE, -1);
            utils.changeAttribute("Stor50MbTE", "uevValue", dateFormat.format(today.getTime()));

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
                            "Item start date for Additional Carrier-Class MMR Storage (50mb) - Group Edition (")
                            && msg.getMsgValue().contains(") must occur on or after contract start (")
                            && msg.getMsgValue().contains(") and before contract end date (")) error = true;
                }
                if (!error) {
                    fail("Error msg not correct");
                }
            }
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor50MbTEtodayDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            utils.changeAttribute("Stor50MbTE", "uevValue", dateFormat.format(today.getTime()));

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor50MbTEnextDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            today.add(Calendar.DATE, 1);
            utils.changeAttribute("Stor50MbTE", "uevValue", dateFormat.format(today.getTime()));

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor50MbTElessEndDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            today.add(Calendar.YEAR, 1);
            today.add(Calendar.DATE, -2);
            utils.changeAttribute("Stor50MbTE", "uevValue", dateFormat.format(today.getTime()));

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor50MbTEonEndDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            today.add(Calendar.YEAR, 1);
            today.add(Calendar.DATE, -1);
            utils.changeAttribute("Stor50MbTE", "uevValue", dateFormat.format(today.getTime()));

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
                            "Item start date for Additional Carrier-Class MMR Storage (50mb) - Group Edition (")
                            && msg.getMsgValue().contains(") must occur on or after contract start (")
                            && msg.getMsgValue().contains(") and before contract end date (")) error = true;
                }
                if (!error) {
                    fail("Error msg not correct");
                }
            }
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor50MbTEgrtEndDate() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            today.add(Calendar.YEAR, 1);
            utils.changeAttribute("Stor50MbTE", "uevValue", dateFormat.format(today.getTime()));

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
                            "Item start date for Additional Carrier-Class MMR Storage (50mb) - Group Edition (")
                            && msg.getMsgValue().contains(") must occur on or after contract start (")
                            && msg.getMsgValue().contains(") and before contract end date (")) error = true;
                }
                if (!error) {
                    fail("Error msg not correct");
                }
            }
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }
    }

    @TestLabels("extended")
    public void testStor500MbTEInRange() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            utils.changeAttribute("Stor500MbTE", "Quantity", "24");

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }

    }

    @TestLabels("extended")
    public void testStor500MbTEOnRange() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            utils.changeAttribute("Stor500MbTE", "Quantity", "25");

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }

    }

    @TestLabels("extended")
    public void testStor500MbTEOutOfRange() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            utils.changeAttribute("Stor500MbTE", "Quantity", "26");

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }

    }

    @TestLabels("extended")
    public void testStor50MbTEInRange() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            utils.changeAttribute("Stor50MbTE", "Quantity", "24");

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }

    }

    @TestLabels("extended")
    public void testStor50MbTEOnRange() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            utils.changeAttribute("Stor50MbTE", "Quantity", "25");

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }

    }

    @TestLabels("extended")
    public void testStor50MbTEOutOfRange() {
        try {
            // store the bom.xml file into the directory
            utils.getBOM(QUOTEID);

            utils.changeAttribute("Stor50MbTE", "Quantity", "26");

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
        } catch (Exception e) {
            fail("Throwing exception " + e.getMessage());
        }

    }

    @Override
    public void ftestTearDown() {
        // attach superbom
        utils.setSuperBOM(QUOTEID, BOM_FILENAME);
    }


}
