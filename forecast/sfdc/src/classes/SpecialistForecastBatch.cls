/**
 * Batch job for processing Specialist Forecast amounts to Specialist Forecast Reports
 *
 * To Run this Class from the Console:
 *
 * @author auzzaman,smohapatra
 * @since 180.store
 */

global with sharing class SpecialistForecastBatch implements
    Database.Batchable<AggregateResult> {

    global Map<String, AggregateResult[]> input {get; set;}
    global Map<String, SpecialistForecastLineItem__c> processedRecords {get; set;}
    public DateTime starttime = System.now();
    public Boolean isRunningByUser = false;

    /**
     *
     * @return TODO
     */
    global Iterable<AggregateResult> start(Database.BatchableContext context) {
        /* FIXME: Remove before prod! */
        System.debug('Start SpecialistForecastBatch ' + System.now());
        return new ForecastCollection();
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
     * @return TODO
     */
    global void execute(Database.BatchableContext context, List<Sobject> items) {
        /* FIXME: Remove before prod! */
        System.debug('Running SpecialistForecastBatch ' + System.now());
        /* FIXME: Remove before prod! */
        System.debug('items.size() = ' + items.size());
        // upsert to SpecialistForecastLineItem  based on input map.
        List<SpecialistForecastLineItem__c> allLineItems = new
            List<SpecialistForecastLineItem__c>();

        SpecialistForecastLineItem__c item;
        processedRecords = new Map<String, SpecialistForecastLineItem__c>();

        String previousKey;
        String temp;
        String external_Key;

        for (Sobject so : items)  {
            AggregateResult ar = (AggregateResult) so;
            // process the results
            external_Key = (String) ar.get('OwnerId') +
                (String) ar.get('FiscalYear__c') + (String) ar.get('FYMonth__c');
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
            item.FiscalYear__c = (String)ar.get('FiscalYear__c');
            temp = (String) ar.get('FYMonth__c');
            item.FYMonth__c =  temp;
            temp = (String) ar.get('ForecastStatus__c');
            Decimal tempAmount = (Decimal) ar.get('amnt');

            if (temp == 'Closed Won' || temp == 'Closed' ) {
                item.OriginalClosedAmount__c = tempAmount;
            } else  if(temp == 'Commit') {
                item.OriginalCommitAmount__c = tempAmount;
            } else  if(temp == 'Best Case') {
                item.OriginalBestCaseAmount__c = tempAmount;
            } else  if(temp == 'Pipeline') {
                item.OriginalPipelineAmount__c = tempAmount;
            }

            item.ExtIdentifier__c = external_Key;
            /* FIXME: Remove before prod! */
            System.debug(item + 'externalkey' + external_Key);

            processedRecords.put(external_Key,item);
            //check if
            if (previousKey != external_Key){
                previousKey = external_Key;
                allLineItems.add(item);
            }
        }


        /* FIXME: Remove before prod! */
        System.debug('1. processedRecords.size() = ' + processedRecords.size());
        /* FIXME: Remove before prod! */
        System.debug('2. allLineItems.size() = ' + allLineItems.size());

        // now upsert at one shot
        try {
            Schema.SObjectField fld = SpecialistForecastLineItem__c.Fields.ExtIdentifier__c;
            Database.upsert(allLineItems, fld, false);

        } catch(DMLException e) {
            String email = ForecastHelper.getCronEmail();
            if(email != ForecastHelper.DO_NOT_SEND_EMAIL) {
                sendErrorNotification('Error in the Specialist Forecast Batch Process',
                    'Specialist Forecast Batch Process not completed Successfully'
                        + e.getMessage(), email);
            }
            /* FIXME: Remove before prod! */
            System.debug(e.getMessage());
        }
    }

    /**
     *
     * @return TODO
     */
    global void finish(Database.BatchableContext context) {
        /* FIXME: Remove before prod! */
        System.debug('Finish SpecialistForecastBatch ' + System.now());
        updateSFLineItems();
        String email = ForecastHelper.getCronEmail();
        User usr = [SELECT EMail FROM User WHERE Id=:UserInfo.getUserId() limit 1];
        if (isRunningByUser) {
            email = usr.EMail;
        }


        if (email != ForecastHelper.DO_NOT_SEND_EMAIL) {
            sendErrorNotification('SpecialistForecastBatch Process Completed',
                'Specialist Forecast Batch Process has completed', email);
        }

    }

    /*
     * update all the line items amount to 0 those are not updated by
     * current process
     */
    private void updateSFLineItems(){
        List<SpecialistForecastLineItem__c> spllist = [SELECT Id,
                            OriginalPipelineAmount__c, OriginalClosedAmount__c,
                            OriginalBestCaseAmount__c, OriginalCommitAmount__c
                            FROM SpecialistForecastLineItem__c
                            WHERE LastModifiedDate < :starttime];

        for (SpecialistForecastLineItem__c item : spllist) {
              item.OriginalClosedAmount__c   = 0;
              item.OriginalCommitAmount__c   = 0;
              item.OriginalBestCaseAmount__c = 0;
              item.OriginalPipelineAmount__c = 0;
        }
         // TODO- store the update() in UpdateResult[] and log the error messages if any record failed update.
         Database.update(spllist,false);



    }
}
