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
 * Batch job for processing Specialist Forecast amounts to Specialist Forecast Reports
 *
 * To Run this Class FROM the Console:
 *
 * @author smohapatra
 * @since 180.store
 */

global with sharing class SpecialistForecastSubmitBatch implements
    Database.Batchable<SpecialistForecastLineItem__c> {

    public static final String DONT_SEND_EMAIL = ForecastHelper.DONT_SEND_EMAIL;

    /** */
    global List<SpecialistForecastLineItem__c> lineItemRecords {get; set;}

    /**
     *
     * @return
     */
    global List<SpecialistForecastLineItem__c>  start(
    Database.BatchableContext context) {
        lineItemRecords =[SELECT id, SubmittedDate__C FROM
            SpecialistForecastLineItem__c WHERE SubmittedDate__c != null];
        return lineItemRecords;
    }

    /**
     *
     * @return
     */
    global void execute(Database.BatchableContext context,
    List<SpecialistForecastLineItem__c> items) {
        List<SpecialistForecastLineItem__c> updatebleRecords = new
            List<SpecialistForecastLineItem__c>();

        for(SpecialistForecastLineItem__c line : items){
            line.SubmittedDate__C = null;
            updatebleRecords.add(line);
        }

        // now upsert at one shot
        try {
             UPDATE updatebleRecords;
        } catch (DMLException e) {
            String email = ForecastHelper.getCronEmail();
            sendNotification('SpecialistForecast Batch Process Completed',
                'Specialist Forecast submit Batch Process has completed', email);
        }
    }

    /**
     * Send error email notification to the gack email
     * @param String subject
     * @param String formattedBody
     */
    private static void sendNotification(String subject,
    String formattedBody, String gackEmailIds) {
        if (gackEmailIds == DONT_SEND_EMAIL) {
            return;
        }
        Messaging.SingleEmailMessage mail = new Messaging.SingleEmailMessage();

        mail.setToAddresses(new String[]{gackEmailIds});
        mail.setSubject(subject);
        mail.setPlainTextBody(formattedBody);

        try {
            Messaging.sendEmail(new Messaging.Email[] { mail });
        } catch(System.Exception ex) {
        }
    }

    /**
     *
     * @return
     */
    global void finish(Database.BatchableContext context) {
        String email = ForecastHelper.getCronEmail();

        sendNotification('Specialist Forecast Submit date batch Process Completed',
                'Specialist Forecast Submitted date to null Batch Process has completed',
                email);
    }

}
