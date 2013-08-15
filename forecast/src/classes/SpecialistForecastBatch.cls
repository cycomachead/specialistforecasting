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
 *  Batch job for processing Specialist Forecast amounts to Specialist Forecast
 *  Reports
 *
 */
global with sharing class SpecialistForecastBatch implements
Database.Batchable<AggregateResult> {

    private static final String CLOSED      = ForecastHelper.CLOSED;
    private static final String CLOSED_WON  = ForecastHelper.CLOSED_WON;
    private static final String PIPELINE    = ForecastHelper.PIPELINE;
    private static final String COMMIT_MSG      = ForecastHelper.COMMIT_MSG;
    private static final String BEST_CASE   = ForecastHelper.BEST_CASE;

    private static final String DONT_EMAIL = ForecastHelper.DONT_EMAIL;
    private static final String SUCCESS_SUBJ =
        'Specialist Forecast Batch Process Completed';
    private static final String SUCCESS_BODY =
        'The Specialist Forecast Batch Process has completed successfully.';
    private static final String ERROR_SUBJ   =
        'Error in the Specialist Forecast Batch Process';
    private static final String ERROR_BODY   =
        'Specialist Forecast Batch Process did not complete. \n\n';

    global Map<String, AggregateResult[]> input { get;set; }
    global Map<String, SpecialistForecastLineItem__c> processedRecords { get;set; }
    public Boolean isRunningByUser = false;

    /**
     * @return An iterator of all forecast records
     */
    global Iterable<AggregateResult> start(Database.BatchableContext context) {
        return new ForecastCollection();
    }

    /**
     *  Execute processing of all Line Items
     */
    global void execute(Database.BatchableContext context, SObject[] inpItems) {
        // upsert to SpecialistForecastLineItem based on input map.
        SpecialistForecastLineItem__c[] allLineItems = new
            SpecialistForecastLineItem__c[0];

        SpecialistForecastLineItem__c item;
        processedRecords = new Map<String, SpecialistForecastLineItem__c>();

        String previousKey;

        for (Sobject so : inpItems)  {
            AggregateResult ar = (AggregateResult) so;
            // process the results
            String external_Key = (String) ar.get('OwnerId') +
                (String) ar.get('FiscalYear__c') + (String) ar.get('Month__c');
            if (processedRecords.get(external_Key) == null) {
                 item = new SpecialistForecastLineItem__c();
                 item.OriginalClosedAmount__c   = 0;
                 item.OriginalBestCaseAmount__c = 0;
                 item.OriginalPipelineAmount__c = 0;
                 item.OriginalCommitAmount__c   = 0;
                 processedRecords.put(external_Key, item);
             }

            item = processedRecords.get(external_Key);
            item.ForecastOwner__c = (Id) ar.get('OwnerId');
            item.OwnerId = (Id) ar.get('OwnerId');
            item.FiscalYear__c = (String) ar.get('FiscalYear__c');
            item.Month__c =  (String) ar.get('Month__c');
            String temp = (String) ar.get('ForecastCategory__c');
            Decimal tempAmount = (Decimal) ar.get('amnt');

            if (temp == CLOSED_WON || temp == CLOSED) {
                item.OriginalClosedAmount__c = tempAmount;
            } else if(temp == COMMIT_MSG) {
                item.OriginalCommitAmount__c = tempAmount;
            } else if(temp == BEST_CASE) {
                item.OriginalBestCaseAmount__c = tempAmount;
            } else if (temp == PIPELINE) {
                item.OriginalPipelineAmount__c = tempAmount;
            }

            item.ExtIdentifier__c = external_Key;

            processedRecords.put(external_Key, item);
            if (previousKey != external_Key) {
                previousKey = external_Key;
                allLineItems.add(item);
            }
        }

        try {
            Schema.SObjectField fld = SpecialistForecastLineItem__c.Fields.ExtIdentifier__c;
            Database.upsert(allLineItems, fld, false);

        } catch(DMLException ex) {
            String email = ForecastHelper.getCronEmail();
            ForecastHelper.sendNotification(ERROR_SUBJ, ERROR_BODY +
                ex.getMessage(), email);
        }
    }

    /**
     *  Finish updating / resesting the original values for all forecasts and
     *  notify the user.
     */
    global void finish(Database.BatchableContext context) {
        String email = ForecastHelper.getCronEmail();

        if (isRunningByUser) {
            email = UserInfo.getUserEmail();
        }
        try {
            updateSFLineItems();
            ForecastHelper.sendNotification(SUCCESS_SUBJ, SUCCESS_BODY, email);
        } catch (Exception ex) {
            ForecastHelper.sendNotification(ERROR_SUBJ, ERROR_BODY +
                ex.getMessage(), email);
        }
    }

    /*
     * update all the line items amount to 0 those are not updated by
     * current process
     */
    private void updateSFLineItems() {
        DateTime NOW = System.now();
        SpecialistForecastLineItem__c[] spllist = [SELECT Id,
            OriginalPipelineAmount__c, OriginalClosedAmount__c,
            OriginalBestCaseAmount__c, OriginalCommitAmount__c
            FROM SpecialistForecastLineItem__c
            WHERE LastModifiedDate < :NOW];

        for (SpecialistForecastLineItem__c item : spllist) {
              item.OriginalClosedAmount__c   = 0;
              item.OriginalCommitAmount__c   = 0;
              item.OriginalBestCaseAmount__c = 0;
              item.OriginalPipelineAmount__c = 0;
        }
         // TODO- store the update() in UpdateResult[] and log the error messages if any record failed update.
         Database.update(spllist, false);

    }
}
