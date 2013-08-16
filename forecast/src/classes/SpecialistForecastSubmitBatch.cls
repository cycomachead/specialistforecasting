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
 *  Batch job for processing Specialist Forecasting
 *  This job sets the Submitted Date of of Line Items to null.
 *
 */
global with sharing class SpecialistForecastSubmitBatch implements
Database.Batchable<SpecialistForecastLineItem__c> {

    private static final String SUCCESS_SUBJ =
        'Specialist Forecast Submit date batch Process Completed';
    private static final String SUCCESS_BODY =
        'Specialist Forecast Submitted date to null Batch Process has completed';
    private static final String ERROR_SUBJ =
        'Specialist Forecast Submit Date Batch Process Had Errors';
    private static final String ERROR_BODY =
        'The batch process to set submission dates to null had errors.\n\n';

    /** */
    global SpecialistForecastLineItem__c[] lineItemRecords { get;set; }

    /**
     *  Grab all lineItems with a submission date.
     */
    global SpecialistForecastLineItem__c[] start(
    Database.BatchableContext context) {
        try {
            lineItemRecords = [SELECT Id,
                                      SubmittedDate__c
                                FROM SpecialistForecastLineItem__c
                                WHERE SubmittedDate__c != null];
        } catch (Exception ex) {
            ForecastHelper.sendNotification(ERROR_SUBJ, ERROR_BODY +
                ex.getMessage(), ForecastHelper.getCronEmail());
        }
        return lineItemRecords;
    }

    /**
     *  For all line items, set the SubmittedDate__c to null
     */
    global void execute(Database.BatchableContext context,
    SpecialistForecastLineItem__c[] items) {
        SpecialistForecastLineItem__c[] updatebleRecords = new
            SpecialistForecastLineItem__c[0];

        for (SpecialistForecastLineItem__c line : items) {
            line.SubmittedDate__C = null;
            updatebleRecords.add(line);
        }

        // now upsert at one shot
        try {
             UPDATE updatebleRecords;
        } catch (DMLException ex) {
            ForecastHelper.sendNotification(ERROR_SUBJ, ERROR_BODY +
                ex.getMessage(), ForecastHelper.getCronEmail());
        }
    }

    /**
     *  Send an email notification when the batch successful completes
     */
    global void finish(Database.BatchableContext context) {
        ForecastHelper.sendNotification(SUCCESS_SUBJ, SUCCESS_BODY,
            ForecastHelper.getCronEmail());
    }

}
