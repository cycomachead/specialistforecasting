package testcase.appstore.comergent.api;

import junit.framework.Test;
import junit.framework.TestSuite;
import testcase.appstore.comergent.selenium.*;


public class AllTests {

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for testcase.appstore.comergent.api");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestOneTimeSetUp.class);
        suite.addTestSuite(NewBizAddProdAllLangEEAllLicTest.class);
        suite.addTestSuite(NewBizAddProdAllLangUEAllLicTest.class);
        suite.addTestSuite(NewBizAddProdAllLangPESFATest.class);
        suite.addTestSuite(NewBizAddProdAllLangGELicTest.class);
        suite.addTestSuite(TestEECrmLicPacConServ.class);
        suite.addTestSuite(TestNewBizEditionLangSded.class);
        suite.addTestSuite(TestNewBizEEQuoteLicenceQty.class);
        suite.addTestSuite(TestNewBizEEQuotePortugueseLicenceQty.class);
        suite.addTestSuite(TestNewBizEEQuoteSChineseLicenceQty.class);
        suite.addTestSuite(TestNewBizEEQuoteSpanishLicenceQty.class);
        suite.addTestSuite(TestNewBizEEQuoteTChineseLicenceQty.class);
        suite.addTestSuite(TestNewBizPEQuoteLicenceQty.class);
        suite.addTestSuite(TestNewBizzEEPremiumSupport.class);
        suite.addTestSuite(TestNewBizzPEPremiumSupport.class);
        suite.addTestSuite(TestPECrmLicPacConServ.class);
        suite.addTestSuite(TestUECrmLicPacConServ.class);
        suite.addTestSuite(TestNewBizTEOneSetLicPunchOut.class);
        suite.addTestSuite(TestFinalCleanUp.class);
        //$JUnit-END$
        return suite;
    }

}
