/**
 * Controller Class used for Specialist Forecast override popup page
 *
 * @author smohapatra
 * @since 180.store
 */
public with sharing class SpecialistForecastOverrideController {

    public Decimal overrideAmount{get;set;}
    public Decimal originalAmount{get;set;}
    public Decimal originaloldAmount{get;set;}
    public String overrideComment{get;set;}
    public String strRecordId {get;set;}
    public String strField {get;set;}
    public boolean pageRefresh{get;set;}
    public String lineItemownerID {get;set;}
    public String strFY {get;set;}
    public String strFQ {get;set;}

    //Constructor
    public SpecialistForecastOverrideController () {
        /* if(System.currentPageReference().getParameters().get('token')!=null)
            {
         String temp = EncodingUtil.base64Decode(System.currentPageReference().getParameters().get('token')).toString();

         originalAmount = decimal.valueOf(temp);
         //originalAmount = decimal.valueOf(System.currentPageReference().getParameters().get('token'));
            }
        */
        String temp = System.currentPageReference().getParameters().get('Amt');

        if (temp != null) {
            temp = String.escapeSingleQuotes(temp);

            try {
                  originalAmount = decimal.valueOf(temp);
            } catch (Exception ex) {
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,'Invalid Override Amount'));
            }
        }

        String oldamt = System.currentPageReference().getParameters().get('orgamt');

        if (oldamt != null) {
            oldamt = String.escapeSingleQuotes(oldamt);

            try {
                originaloldAmount = decimal.valueOf(oldamt);
            } catch (Exception ex) {

            }
        }

        strRecordId = System.currentPageReference().getParameters().get('RecId');

        if (strRecordId != null) {
            strRecordId = String.escapeSingleQuotes(strRecordId );
        }
        strField = System.currentPageReference().getParameters().get('field');
        if (strField != null) {
            strField = String.escapeSingleQuotes(strField);
        }
        lineItemownerID = System.currentPageReference().getParameters().get('ownerId');
        if (lineItemownerID != null) {
            lineItemownerID = String.escapeSingleQuotes(lineItemownerID );
        }
        strFQ = System.currentPageReference().getParameters().get('fq');
        if (strFQ != null) {
            strFQ = String.escapeSingleQuotes(strFQ);
        }
       strFY = System.currentPageReference().getParameters().get('fy');
       if (strFY != null) {
           strFY = String.escapeSingleQuotes(strFY);
       }
    }

     public pagereference saveOverride() {

        SpecialistForecastLineItem__c spc = new SpecialistForecastLineItem__c();
        SpecialistForecastOverride__c spovr = new SpecialistForecastOverride__c();
        String external_Key;
        if (strField == 'mycommit') {

            if (isSelfOverride()) {
                spovr.OwnOverridenCommitAmount__c = overrideAmount;
                spovr.OwnOverrideNotes__c = overridecomment;
            } else {
                spovr.ManagerOverridenCommitAmount__c = overrideAmount;
                spovr.ManagerOverrideCommitNotes__c = overridecomment;

            }
            spovr.PreviousDirectRepCommitAmount__c = originaloldAmount;
        }else if(strField =='mybestcase'){

             if(isSelfOverride()){
                spovr.OwnOverridenBestCaseAmount__c = overrideAmount;
                spovr.OwnOverrideNotes__c = overrideComment;
            }else{
                spovr.ManagerOverridenBestCaseAmount__c = overrideAmount;
                spovr.ManagerOverrideBestcaseNotes__c = overrideComment;

            }
           spovr.PreviousDirectRepBestcaseAmount__c = originaloldAmount;
        }
        spovr.PeriodType__c = strFQ;
        external_Key = lineItemownerID + strFY + strFQ;
        spovr.ExtIdentifier__c = external_Key;
        spovr.OwnerId = lineItemownerID;

        try{
            Upsert spovr ExtIdentifier__c;
         }catch(System.DMLException ex){

             ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,' Failed to update the Override value'));
            return null;
        }

        //upsert Line item
        List<String> lstMonths = ForecastHelper.getMonthsInQuarter(strFQ);

        spc.ExtIdentifier__c = lineItemownerID + strFY + lstMonths.get(0);
        spc.SpecialistForecastOverride__c = spovr.Id;
        spc.FYMonth__c = lstMonths.get(0);
        spc.ForecastOwner__c = lineItemownerID;
        spc.FiscalYear__c = strFY;
        spc.OwnerId = lineItemownerID;
        try {
            Upsert spc ExtIdentifier__c;
         } catch(System.DMLException ex) {
             ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,' Failed to update the Override Amount'));
             return null;
        }

        ApexPages.addmessage(new ApexPages.message(ApexPages.severity.CONFIRM,'Override Amount updated sucessfully'));
        return null;
     }

     private boolean isSelfOverride(){
         Boolean blnoverride = false;
         if (UserInfo.getUserId() == lineItemownerID){
             blnoverride = true;
         }
         return blnoverride;
     }
}
