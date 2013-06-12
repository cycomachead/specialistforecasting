/*
Copyright (c) 2013, salesforce.com, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
    * Neither the name of the salesforce.com, Inc. nor the names of its contributors
    may be used to endorse or promote products derived from this software
    without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE. Written by Michael Ball.
*/

/**
 *
 * Class Name : SpecForecastHelperTest
 * Description: Contains test cases to test utility methods for forecast
 * @since 178.store
 */

public class SpecForecastHelperTest {

    private static testMethod void testGetMonthsInQuarter() {
        List<String> months = ForecastHelper.getMonthsInQuarter('Q1');
        System.assertEquals('February', months.get(0));
        System.assertEquals('March', months.get(1));
        System.assertEquals('April', months.get(2));
        months = ForecastHelper.getMonthsInQuarter('Q2');
        System.assertEquals('May', months.get(0));
        System.assertEquals('June', months.get(1));
        System.assertEquals('July', months.get(2));
        months = ForecastHelper.getMonthsInQuarter('Q3');
        System.assertEquals('August', months.get(0));
        System.assertEquals('September', months.get(1));
        System.assertEquals('October', months.get(2));
        months = ForecastHelper.getMonthsInQuarter('Q4');
        System.assertEquals('November', months.get(0));
        System.assertEquals('December', months.get(1));
        System.assertEquals('January', months.get(2));
    }

    private static testMethod void testGetCurrentQuarter() {
        String curQrtr = ForecastHelper.getCurrentQuarter();
        System.assert(curQrtr == 'Q1' || curQrtr == 'Q2' || curQrtr == 'Q3' || curQrtr == 'Q4');
    }

    // update the assert of FY13 every year (1st February)
    private static testMethod void testGetCurrentFiscalYear() {
        String curFY = ForecastHelper.getCurrentFiscalYear();
        System.assertEquals('FY14', curFY);
    }

    // update the assert of FY12 every year (1st February)
    private static testMethod void testGetLastFiscalYear() {
        String lastFY = ForecastHelper.getLastFiscalYear();
        System.assertEquals('FY13', lastFY);
    }

    private static testMethod void testGetPreviousFiscalYear() {
        String previousFY = ForecastHelper.getPreviousFiscalYear('FY13');
        System.assertEquals('FY12', previousFY);
        previousFY = ForecastHelper.getPreviousFiscalYear('FY14');
        System.assertEquals('FY13', previousFY);
        previousFY = ForecastHelper.getPreviousFiscalYear('FY24');
        System.assertEquals('FY23', previousFY);
        previousFY = ForecastHelper.getPreviousFiscalYear('FY20');
        System.assertEquals('FY19', previousFY);
        previousFY = ForecastHelper.getPreviousFiscalYear('FY00');
        System.assertEquals('FY-1', previousFY);
    }
    
    // MUST be updated every year!!
    // TODO Fix for testing with Data and without
    private static testMethod void testGetSupportedFiscalYears() {
        List<String> supportedFYs = ForecastHelper.getSupportedFiscalYears();
        System.assertEquals('FY14', supportedFYs.get(0), 'supported is FY14');
        // System.assertEquals(supportedFYs.get(1), 'FY14', 'supported is FY14');
        // System.assertEquals(supportedFYs.get(2), 'FY15', 'supported is FY15');

    }

    private static testMethod void testGetSupportedFiscalQuarters() {
        List<String> supportedFQs = ForecastHelper.getSupportedFiscalQuarters();
        System.assertEquals('Q1', supportedFQs.get(0));
        System.assertEquals('Q2', supportedFQs.get(1));
        System.assertEquals('Q3', supportedFQs.get(2));
        System.assertEquals('Q4', supportedFQs.get(3));
    }

    private static testMethod void testExtractFYAndMonthFromDate() {
        Date myDate = Date.newInstance(2012, 1, 17);
        String[] monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('January', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 2, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('February', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 3, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('March', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 4, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('April', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 5, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('May', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 6, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('June', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 7, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('July', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 8, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('August', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 9, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('September', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 10, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('October', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 11, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('November', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 12, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('December', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);

        myDate = Date.newInstance(2013, 1, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('January', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 2, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('February', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 3, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('March', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 4, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('April', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 5, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('May', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 6, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('June', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 7, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('July', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 8, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('August', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 9, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('September', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 10, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('October', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 11, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('November', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2013, 12, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('December', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);

        myDate = Date.newInstance(2014, 1, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('January', monthAndFiscalYear[0]);
        System.assertEquals('FY14', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 2, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('February', monthAndFiscalYear[0]);
        System.assertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 3, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('March', monthAndFiscalYear[0]);
        System.assertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 4, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('April', monthAndFiscalYear[0]);
        System.assertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 5, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('May', monthAndFiscalYear[0]);
        System.assertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 6, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('June', monthAndFiscalYear[0]);
        System.assertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 7, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('July', monthAndFiscalYear[0]);
        System.assertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 8, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('August', monthAndFiscalYear[0]);
        System.assertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 9, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('September', monthAndFiscalYear[0]);
        System.assertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 10, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('October', monthAndFiscalYear[0]);
        System.assertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 11, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('November', monthAndFiscalYear[0]);
        System.assertEquals('FY15', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2014, 12, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('December', monthAndFiscalYear[0]);
        System.assertEquals('FY15', monthAndFiscalYear[1]);

        myDate = Date.newInstance(2011, 1, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('January', monthAndFiscalYear[0]);
        System.assertEquals('FY11', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 2, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('February', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 3, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('March', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 4, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('April', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 5, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('May', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 6, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('June', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 7, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('July', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 8, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('August', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 9, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('September', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 10, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('October', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 11, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('November', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2011, 12, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonthFromDate(myDate);
        System.assertEquals('December', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
    }

    private static testMethod void testApplyMonthsFromQuarter() {
        Date myDate = Date.newInstance(2012, 4, 17);
        Map<String, Boolean> showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q1', showHideMap, myDate);
        System.assert(showHideMap.get('hideFirstMonth'));
        System.assert(showHideMap.get('hideSecondMonth'));
        System.assert(! showHideMap.get('hideThirdMonth'));
        // myDate = Date.newInstance(2012, 4, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q2', showHideMap, myDate);
        System.assert(! showHideMap.get('hideFirstMonth'));
        System.assert(! showHideMap.get('hideSecondMonth'));
        System.assert(! showHideMap.get('hideThirdMonth'));
        myDate = Date.newInstance(2012, 3, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q1', showHideMap, myDate);
        System.assert(showHideMap.get('hideFirstMonth'));
        System.assert(! showHideMap.get('hideSecondMonth'));
        System.assert(! showHideMap.get('hideThirdMonth'));
        myDate = Date.newInstance(2012, 5, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q1', showHideMap, myDate);
        System.assert(showHideMap.get('hideFirstMonth'));
        System.assert(showHideMap.get('hideSecondMonth'));
        System.assert(showHideMap.get('hideThirdMonth'));
        
        myDate = Date.newInstance(2012, 6, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q2', showHideMap, myDate);
        System.assert(showHideMap.get('hideFirstMonth'));
        System.assert(! showHideMap.get('hideSecondMonth'));
        System.assert(! showHideMap.get('hideThirdMonth'));

        myDate = Date.newInstance(2012, 2, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q1', showHideMap, myDate);
        System.assert(! showHideMap.get('hideThirdMonth'));

        myDate = Date.newInstance(2012, 8, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q3', showHideMap, myDate);
        System.assert(! showHideMap.get('hideFirstMonth'));
        System.assert(! showHideMap.get('hideSecondMonth'));
        System.assert(! showHideMap.get('hideThirdMonth'));

        myDate = Date.newInstance(2012, 11, 17);
        showHideMap = new Map<String, Boolean>();
        ForecastHelper.applyMonthsFromQuarter('Q4', showHideMap, myDate);
        System.assert(!showHideMap.get('hideFirstMonth'));
        System.assert(!showHideMap.get('hideSecondMonth'));
        System.assert(!showHideMap.get('hideThirdMonth'));
    }

}
