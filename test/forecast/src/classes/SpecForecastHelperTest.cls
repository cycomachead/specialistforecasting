/**
 *
 * Class Name : SpecForecastHelperTest
 * Description: Contains test cases to test utility methods for forecast
 * @since 178.store
 */

public class SpecForecastHelperTest {

    private static testMethod void testGetMonthsInQuarter() {
        List<String> months = ForecastHelper.getMonthsInQuarter('Q1');
        System.AssertEquals('February', months.get(0));
        System.AssertEquals('March', months.get(1));
        System.AssertEquals('April', months.get(2));
        months = ForecastHelper.getMonthsInQuarter('Q2');
        System.AssertEquals('May', months.get(0));
        System.AssertEquals('June', months.get(1));
        System.AssertEquals('July', months.get(2));
        months = ForecastHelper.getMonthsInQuarter('Q3');
        System.AssertEquals('August', months.get(0));
        System.AssertEquals('September', months.get(1));
        System.AssertEquals('October', months.get(2));
        months = ForecastHelper.getMonthsInQuarter('Q4');
        System.AssertEquals('November', months.get(0));
        System.AssertEquals('December', months.get(1));
        System.AssertEquals('January', months.get(2));
    }

    private static testMethod void testGetCurrentQuarter() {
        String curQrtr = ForecastHelper.getCurrentQuarter();
        System.Assert(curQrtr == 'Q1' || curQrtr == 'Q2' || curQrtr == 'Q3' || curQrtr == 'Q4');
    }

    // update the assert of FY13 every year (1st February)
    private static testMethod void testGetCurrentFiscalYear() {
        String curFY = ForecastHelper.getCurrentFiscalYear();
        System.AssertEquals('FY14', curFY);
    }

    // update the assert of FY12 every year (1st February)
    private static testMethod void testGetLastFiscalYear() {
        String lastFY = ForecastHelper.getLastFiscalYear();
        System.AssertEquals('FY13', lastFY);
    }

    private static testMethod void testGetPreviousFiscalYear() {
        String previousFY = ForecastHelper.getPreviousFiscalYear('FY13');
        System.AssertEquals('FY12', previousFY);
        previousFY = ForecastHelper.getPreviousFiscalYear('FY14');
        System.AssertEquals('FY13', previousFY);
        previousFY = ForecastHelper.getPreviousFiscalYear('FY24');
        System.AssertEquals('FY23', previousFY);
        previousFY = ForecastHelper.getPreviousFiscalYear('FY20');
        System.AssertEquals('FY19', previousFY);
        previousFY = ForecastHelper.getPreviousFiscalYear('FY00');
        System.AssertEquals('FY-1', previousFY);
    }
    
    // MUST be updated every year!!
    // TODO Fix for testing with Data and without
    private static testMethod void testGetSupportedFiscalYears() {
        List<String> supportedFYs = ForecastHelper.getSupportedFiscalYears();
        System.AssertEquals('FY14', supportedFYs.get(0), 'supported is FY14');
        // System.AssertEquals(supportedFYs.get(1), 'FY14', 'supported is FY14');
        // System.AssertEquals(supportedFYs.get(2), 'FY15', 'supported is FY15');

    }

    private static testMethod void testGetSupportedFiscalQuarters() {
        List<String> supportedFQs = ForecastHelper.getSupportedFiscalQuarters();
        System.AssertEquals('Q1', supportedFQs.get(0));
        System.AssertEquals('Q2', supportedFQs.get(1));
        System.AssertEquals('Q3', supportedFQs.get(2));
        System.AssertEquals('Q4', supportedFQs.get(3));
    }

    private static testMethod void testExtractFYAndMonthFromDate() {
        Date myDate = Date.newInstance(2012, 1, 17);
        String[] monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('January', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 2, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('February', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 3, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('March', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 4, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('April', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 5, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('May', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 6, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('June', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 7, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('July', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 8, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('August', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 9, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('September', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 10, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('October', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 11, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('November', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 12, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('December', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);

        myDate = Date.newInstance(2013, 1, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('January', monthAndFiscalYear[0]);
        System.AssertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 2, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('February', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 3, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('March', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 4, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('April', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 5, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('May', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 6, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('June', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 7, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('July', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 8, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('August', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 9, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('September', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 10, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('October', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 11, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('November', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 12, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('December', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);

        myDate = Date.newInstance(2014, 1, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('January', monthAndFiscalYear[0]);
        System.AssertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 2, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('February', monthAndFiscalYear[0]);
        System.AssertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 3, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('March', monthAndFiscalYear[0]);
        System.AssertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 4, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('April', monthAndFiscalYear[0]);
        System.AssertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 5, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('May', monthAndFiscalYear[0]);
        System.AssertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 6, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('June', monthAndFiscalYear[0]);
        System.AssertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 7, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('July', monthAndFiscalYear[0]);
        System.AssertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 8, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('August', monthAndFiscalYear[0]);
        System.AssertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 9, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('September', monthAndFiscalYear[0]);
        System.AssertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 10, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('October', monthAndFiscalYear[0]);
        System.AssertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 11, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('November', monthAndFiscalYear[0]);
        System.AssertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 12, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('December', monthAndFiscalYear[0]);
        System.AssertEquals('FY15', monthAndFiscalYear[1]);

        myDate = Date.newInstance(2011, 1, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('January', monthAndFiscalYear[0]);
        System.AssertEquals('FY11', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 2, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('February', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 3, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('March', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 4, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('April', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 5, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('May', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 6, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('June', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 7, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('July', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 8, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('August', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 9, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('September', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 10, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('October', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 11, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('November', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 12, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.AssertEquals('December', monthAndFiscalYear[0]);
        System.AssertEquals('FY12', monthAndFiscalYear[1]);
    }

    private static testMethod void testApplyMonthsFromQuarter() {
        Date myDate = Date.newInstance(2012, 4, 17);
        Map<String, Boolean> showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q1', showHideMap, myDate);
        System.Assert(showHideMap.get('hideFirstMonth'));
        System.Assert(showHideMap.get('hideSecondMonth'));
        System.Assert(! showHideMap.get('hideThirdMonth'));
        // myDate = Date.newInstance(2012, 4, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q2', showHideMap, myDate);
        System.Assert(! showHideMap.get('hideFirstMonth'));
        System.Assert(! showHideMap.get('hideSecondMonth'));
        System.Assert(! showHideMap.get('hideThirdMonth'));
        myDate = Date.newInstance(2012, 3, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q1', showHideMap, myDate);
        System.Assert(showHideMap.get('hideFirstMonth'));
        System.Assert(! showHideMap.get('hideSecondMonth'));
        System.Assert(! showHideMap.get('hideThirdMonth'));
        myDate = Date.newInstance(2012, 5, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q1', showHideMap, myDate);
        System.Assert(showHideMap.get('hideFirstMonth'));
        System.Assert(showHideMap.get('hideSecondMonth'));
        System.Assert(showHideMap.get('hideThirdMonth'));
        
        myDate = Date.newInstance(2012, 6, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q2', showHideMap, myDate);
        System.Assert(showHideMap.get('hideFirstMonth'));
        System.Assert(! showHideMap.get('hideSecondMonth'));
        System.Assert(! showHideMap.get('hideThirdMonth'));

        myDate = Date.newInstance(2012, 2, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q1', showHideMap, myDate);
        System.Assert(! showHideMap.get('hideThirdMonth'));

        myDate = Date.newInstance(2012, 8, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q3', showHideMap, myDate);
        System.Assert(! showHideMap.get('hideFirstMonth'));
        System.Assert(! showHideMap.get('hideSecondMonth'));
        System.Assert(! showHideMap.get('hideThirdMonth'));

        myDate = Date.newInstance(2012, 11, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q4', showHideMap, myDate);
        System.Assert(!showHideMap.get('hideFirstMonth'));
        System.Assert(!showHideMap.get('hideSecondMonth'));
        System.Assert(!showHideMap.get('hideThirdMonth'));
    }

}
