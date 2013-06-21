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
 * Controller Class used for Specialist Forecast override popup page
 *
 * @author smohapatra
 * @since 180.store
 */
public with sharing class SpecialistForecastOverrideController {

    public Decimal overrideAmount {get;set;}
    public Decimal originalAmount {get;set;}
    public Decimal originaloldAmount {get;set;}
    public String overrideComment {get;set;}
    public String strRecordId {get;set;}
    public String strField {get;set;}
    public Boolean pageRefresh {get;set;}
    public String lineItemownerID {get;set;}
    public String strFY {get;set;}
    public String strFQ {get;set;}

    // Constructor
    public SpecialistForecastOverrideController () {
        String temp = System.currentPageReference().getParameters().get('Amt');

        if (temp != null) {
            temp = String.escapeSingleQuotes(temp);

            try {
                  originalAmount = decimal.valueOf(temp);
            } catch (Exception ex) {
                ApexPages.addmessage(new ApexPages.message(
                    ApexPages.severity.Error,'Invalid Override Amount'));
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
        } else if (strField =='mybestcase') {
            if (isSelfOverride()) {
                spovr.OwnOverridenBestCaseAmount__c = overrideAmount;
                spovr.OwnOverrideNotes__c = overrideComment;
            } else {
                spovr.ManagerOverridenBestCaseAmount__c = overrideAmount;
                spovr.ManagerOverrideBestcaseNotes__c = overrideComment;
            }
           spovr.PreviousDirectRepBestcaseAmount__c = originaloldAmount;
        }

        spovr.PeriodType__c = strFQ;
        external_Key = lineItemownerID + strFY + strFQ;
        spovr.ExtIdentifier__c = external_Key;
        spovr.OwnerId = lineItemownerID;

        try {
            Upsert spovr ExtIdentifier__c;
         } catch(System.DMLException ex) {
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
             ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,
                 'Failed to update the Override Amount'));
             return null;
        }

        ApexPages.addmessage(new ApexPages.message(ApexPages.severity.CONFIRM,
            'Override Amount updated successfully'));
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
