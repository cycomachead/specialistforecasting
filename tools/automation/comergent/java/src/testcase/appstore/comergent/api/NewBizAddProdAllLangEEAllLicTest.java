package testcase.appstore.comergent.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import test.runner.Globals;
import test.runner.TestLabels;
import test.util.BaseTest;
import test.util.ComergentTestingUtil;
import test.util.ComergentUtil;

import com.comergent.www.webservices.SfdcMessageResults;
import com.comergent.www.webservices.SfdcMessages;
import com.comergent.www.webservices.SfdcQuoteValidatedResults;
import com.comergent.www.webservices.SfdcQuoteValidation;

public class NewBizAddProdAllLangEEAllLicTest extends BaseTest {

    private static Properties props = null;

    // variable to access any comergentUtility methods
    private static ComergentUtil comergent = null;

    private ComergentTestingUtil utils = new ComergentTestingUtil();

    // validation object that can be tested against comergent
    private SfdcQuoteValidation qval = null;

    // quote Id of the quote with which this test will run
    private static String QUOTEID;

    // filename for the BOM. keep them same as the classname
    private String BOM_FILENAME = Globals.ROOT_DIR+"/xml/NewBizAddProdAllLangEEAllLicTest";

    // static varable to do presetup
    private static boolean flag = true;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");


    private Calendar today = Calendar.getInstance();

    public NewBizAddProdAllLangEEAllLicTest(String name) {
        super(name);
    }

    /**
     * Initial setUp to to create one sforce Stub and one comergent stub.
     *
     * @throws Exception
     */

    public void oneTimeSetUp() throws Exception {
        utils.doLogin();

        comergent = new ComergentUtil();
        props = utils.loadProperties();
        QUOTEID = utils.getQuoteId(props.getProperty("NewBizAddProdAllLangEEAllLicTest.Quote"));

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
    public void testAdminWksEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdminWksEE", "Quantity", "49");

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
    public void testAdminWksEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdminWksEE", "Quantity", "50");

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
    public void testAdminWksEEOutOfRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AdminWksEE", "Quantity", "51");

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
    public void testNewFeatWksEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksEE", "Quantity", "49");

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
    public void testNewFeatWksEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksEE", "Quantity", "50");

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
    public void testNewFeatWksEEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksEE", "Quantity", "51");

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
    public void testPrtnrAdmWksEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("PrtnrAdmWksEE", "Quantity", "49");

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
    public void testPrtnrAdmWksEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("PrtnrAdmWksEE", "Quantity", "50");

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
    public void testPrtnrAdmWksEEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("PrtnrAdmWksEE", "Quantity", "51");

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
    public void testNewFeatWksClsrmEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksClsrmEE", "Quantity", "49");

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
    public void testNewFeatWksClsrmEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksClsrmEE", "Quantity", "50");

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
    public void testNewFeatWksClsrmEEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("NewFeatWksClsrmEE", "Quantity", "51");

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
    public void testSCntrlWksEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("SCntrlWksEE", "Quantity", "49");

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
    public void testSCntrlWksEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("SCntrlWksEE", "Quantity", "50");

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
    public void testSCntrlWksEEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("SCntrlWksEE", "Quantity", "51");

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
    public void testAppXAPIWksEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXAPIWksEE", "Quantity", "49");

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
    public void testAppXAPIWksEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXAPIWksEE", "Quantity", "50");

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
    public void testAppXAPIWksEEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXAPIWksEE", "Quantity", "51");

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
    public void testAppXLabWksEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXLabWksEE", "Quantity", "49");

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
    public void testAppXLabWksEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXLabWksEE", "Quantity", "50");

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
    public void testAppXLabWksEEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXLabWksEE", "Quantity", "51");

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
    public void testAppXMobWksEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXMobWksEE", "Quantity", "49");

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
    public void testAppXMobWksEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXMobWksEE", "Quantity", "50");

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
    public void testAppXMobWksEEGrtRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppXMobWksEE", "Quantity", "51");

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
    public void testAppExchMobileEEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("AppExchMobileEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testAppExchMobileEEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppExchMobileEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testAppExchMobileEEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("AppExchMobileEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testAppExchMobileEElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("AppExchMobileEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testAppExchMobileEEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("AppExchMobileEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testAppExchMobileEEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("AppExchMobileEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testStor500MbEEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor500MbEE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (500mb) - Enterprise Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }

    }

    @TestLabels("extended")
    public void testStor500MbEEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testStor500MbEEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("Stor500MbEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testStor500MbEElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("Stor500MbEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testStor500MbEEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor500MbEE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (500mb) - Enterprise Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor500MbEEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("Stor500MbEE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (500mb) - Enterprise Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbEEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor50MbEE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (50mb) - Enterprise Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbEEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testStor50MbEEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("Stor50MbEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testStor50MbEElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("Stor50MbEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testStor50MbEEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("Stor50MbEE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (50mb) - Enterprise Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testStor50MbEEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("Stor50MbEE", "uevValue", dateFormat.format(today.getTime()));

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
                        "Item start date for Additional Carrier-Class MMR Storage (50mb) - Enterprise Edition (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPEEprevDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, -1);
        utils.changeAttribute("ConSAPEE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for ConnectSAP - EE (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPEEtodayDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("ConSAPEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testConSAPEEnextDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.DATE, 1);
        utils.changeAttribute("ConSAPEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testConSAPEElessEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -2);
        utils.changeAttribute("ConSAPEE", "uevValue", dateFormat.format(today.getTime()));

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
    public void testConSAPEEonEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        today.add(Calendar.DATE, -1);
        utils.changeAttribute("ConSAPEE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for ConnectSAP - EE (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    @TestLabels("extended")
    public void testConSAPEEgrtEndDate() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        today.add(Calendar.YEAR, 1);
        utils.changeAttribute("ConSAPEE", "uevValue", dateFormat.format(today.getTime()));

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
                if (msg.getMsgValue().contains("Item start date for ConnectSAP - EE (")
                        && msg.getMsgValue().contains(") must occur on or after contract start (")
                        && msg.getMsgValue().contains(") and before contract end date (")) error = true;
            }
            if (!error) {
                fail("Error msg not correct");
            }
        }
    }

    /* All PRM licences selected with quantity 1 and the total quantity of licenses selected is 50
     * but AppExchange Mobile quantity depends n the total number of CRM and Platform Licenses
    */
    @TestLabels("extended")
    public void testAppExchMobileEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppExchMobileEE", "Quantity", "39");

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
    public void testAppExchMobileEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppExchMobileEE", "Quantity", "40");

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
    public void testAppExchMobileEEOutOfRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("AppExchMobileEE", "Quantity", "41");

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
    public void testStor500MbEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbEE", "Quantity", "49");

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
    public void testStor500MbEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbEE", "Quantity", "50");

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
    public void testStor500MbEEOutOfRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor500MbEE", "Quantity", "51");

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
    public void testStor50MbEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbEE", "Quantity", "49");

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
    public void testStor50MbEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbEE", "Quantity", "50");

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
    public void testStor50MbEEOutOfRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("Stor50MbEE", "Quantity", "51");

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
    public void testConSAPEEInRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("ConSAPEE", "Quantity", "49");

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
    public void testConSAPEEOnRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("ConSAPEE", "Quantity", "50");

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
    public void testConSAPEEOutOfRange() throws Exception {

        // store the bom.xml file into the directory
        utils.getBOM(QUOTEID);

        utils.changeAttribute("ConSAPEE", "Quantity", "51");

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
