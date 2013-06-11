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

    /** */
    global List<SpecialistForecastLineItem__c> lineItemRecords {get; set;}

    /**
     *
     * @return
     */
    global List<SpecialistForecastLineItem__c>  start(
    Database.BatchableContext context) {
        System.debug('Start SpecialistForecastsubmitBatch ' + System.now());
        lineItemRecords =[SELECT ID,SubmittedDate__C FROM
            SpecialistForecastLineItem__c WHERE SubmittedDate__c!=null];
        return lineItemRecords;
    }

    /**
     *
     * @return
     */
    global void execute(Database.BatchableContext context,
    List<SpecialistForecastLineItem__c> items) {
        /* FIXME: Remove before prod! */
        System.debug('Running SpecialistForecastSubmitBatch ' + System.now());
        /* FIXME: Remove before prod! */
        System.debug('items.size() = ' + items.size());

        List<SpecialistForecastLineItem__c> updatebleRecords = new
            List<SpecialistForecastLineItem__c>();

        for(SpecialistForecastLineItem__c line : items){
            line.SubmittedDate__C = null;
            updatebleRecords.add(line);

        }


         // now upsert at one shot
        try {
             update updatebleRecords;

        } catch (DMLException e) {
             String email = ForecastHelper.getCronEmail();
           if (email != ForecastHelper.DO_NOT_SEND_EMAIL) {
            sendErrorNotification('SpecialistForecastBatch Process Completed',
                'Specialist Forecast submit Batch Process has completed', email);
           }
        }
    }


    /**
     * Send error email notification to the gack email
     * @param String subject
     * @param String formattedBody
     */
    private static void sendErrorNotification(String subject,
    String formattedBody, String gackEmailIds) {
        Messaging.SingleEmailMessage mail = new Messaging.SingleEmailMessage();

        mail.setToAddresses(new String[]{gackEmailIds});
        mail.setSubject(subject);
        mail.setPlainTextBody(formattedBody);

        try {
            Messaging.sendEmail(new Messaging.Email[] { mail });
            // Messaging.sendSystemEmail(mail, 'do-not-reply@salesforce.com');
        } catch(System.Exception ex) {
            /* FIXME: Remove before prod!! */
            System.debug('Error in sendMail ' + ex.getMessage());
        }
    }

    /**
     *
     * @return
     */
    global void finish(Database.BatchableContext context) {
        /* FIXME: Remove before prod!! */
        System.debug('Finish SpecialistForecast Submit Batch ' + System.now());
        String email = ForecastHelper.getCronEmail();

        if (email != ForecastHelper.DO_NOT_SEND_EMAIL) {
            sendErrorNotification('Specialist Forecast Submit date batch Process Completed',
                'Specialist Forecast Submitted date to null Batch Process has completed', email);
        }
    }

}
