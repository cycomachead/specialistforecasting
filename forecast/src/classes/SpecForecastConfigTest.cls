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
 *  Class for testing forecast configurations custom settings
 *  NOTE: There aren't many tests here because configurations in custom settings
 *  can change very easily. You should modify these tests as you modify your own
 *  custom settings
 *  A working example test is provided that uses some basic data.
 */

@isTest(SeeAllData = true)
public with sharing class SpecForecastConfigTest {

    // Fill in SFCast Custom Settings.
    private static testMethod void createTestData() {
        SObject[] settings = [SELECT Id FROM ForecastConfigurations__c limit 20];
        DELETE settings;
        // New forecast config objects
        // true forces an upsert of data.
        ForecastHelper.setupBatchJobs(true);

    }

    // Test the data Retrieval from ForecastConfigurations custom settings
    private static testMethod void testRetrieveForecastConfigurations() {
        createTestData();
        Map<String, String> batches =
            RetrieveForecastConfigurations.getForecastConfigurationsByName(
                SpecForecastTestLabel.SF_BATCH);
        if (batches == null) {
            System.assert(false, 'No Batch Configuration Found');
        }
        System.assert(batches.get(SpecForecastTestLabel.CONFIG_VAL).contains(
            SpecForecastTestLabel.VALUE_YES));
        System.assert(batches.get(SpecForecastTestLabel.CONFIG_CAT).contains(
            SpecForecastTestLabel.CAT_STORE_BATCH_JOB));
        System.assert(batches.get(SpecForecastTestLabel.ACTIVE) == 'true');
        System.assert(batches.get(SpecForecastTestLabel.COMMENT) ==
            SpecForecastTestLabel.BATCH_COMMENT);
    }

    // Like the first method, but test for the submit batch properties.
    // Simply a double-check. Both methods are identical.
    private static testMethod void testRetrieveSubmitBatchConfig() {
        createTestData();
        Map<String, String> batches =
            RetrieveForecastConfigurations.getForecastConfigurationsByName(
                SpecForecastTestLabel.SUBMIT_BATCH);
        if (batches == null) {
            System.assert(false, 'No Batch Configuration Found');
        }
        System.assert(batches.get(SpecForecastTestLabel.CONFIG_VAL).contains(
            SpecForecastTestLabel.VALUE_YES));
        System.assert(batches.get(SpecForecastTestLabel.CONFIG_CAT).contains(
            SpecForecastTestLabel.CAT_STORE_BATCH_JOB));
        System.assert(batches.get(SpecForecastTestLabel.ACTIVE) == 'true');
        System.assert(batches.get(SpecForecastTestLabel.COMMENT) ==
            SpecForecastTestLabel.BATCH_COMMENT);
    }

}
