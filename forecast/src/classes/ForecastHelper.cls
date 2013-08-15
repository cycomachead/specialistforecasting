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
 *  Helper Class used for Forecast related functionalities
 *  This class contains basic labels used for Specialist Forecasting and some
 *  common functions that aren't tied to any specific class.
 *  Not all functions are currently in use.
 *  @author Michael Ball
 */
public with sharing class ForecastHelper {

    /** Category Configuration Settings */
    public static final String CAT_STORE_BATCH_JOB = 'SForecastBatchJob';
    public static final String CAT_SUPPORTED_DATA  = 'SupportedData';
    public static final String CAT_SUPPORTED_FY    = 'Supported Fiscal Years';

    /** Config Settings Map keys */
    public static final String NAME       = 'Name';
    public static final String CONFIG_CAT = 'ConfigurationCategory';
    public static final String CONFIG_VAL = 'ConfigurationValue';
    public static final String ACTIVE     = 'IsActive';
    public static final String COMMENT    = 'Comment';

    /** Standard Forecast Categories. */
    public static final String BEST_CASE   = 'Best Case';
    public static final String CLOSED      = 'Closed';
    public static final String CLOSED_WON  = 'Closed Won';
    public static final String COMMIT_MSG      = 'Commit';
    public static final String PIPELINE    = 'Pipeline';
    public static final String GRAND_TOTAL = 'Total';

    public static final String HIERARCHY_TYPE_ROLE = 'ROLE';
    public static final String HIERARCHY_TYPE_USER = 'USER';
    /** Forecast Configurations Custom Settings Strings */
    public static final String DONT_EMAIL          = 'DoNotSendEmail';
    public static final String VALUE_YES           = 'yes';
    public static final String QTR_ALL             = 'All';
    public static final String BATCH_RUN_DEF_DAY   = 'Sunday';
    public static final String SCHEDULER_FREQUENCY = 'SchedulerFrequency';
    public static final String SF_BATCH            = 'SpecialistForecastBatch';
    public static final String BATCH_COMPLETED     = 'BatchCompletedEmail';
    public static final String SUBMIT_BATCH = 'SpecialistForecastSubmitBatch';
    public static final String SUBMIT_BATCH_RUN = 'SpecialistForecastSubmitRun';
    public static final String BATCH_COMMENT       =
        'Default Batch Processing Settings Used for Specialist Forecasting';

    /** Error messages */
    private static final String CANT_FIND_USER =
            SpecialistForecastException.CANT_FIND_USER;
    private static final String LI_ERROR = SpecialistForecastException.LI_ERROR;
    private static final String QUOTA_ERROR =
            SpecialistForecastException.QUOTA_ERROR;

    /** List of all supported quarters. */
    private static final String[] ALL_FQ = new String[] {'Q1', 'Q2', 'Q3', 'Q4'};
    /** Runtime generated list of supported fiscal years. */
    private static String[] supportedFY;
    // For future, this should be created dynamically, not from config settingd

    /**************************************************************************/

    /** Returns configuration settings for batch jobs. */
    public static Map<String, String> getBatchJobs() {
        Set<String> batchJobNames;
        try {
            batchJobNames =
                RetrieveForecastConfigurations.getForecastConfigurationNames(
                ForecastHelper.CAT_STORE_BATCH_JOB);
        } catch (Exception ex) {}
        if (batchJobNames == null || batchJobNames.size() == 0) {
            return null;
        }
        Map<String, String> batchJobs = new Map<String, String>();
        for(String batchName : batchJobNames) {
            Map<String, String> strBatches =
                RetrieveForecastConfigurations.getForecastConfigurationsByName(
                batchName);
            batchJobs.put(strBatches.get(NAME), strBatches.get(CONFIG_VAL));
        }
        return batchJobs;
    }

    /** This checks that custom settings are in place for batch jobs to run. */
    public static void setupBatchJobs(Boolean force) {
        Map<String, String> jobs;
        try {
            jobs = getBatchJobs();
        } catch (Exception ex) {}
        if (force || jobs == null) {
            ForecastConfigurations__c batch = new ForecastConfigurations__c(
                Name = SF_BATCH,
                ConfigurationCategory__c = CAT_STORE_BATCH_JOB,
                ConfigurationValue__c = VALUE_YES,
                IsActive__c = true,
                Comment__c = BATCH_COMMENT);
            ForecastConfigurations__c submit = new ForecastConfigurations__c(
                Name = SUBMIT_BATCH,
                ConfigurationCategory__c = CAT_STORE_BATCH_JOB,
                ConfigurationValue__c = VALUE_YES,
                IsActive__c = true,
                Comment__c = BATCH_COMMENT);
            ForecastConfigurations__c[] configs = new ForecastConfigurations__c[]
                { batch, submit };
            try {
                UPSERT configs Name;
            } catch (Exception ex) {
                return;
            }
        }
    }

    /** returns the notification email set in custom settings */
    public static String getCronEmail() {
        try {
            Map<String, String> email =
                RetrieveForecastConfigurations.getForecastConfigurationsByName(
                BATCH_COMPLETED);
            if (email != null && email.get(ACTIVE) == 'true') {
                return email.get(CONFIG_VAL);
            }
            return DONT_EMAIL;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     *  Returns the scheduled run day from custom settings, or the default day
     *  if one can't be found.
    */
    public static String getCronRunDay() {
        try {
            Map<String, String> runday =
                RetrieveForecastConfigurations.getForecastConfigurationsByName(
                ForecastHelper.SUBMIT_BATCH_RUN);
            if (runday != null && runday.get(ACTIVE) == 'true' ) {
                return runday.get(CONFIG_VAL);
            }
            return BATCH_RUN_DEF_DAY;
        } catch (Exception ex) {
            return BATCH_RUN_DEF_DAY;
        }
    }

    /**
     *  Send error email notification to the provided addr
     *  @param String subject
     *  @param String formattedBody
     *  @param addr is the email address to send to.
     */
    public static void sendNotification(String subject,
    String formattedBody, String addr) {
        if (addr == DONT_EMAIL || addr == null) {
            return;
        }

        Messaging.SingleEmailMessage mail = new Messaging.SingleEmailMessage();
                                     mail.setToAddresses(new String[]{ addr });
                                     mail.setSubject(subject);
                                     mail.setPlainTextBody(formattedBody);
        try {
            Messaging.sendEmail(new Messaging.Email[] { mail });
        } catch(Exception ex) {
            // Usually run from console, so no need to notify user?
        }
    }

    /**
     *  @param userID is the user to search for
     *  @return A user with the input userID
     *  @throws SpecialistForecastException
     */
    public static User getDetailsByUserId(Id userId) {
        try {
            User aUser = [SELECT Id, Name, UserName, Email, UserType, UserRoleId,
                IsActive, Alias, FirstName, UserRole.Name FROM User WHERE Id = :userId];
            return aUser;
        } catch (Exception ex) {
            throw new SpecialistForecastException(CANT_FIND_USER + userID + '.');
        }
    }

    /**
     * @return a UserRole object (Name, ID) from a user.
     */
    public static UserRole getRoleByUserId(Id userId) {
        User aUser = getDetailsByUserId(userId);
        try {
            UserRole aRole = new UserRole();
            aRole.Id       = aUser.UserRoleId;
            aRole.Name     = aUser.UserRole.Name;
            return aRole;
        } catch (Exception ex) { // TODO use better error msg
            throw new SpecialistForecastException('No Role Found ' + userID + '.');
        }
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
        } catch (Exception ex) {
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,
                LI_ERROR + ex.getMessage()));
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
            GROUP BY SpecialistForecastuser__c, FiscalYear__c
            ORDER BY SpecialistForecastuser__c];
        } catch (Exception ex) {
            ApexPages.addMessage(new ApexPages.message(ApexPages.severity.ERROR,
                QUOTA_ERROR + ex.getMessage()));
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
      *  @return True IFF the user has access to the proper forecasts data
      */
     public static Boolean checkForecastDataSyncAccessForCurrentUser() {

         Map<String, Schema.SObjectField> mForecast =
             Schema.SObjectType.ForecastHierarchyConfigurations__c.fields.getMap();
         if (mForecast == null || mForecast.size() == 0) {
             return false;
         }

         String[] accessFields = new String [] {'checkAccessToSyncJob__c'};
         // Check if the user has Read access for the field
         for (String fieldToCheck : accessFields) {
             System.debug('\n\n ### MAP ACCESS:\t ' + mForecast.get(fieldToCheck).getDescribe().isAccessible());
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
     */ //In the future this could be handled based on user currency.
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
     *  Given a DATE, a FQ, and FY, determine if the date is in FQ-FY.
     *  @param date is string YYYY-MM-DD
     *  @paran fq is a string Q#, or 'All'
     *  @param fy is a string FY##
     *  @return True IFF the date is in the selected quarter and year.
     */
    // NOTE: This is currently untested and unused.
    public static Boolean dateInFQAndFY(String dates, String fq, String fy) {
        Integer recordYr, recordMon, checkQtr, checkYr;
        try {
            recordYr  = Integer.valueOf(dates.substring(0,3));
            recordMon = Integer.valueOf(dates.substring(5,6));
            checkQtr  = fq != QTR_ALL ? Integer.valueOf(fq.substring(1)) : 0;
            checkYr   = Integer.valueOf(fy.substring(2));
        } catch (Exception ex) {
            // ERROR
            return null;
        }

        Boolean correctFY = MonthUtil.getFY(recordYr, recordMon) == checkYr;
        if (fq == QTR_ALL) {
            return correctFY;
        } else {
            return MonthUtil.getFiscalQtr(recordMon) == checkQtr && correctFY;
        }
    }
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
    public static String[] extractFYAndMonth(Date inpDate) {
        if (inpDate == null) {
            return extractFYAndMonth(System.today());
        }

        return new String[] { MonthUtil.getMonth(inpDate.month()),
                              'FY' + MonthUtil.getFYFromDate(inpDate) };
    }

}
