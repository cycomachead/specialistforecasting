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
 *  Class Name : SpecForecastHelperTest
 *  Description: Contains test cases to test utility methods for forecast
 *  @author Michael Ball
 */

@isTest
public with sharing class SpecForecastHelperTest {

    // Test returning fiscal quarters.
    private static testMethod void testGetMonthsInQuarter() {
        // This must be here for tests to pass in any org.
        MonthUtil.reset(2, false);
        String[] months = ForecastHelper.getMonthsInQuarter('Q1');
        System.assertEquals('February', months[0]);
        System.assertEquals('March', months[1]);
        System.assertEquals('April', months[2]);
        months = ForecastHelper.getMonthsInQuarter('Q2');
        System.assertEquals('May', months[0]);
        System.assertEquals('June', months[1]);
        System.assertEquals('July', months[2]);
        months = ForecastHelper.getMonthsInQuarter('Q3');
        System.assertEquals('August', months[0]);
        System.assertEquals('September', months[1]);
        System.assertEquals('October', months[2]);
        months = ForecastHelper.getMonthsInQuarter('Q4');
        System.assertEquals('November', months[0]);
        System.assertEquals('December', months[1]);
        System.assertEquals('January', months[2]);
    }

    // Test for what looks like a good quarter value
    private static testMethod void testGetCurrentQuarter() {
        String curQrtr = ForecastHelper.getCurrentQuarter();
        System.assert(curQrtr.length() == 2 &&
            Integer.valueOf(curQrtr.substring(1)) < 5, 'Current Qtr is valid');
    }

    // Checks that a string FYXX is returned and is close to the current year
    private static testMethod void testGetCurrentFiscalYear() {
        String curFY = ForecastHelper.getCurrentFiscalYear();
        System.assert(curFY.length() == 4, 'FY is correct length');
        Integer yr = Integer.valueOf(curFY.substring(2));
        Integer year = System.today().year() - 2000;
        System.assert( yr == year || yr == year + 1, 'Year is close.');
    }

    // update the assert of FY12 every year (1st February)
    private static testMethod void testGetLastFiscalYear() {
        String curFY = ForecastHelper.getLastFiscalYear();
        System.assert(curFY.length() == 4, 'FY is correct length');
        Integer yr = Integer.valueOf(curFY.substring(2));
        Integer year = System.today().year() - 2000;
        System.assert( yr == year || yr == year - 1, 'Year is close.');
    }

    // Test the previous fiscal year function for (un)expected values.
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
        System.assertEquals('FY99', previousFY);
        // not expected, but these should work.
        previousFY = ForecastHelper.getPreviousFiscalYear('FY100');
        System.assertEquals('FY99', previousFY);
        previousFY = ForecastHelper.getPreviousFiscalYear('FY103');
        System.assertEquals('FY02', previousFY);
        // error case
        previousFY = ForecastHelper.getPreviousFiscalYear('notGonnaHappen');
        System.assertEquals('ERROR', previousFY);
    }

    // Ensures that the method returns at least the current year.
    private static testMethod void testGetSupportedFiscalYears() {
        String[] supportedFYs = ForecastHelper.getSupportedFiscalYears();
        // use because there's not list contains.
        Set<String> years = new Set<String>();
        years.addAll(supportedFYs);
        System.assert(supportedFYs.size() >= 1, 'At least 1 FY is supported');
        System.assert(years.contains(ForecastHelper.getCurrentFiscalYear()),
            'List should contain the current fiscal year');
        ForecastConfigurations__c yrs = new ForecastConfigurations__c(
            Name = 'Supported Fiscal Years',
            ConfigurationCategory__c = 'SupportedData',
            ConfigurationValue__c = 'FY10,FY11,FY12,FY13,FY14,FY15,FY16,FY17,FY18,FY19',
            IsActive__c = true,
            Comment__c = '');
        UPSERT yrs Name;
        supportedFYs = ForecastHelper.getSupportedFiscalYears();
        // use because there's not list contains.
        years.addAll(supportedFYs);
        System.assertEquals(10, supportedFYs.size());
        System.assert(years.contains('FY10'), 'List should contain FY10');
    }

    // Test for supported fiscal quarters, simple and basic.
    private static testMethod void testGetSupportedFiscalQuarters() {
        String[] supportedFQs = ForecastHelper.getSupportedFiscalQuarters();
        System.assertEquals('Q1', supportedFQs[0]);
        System.assertEquals('Q2', supportedFQs[1]);
        System.assertEquals('Q3', supportedFQs[2]);
        System.assertEquals('Q4', supportedFQs[3]);
    }

    // Tests getting a proper fiscal year from month and date
    // based on month util settings.
    private static testMethod void testextractFYAndMonth() {
        // This must be here for the tests to pass in every org.
        MonthUtil.reset(2, false);
        Date myDate = Date.newInstance(2012, 1, 17);
        String[] monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('January', monthAndFiscalYear[0]);
        System.assertEquals('FY12', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 2, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('February', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 3, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('March', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 4, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('April', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 5, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('May', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 6, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('June', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 7, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('July', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 8, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('August', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 9, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('September', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 10, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('October', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 11, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('November', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
        myDate = Date.newInstance(2012, 12, 17);
        monthAndFiscalYear = ForecastHelper.extractFYAndMonth(myDate);
        System.assertEquals('December', monthAndFiscalYear[0]);
        System.assertEquals('FY13', monthAndFiscalYear[1]);
    }

    /** Test getting and inserting setup data. */
    public static testMethod void testSetupConfig() {
        Map<String, String> configs;
        reset();
        configs = ForecastHelper.getBatchJobs();
        System.assert(configs == null, 'No batch settings');
        ForecastHelper.setupBatchJobs(false);
        System.assert(true, 'Setup was successful.');
        configs = ForecastHelper.getBatchJobs();
        System.assertEquals(2, configs.size());
    }

    /** Test simple emailing. */
    public static testMethod void testEmail() {
        String badAddr;
        ForecastHelper.sendNotification('', '', badAddr);
        System.assert(true, 'Passes for null case');
        ForecastHelper.sendNotification('Hello', 'Goodbye', 'not=a-vaild?em.ail');
        System.assert(true, 'No errors on terrible addr.');
    }

    /** Test getting a user. This is a very basic test. */
    public static testMethod void testGetUsers() {
        Id uid = UserInfo.getUserId();
        User u = ForecastHelper.getDetailsByUserId(uid);
        System.assertEquals(uid, u.Id);
        UserRole ur = ForecastHelper.getRoleByUserId(uid);
        System.assertEquals(UserInfo.getUserRoleId(), ur.Id);
    }

    /** Test for get cron email errors. */
    public static testMethod void testGetEmail() {
        reset();
        String em = ForecastHelper.getCronEmail();
        System.assert(em == null, 'No email found');
    }

    /** Test number to string currency formatting. */
    public static testMethod void testFormatting() {
        System.assertEquals('0.00', ForecastHelper.formatAsDollar(0));
        System.assertEquals('1.50', ForecastHelper.formatAsDollar(1.5));
        System.assertEquals('3.14', ForecastHelper.formatAsDollar(3.14159));
    }

    /** Check Access Function. */
    public static testMethod void testAccess() {
        User u = [SELECT Id, Name FROM User WHERE Name = 'Chatter Expert' limit 1];
        Boolean b;
        System.runAs(u) {
            b = ForecastHelper.checkForecastDataSyncAccessForCurrentUser();
        }
        System.assert(!b, 'Chatter user shouldn\'t have access');
    }

    /** Reset Config data for use in various functions. */
    public static void reset() {
        SObject[] configs = [SELECT Id FROM ForecastConfigurations__c];
        DELETE configs;
    }
}
