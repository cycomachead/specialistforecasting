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
 *  Simple labels used for testing data, mostly string comparisons
 */
@isTest
public with sharing class SpecForecastTestLabel {

    public static final String GRAND_TOTAL = 'Total';

    public static final String HIERARCHY_TYPE_ROLE = 'ROLE';
    public static final String HIERARCHY_TYPE_USER = 'USER';
    
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
    public static final String COMMIT_MSG  = 'Commit';
    public static final String PIPELINE    = 'Pipeline';


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
}

