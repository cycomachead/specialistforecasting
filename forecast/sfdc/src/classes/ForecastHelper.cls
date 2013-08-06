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
 * Helper Class used for Forecast related functionalities
 *
 * @author Michael Ball
 */
public with sharing class ForecastHelper {

    public static final String CAT_STORE_BATCH_JOB = 'StoreBatchJob';
    public static final String CAT_SUPPORTED_DATA  = 'SupportedData';
    public static final String CAT_SUPPORTED_FY    = 'Supported Fiscal Years';

    /** Config Settings Map keys */
    public static final String NAME       = 'Name';
    public static final String CONFIG_CAT = 'ConfigurationCategory';
    public static final String CONFIG_VAL = 'ConfigurationValue';
    public static final String ACTIVE     = 'IsActive';
    public static final String COMMENT    = 'Comment';

    /** Column Headings and related uses. */
    public static final String BEST_CASE   = 'Best Case';
    public static final String CLOSED      = 'Closed';
    public static final String CLOSED_WON  = 'Closed Won';
    public static final String COMMIT_MSG  = 'Commit';
    public static final String PIPELINE    = 'Pipeline';
    public static final String GRAND_TOTAL = 'Total';

    public static final String HIERARCHY_TYPE_ROLE = 'ROLE';
    public static final String HIERARCHY_TYPE_USER = 'USER';
    public static final String DONT_SEND_EMAIL     = 'DoNotSendEmail';
    public static final String VALUE_YES           = 'yes';
    public static final String QTR_ALL             = 'All';
    public static final String BATCH_RUN_DEF_DAY   = 'Sunday';
    public static final String SCHEDULER_FREQUENCY = 'SchedulerFrequency';
    public static final String SF_BATCH            = 'SpecialistForecastBatch';
    public static final String BATCH_COMPLETED     = 'BatchCompletedEmail';
    public static final String SUBMIT_BATCH = 'SpecialistForecastSubmitBatch';
    public static final String SUBMIT_BATCH_RUN = 'SpecialistForecastSubmitRun';

    /** Error messages */
    private static final String CANT_FIND_USER =
            SpecialistForecastException.CANT_FIND_USER;
    private static final String CRON_SCHED_ERR =
            SpecialistForecastException.CRON_SCHED_ERR;

    /** TODO */
    public static String rtName = null;
    /** TODO */
    private static Map<String, String> batchJobs;
    /** List of all supported quarters. */
    private static final String[] ALL_FQ = new String[] {'Q1', 'Q2', 'Q3', 'Q4'};
    /** Runtime generated list of supported fiscal years. */
    private static String[] supportedFY;

    /**************************************************************************/

    public static Map<String, String> getBatchJobs() {
        Set<String> batchJobNames =
            RetrieveForecastConfigurations.getForecastConfigurationNames(
            ForecastHelper.CAT_STORE_BATCH_JOB);
        if (batchJobNames == null || batchJobNames.size() == 0) {
            return null;
        }
        batchJobs = new Map<String, String>();
        for(String batchName : batchJobNames) {
            Map<String, String> strBatches =
                RetrieveForecastConfigurations.getForecastConfigurationsByName(
                batchName);
            // if (strBatches == null || strBatches.size() == 0
            // || strBatches.get(ACTIVE) != 'true') {
            //     continue;
            // }
            batchJobs.put(strBatches.get(NAME), strBatches.get(CONFIG_VAL));
        }
        return batchJobs;
    }

    /**
     *  @return current 'cron' style expr. for scheduled jobs.
     *  @throws SpecialistForecastException
     */
    public static String getCronExpression() {
        Map<String, String> expression =
            RetrieveForecastConfigurations.getForecastConfigurationsByName(
            SCHEDULER_FREQUENCY);
        if (expression != null && expression.get(ACTIVE) == 'true') {
            return expression.get(CONFIG_VAL);
        }
        throw new SpecialistForecastException(CRON_SCHED_ERR);
    }

    public static String getCronEmail() {
        Map<String, String> email =
            RetrieveForecastConfigurations.getForecastConfigurationsByName(
            BATCH_COMPLETED);
        if (email != null && email.get(ACTIVE) == 'true') {
            return email.get(CONFIG_VAL);
        }
        return DONT_SEND_EMAIL;
    }

    public static String getCronRunDay() {
        Map<String, String> runday =
            RetrieveForecastConfigurations.getForecastConfigurationsByName(
            ForecastHelper.SUBMIT_BATCH_RUN);
        if (runday != null && runday.get(ACTIVE) == 'true' ) {
            return runday.get(CONFIG_VAL);
        }
        return BATCH_RUN_DEF_DAY;
    }

    /** TODO */
    public static String getRecordTypeName(Id rtId) {
        if (rtName == null) {
            RecordType rt = [SELECT DeveloperName FROM RecordType
                WHERE Id = :rtId limit 1];
            rtName = rt.DeveloperName;
        }
        return rtName;
    }

    /**
     *  @param userID is the user to search for
     *  @return A user with the input userID
     *  @throws SpecialistForecastException
     */
    public static User getDetailsByUserId(Id userId) {
        try {
            User aUser = ([SELECT Id, Name, UserName, Email, UserType, UserRoleId,
                IsActive, Alias FROM User WHERE Id = :userId]);
            return aUser;
        } catch (Exception ex) {
            throw new SpecialistForecastException(CANT_FIND_USER + userID + '.');
        }
    }

    /**
     * @return TODO
     */
    public static UserRole getRoleByUserId(Id userId) {
        User aUser = getDetailsByUserId(userId);
        UserRole aRole = [SELECT id, name FROM UserRole
            WHERE id = :aUser.UserRoleId];
        return aRole;
    }

    /**
     *   gets all Specialist Forecast line items by matching
     *   Year, Month and owners of lineitems
     *   @param fy is the fiscal year (String) to search for
     *   @param mons is a String[] are the months to search on
     *   @param owners is a Set<Id> are the owners to search from.
     *   @return a list of all line item records matching the search terms.
     */
    public static SpecialistForecastLineItem__c[] getAllLineItems(String fy,
    String[] mons, Set<Id> owners) {
        try {
            return [SELECT Id, ForecastOwner__c, FiscalYear__c, Month__c,
            OriginalBestcaseAmount__c, OriginalClosedAmount__c,
            OriginalCommitAmount__c, OriginalPipelineAmount__c,
            SpecialistForecastOverride__r.OwnOverriddenCommitAmount__c,
            SpecialistForecastOverride__r.ManagerOverriddenCommitAmount__c,
            SpecialistForecastOverride__r.OwnOverriddenBestcaseAmount__c,
            SpecialistForecastOverride__r.ManagerOverriddenBestCaseAmount__c,
            SpecialistForecastOverride__r.ManagerOverrideCommitNotes__c,
            SpecialistForecastOverride__r.ManagerOverrideBestcaseNotes__c,
            SpecialistForecastOverride__r.PreviousDirectRepCommitAmount__c,
            SpecialistForecastOverride__r.PreviousDirectRepBestcaseAmount__c,
            SpecialistForecastOverride__c, SubmittedDate__c
            FROM SpecialistForecastLineItem__c
            WHERE ExcludeFromCalculation__c = false
            AND FiscalYear__c = :fy AND Month__c IN :mons
            AND ForecastOwner__c IN :owners ORDER By ForecastOwner__c];
        } catch (Exception ex) { // FIXME granular exception and error label
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,
            'An error occurred while trying to access Specialist Forecast ' +
            'line items. <br /> ' + ex.getMessage()));
            return null;
        }
    }


    /**
     *  gets all the quota amount by Year, Month and owners results used by
     *  SpecialistForecastController to display quota amount for AE
     *  @param fy is the fiscal year (String) to search for
     *  @param mons is a String[] are the months to search on
     *  @param owners is a Set<Id> are the owners to search from.
     *  @return a list of all quota records matching the search terms.
     */
    public static AggregateResult[] getAllQuotas(String fy, String[] mons,
    Set<Id> owners) {
        try {
            return [SELECT SpecialistForecastUser__c, FiscalYear__c,
            SUM(QuotaAmount__c) amnt FROM SpecialistForecastQuota__c
            WHERE FiscalYear__c = :fy AND Month__c IN :mons
            AND SpecialistForecastuser__c IN :owners
            GROUP BY  SpecialistForecastuser__c,FiscalYear__c
            ORDER BY SpecialistForecastuser__c];
        } catch (Exception ex) { // FIXME granular exception and error label
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,
            'An error occurred while trying to access Specialist Forecast ' +
            'quotas. <br /> ' + ex.getMessage()));
            return null;
        }
    }

    /**
     *  A common FLS check for Company Specialist Forecasting
     *  @return true IFF user has access to all necessary forecast objects
     */
    public static Boolean canAccessForecasts() {
        return (Schema.SObjectType.SpecialistForecast__c.isAccessible() &&
            Schema.SObjectType.SpecialistForecastLineItem__c.isAccessible() &&
            Schema.SObjectType.SpecialistForecastQuota__c.isAccessible() &&
            Schema.SObjectType.SpecialistForecastOverride__c.isAccessible());
    }

     /**
      *  This method is used to check access for the page exists for data sync
      *  @return TODO
      */ // FIXME -- remove unecessary iteration?
     public static Boolean checkForecastDataSyncAccessForCurrentUser() {

         Map<String, Schema.SObjectField> mForecast =
             Schema.SObjectType.ForecastHierarchyConfigurations__c.fields.getMap();
         if (mForecast == null || mForecast.size() == 0) {
             return false;
         }

         String[] accessFields = new String [] {'checkAccessToSyncJob__c'};
         // Check if the user has Read access for the field
         for (String fieldToCheck : accessFields) {
             if (mForecast.get(fieldToCheck) == null ||
             !mForecast.get(fieldToCheck).getDescribe().isAccessible()) {
                 return false;
             }
         }
         return true;
     }

    /**
     *  Simple search of selectOption list, based on selectoption VALUES
     *  @param lst is the list to be searched
     *  @param search is the search term
     *  @return true IFF an exact match is found in the list (cast INsensitive)
     */
    public static Boolean listContains(SelectOption[] lst, String search) {
        for (SelectOption item : lst) {
            if (item.getValue() == search) { return true; }
        }
        return false;
    }

    /**
     *  Formats a string for a table view to look like: $0.00
     *  @param value is Decimal type representing some money.
     *  @return a string formatted as US currency.
     */ //FIXME There should be a generic way to do this!
    public static String formatAsDollar(Decimal value) {
        String[] formatter = new String[] {'0','number','###,###,##0.00'};
        if (value != null) {
            value.setScale(2);
            String formatted = String.format(value.format(), formatter);
            if (!formatted.contains('.' )) { formatted += '.00'; }
            return formatted;
        }
        return null;
    }

    /**************************************************************************/
    /**               FISCAL YEAR CALCUATION UTIL METHODS                    **/
    /**
     * @return current fiscal quarter as a string, 'Q#', based on system time.
     */
    public static String getCurrentQuarter() {
        return 'Q' + MonthUtil.getCurrentFiscalQtr();
    }

    /**
     * @return The current fiscal year as a string 'FY##'.
     */
    public static String getCurrentFiscalYear() {
        return 'FY' + MonthUtil.normalizeYrToStr(MonthUtil.getCurrentFY());
    }

    /**
     * @return previous fiscal year (from current month) as a string 'FY##'.
     */
    public static String getLastFiscalYear() {
        return 'FY' + MonthUtil.normalizeYrToStr(MonthUtil.getCurrentFY() - 1);
    }

    /**
     *  Given a fiscal year, return the year prior.
     *  @param fYear is a fiscal year, "FY##"
     *  @return the two digit FY as a string in the format 'FY##'.
     */
    public static String getPreviousFiscalYear(String yr) {
        try {
            return 'FY' + MonthUtil.normalizeYrToStr(
                Integer.valueOf(yr.substring(2)) - 1);
        } catch (Exception ex) {
            return 'ERROR';
        }
    }

    /**
     *  @param fQuarter is a fiscal quarter string, 'Q#'.
     *  @return a list of the months in the specified quarter.
     */
    public static String[] getMonthsInQuarter(String fQuarter) {
        if (fQuarter == 'All') {
            return MonthUtil.getOrderedFiscalMonths();
        }

        Integer qtr;
        try {
            qtr = Integer.valueOf(fQuarter.substring(1));
        } catch (Exception ex) {
            qtr = MonthUtil.getCurrentFiscalQtr();
        }
        return MonthUtil.getMonthsInQtr(qtr);
    }

    /**
     *  @return List of alll supported FY's as { 'FY##', ..., 'FY##' }
     */
    public static String[] getSupportedFiscalYears() {
        if (supportedFY == null || supportedFY.size() < 3) {
            try {
                Map<String, String> supportedFYs =
                RetrieveForecastConfigurations.getForecastConfigurationsByName(
                CAT_SUPPORTED_FY);
                supportedFY = supportedFYs.get(CONFIG_VAL).split(',');
            } catch (Exception ex) {
                supportedFY = new String[] { getCurrentFiscalYear() };
            }
        }
        return supportedFY;
    }

    /**
     *  @return {'Q1', 'Q2', 'Q3', 'Q4' }
     */
    public static String[] getSupportedFiscalQuarters() {
        return ALL_FQ;
    }

    /**
     *  Find the Month name and fiscal year from a given date.
     *  @param inpDate is an Apex date object
     *  @return A String[] of { 'MONTH', 'FYX##' }
     */
    public static String[] extractFYAndMonthFromDate(Date inpDate) {
        if (inpDate == null) {
            return extractFYAndMonthFromDate(System.today());
        }

        return new String[] { MonthUtil.getMonth(inpDate.month()),
                              'FY' + MonthUtil.getFYFromDate(inpDate) };
    }

}
