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
 * @since
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
    public static final String COMMIT      = 'Commit';
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
            if (strBatches == null || strBatches.size() == 0
            || strBatches.get(ACTIVE) != 'true') {
                continue;
            }
            batchJobs.put(strBatches.get(NAME), strBatches.get(CONFIG_VAL));
        }

        return batchJobs;
    }

    /**
     *  @return SOMETHING
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
        if (email != null && email.get(ACTIVE) == 'true' &&
        emailIsValid(email.get(CONFIG_VAL))) {
            return email.get(CONFIG_VAL);
        }
        return DONT_SEND_EMAIL;
    }

    /**
     *  TODO THIS NEEDS ACTUAL CONTENT TO WORK.
     *  @oaram EMAIL is an adress to be checked.
     *  @return true IFF the email is valid.
     */
    private static boolean emailIsValid(String addr) {
        return true;
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


    public static String getRecordTypeName(Id rtId) {
        if (rtName == null) {
            RecordType rt = [SELECT DeveloperName FROM RecordType
                WHERE Id = : rtId LIMIT 1];
            rtName = rt.DeveloperName;
        }
        return rtName;
    }

    public static void applyMonthsFromQuarter(String fQuarter,
    Map<String, Boolean> showHideMap) {
        applyMonthsFromQuarter(fQuarter, showHideMap, System.today());
    }

    /**
     *  TODO IS THIS EVEN USEFUL?!
     *
     *  @return TODO
     */
    public static void applyMonthsFromQuarter(String fQuarter,
    Map<String, Boolean> showHideMap, Date now) {
        List<String> monthsInQrtr = getMonthsInQuarter(fQuarter);
        Set<String> monthsInQrtrSet = new Set<String>();
        monthsInQrtrSet.addAll(monthsInQrtr);

        if (monthsInQrtrSet.contains('February')) {
            if (now.month() > 4 || now.month() == 1) {
                showHideMap.put('hideFirstMonth', true);
                showHideMap.put('hideSecondMonth', true);
                showHideMap.put('hideThirdMonth', true);
            } else if (now.month() == 4) {
                showHideMap.put('hideFirstMonth', true);
                showHideMap.put('hideSecondMonth', true);
                showHideMap.put('hideThirdMonth', false);
            } else if (now.month() == 3) {
                showHideMap.put('hideFirstMonth', true);
                showHideMap.put('hideSecondMonth', false);
                showHideMap.put('hideThirdMonth', false);
            } else if (now.month() == 2) {
                showHideMap.put('hideFirstMonth', false);
                showHideMap.put('hideSecondMonth', false);
                showHideMap.put('hideThirdMonth', false);
            }
        } else if (monthsInQrtrSet.contains('May')) {
            if (now.month() > 7 || now.month() == 1) {
                showHideMap.put('hideFirstMonth', true);
                showHideMap.put('hideSecondMonth', true);
                showHideMap.put('hideThirdMonth', true);
            } else if (now.month() == 7) {
                showHideMap.put('hideFirstMonth', true);
                showHideMap.put('hideSecondMonth', true);
                showHideMap.put('hideThirdMonth', false);
            } else if (now.month() == 6) {
                showHideMap.put('hideFirstMonth', true);
                showHideMap.put('hideSecondMonth', false);
                showHideMap.put('hideThirdMonth', false);
            } else { // 2, 3, 4, 5
                showHideMap.put('hideFirstMonth', false);
                showHideMap.put('hideSecondMonth', false);
                showHideMap.put('hideThirdMonth', false);
            }
        } else if (monthsInQrtrSet.contains('August')) {
            if (now.month() > 10 || now.month() == 1) {
                showHideMap.put('hideFirstMonth', true);
                showHideMap.put('hideSecondMonth', true);
                showHideMap.put('hideThirdMonth', true);
            } else if (now.month() == 10) {
                showHideMap.put('hideFirstMonth', true);
                showHideMap.put('hideSecondMonth', true);
                showHideMap.put('hideThirdMonth', false);
            } else if (now.month() == 9) {
                showHideMap.put('hideFirstMonth', true);
                showHideMap.put('hideSecondMonth', false);
                showHideMap.put('hideThirdMonth', false);
            } else { // 2, 3, 4, 5, 6, 7, 8
                showHideMap.put('hideFirstMonth', false);
                showHideMap.put('hideSecondMonth', false);
                showHideMap.put('hideThirdMonth', false);
            }
        } else if (monthsInQrtrSet.contains('November')) {
            if (now.month() == 11) {
                showHideMap.put('hideFirstMonth', false);
                showHideMap.put('hideSecondMonth', false);
                showHideMap.put('hideThirdMonth', false);
            } else if (now.month() == 12) {
                showHideMap.put('hideFirstMonth', true);
                showHideMap.put('hideSecondMonth', false);
                showHideMap.put('hideThirdMonth', false);
            } else if (now.month() == 1) {
                showHideMap.put('hideFirstMonth', true);
                showHideMap.put('hideSecondMonth', true);
                showHideMap.put('hideThirdMonth', false);
            } else { // 2, 3, 4, 5, 6, 7, 8, 9, 10
                showHideMap.put('hideFirstMonth', false);
                showHideMap.put('hideSecondMonth', false);
                showHideMap.put('hideThirdMonth', false);
            }
        }

        if (showHideMap.get('hideFirstMonth')
        && showHideMap.get('hideSecondMonth')
        && showHideMap.get('hideThirdMonth')) {
            showHideMap.put('hideQrtrTotal', true);
        } else {
            showHideMap.put('hideQrtrTotal', false);
        }
    }

    /**
     *  @return TODO
     *  @throws SpecialistForecastException
     */
    public static User getDetailsByUserId(Id userId) {
        if (userId == null) {
            return null;
        }
        User aUser = ([SELECT Id, Name, UserName, Email, UserType, UserRoleId,
                IsActive, Alias FROM User WHERE Id = :userId]);

        if (aUser == null) {
            throw new SpecialistForecastException(CANT_FIND_USER + userID + '.');
        }
        return aUser;
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
     *   gets all the line items by Year, Month and owners of lineitems
     *   results used by SpecialistForecastController
     *   @return
     */
    public static SpecialistForecastLineItem__c[] getSpecialistForecastLinesTable(
    ForecastReportQueryParam queryParam) {

        SpecialistForecastLineItem__c[] lineItemsData;

        lineItemsData = [SELECT Id,ForecastOwner__c, FiscalYear__c, FYMonth__c,
            OriginalBestcaseAmount__c, OriginalClosedAmount__c,
            OriginalCommitAmount__c, OriginalPipelineAmount__c,
            SpecialistForecastOverride__r.OwnOverridenCommitAmount__c,
            SpecialistForecastOverride__r.ManagerOverridenCommitAmount__c,
            SpecialistForecastOverride__r.OwnOverridenBestcaseAmount__c,
            SpecialistForecastOverride__r.ManagerOverridenBestCaseAmount__c,
            SpecialistForecastOverride__r.ManagerOverrideCommitNotes__c,
            SpecialistForecastOverride__r.ManagerOverrideBestcaseNotes__c,
            SpecialistForecastOverride__r.PreviousDirectRepCommitAmount__c,
            SpecialistForecastOverride__r.PreviousDirectRepBestcaseAmount__c,
            SpecialistForecastOverride__c, SubmittedDate__c
                        FROM SpecialistForecastLineItem__c
                        WHERE ExcludeFromCalculation__c = false
                            AND FiscalYear__c = :queryParam.getFiscalYear()
                            AND FYMonth__c IN : queryParam.getMonthsInQrtr()
                            AND ForecastOwner__c IN :queryParam.getOwnerIds()
                            Order By ForecastOwner__c
                      ];

         return lineItemsData;
     }


    /**
     * gets all the quota amount by Year, Month and owners results used by
     * SpecialistForecastController to display quota amount for AE
     * @return TODO
     */
    public static AggregateResult[] getSpecialistForecastQuota(
    ForecastReportQueryParam queryParam) {
        AggregateResult[] quotasListData;
        quotasListData = [SELECT SpecialistForecastuser__c,
                        FiscalYear__c,
                        sum(QuotaAmount__c) amnt
              FROM SpecialistForecastQuota__c
                        WHERE FiscalYear__c = :queryParam.getFiscalYear()
                        AND FYMonth__c IN : queryParam.getMonthsInQrtr()
                        AND SpecialistForecastuser__c IN :queryParam.getOwnerIds()
                        GROUP BY  SpecialistForecastuser__c,FiscalYear__c
                        ORDER BY SpecialistForecastuser__c
                      ];

         return quotasListData;
    }

    /**
     *  A common FLS check for Company Specialist Forecasting
     *  @return
     */
    public static Boolean checkFieldsSplForecastRepAccessibility() {
        return (Schema.SObjectType.SpecialistForecast__c.isAccessible() &&
            Schema.SObjectType.SpecialistForecastLineItem__c.isAccessible() &&
            Schema.SObjectType.SpecialistForecastQuota__c.isAccessible() &&
            Schema.SObjectType.SpecialistForecastOverride__c.isAccessible());
    }


     /**
      *  This method is used to check access for the page exists for data sync
      *  @return TODO
      */
     public static Boolean checkForecastDataSyncAccessForCurrentUser() {
        boolean hasAccess = true;

        Map<String, Schema.SObjectField> mForecast =
            Schema.SObjectType.ForecastHierarchyConfigurations__c.fields.getMap();

        String [] accessFields = new String [] {'checkAccessToSyncJob__c'};
        if (mForecast == null || mForecast.size() == 0) {
            hasAccess = false;
        }
        // Check if the user has Read access for the field
        for (String fieldToCheck : accessFields) {
            if (mForecast .get(fieldToCheck) == null ||
            !mForecast.get(fieldToCheck).getDescribe().isAccessible()) {
                hasAccess = false;
            }
        }

        return hasAccess;

    }
    /**************************************************************************/

    /**************************************************************************/
    /**               FISCAL YEAR CALCUATION UTIL METHODS                    **/
    /**************************************************************************/

    /**                     STRING BASED METHODS                             **/
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

        return new String[] { MonthUtil.getAll()[inpDate.month() - 1],
                              'FY' + MonthUtil.getFYFromDate(inpDate) };
    }

}
