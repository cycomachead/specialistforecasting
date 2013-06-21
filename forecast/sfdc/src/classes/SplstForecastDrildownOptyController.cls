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
 *
 *
 */
public with sharing class SplstForecastDrildownOptyController {

    public List<SpecialistForecast__c> specialistforecastList{get;set;}

    private static final String CLOSED = ForecastHelper.CLOSED;
    private static final String CLOSED_WON = ForecastHelper.CLOSED_WON;


    /**********Constructor***********/
    public SplstForecastDrildownOptyController() {
         String SelectedFQ = System.currentPageReference().getParameters().get('fq');
         String SelectedFY = System.currentPageReference().getParameters().get('fy');
         Id SelectedOwner = System.currentPageReference().getParameters().get('oId');
         String status = System.currentPageReference().getParameters().get('st');

         selectedFQ = SelectedFQ != null ?
             String.escapeSingleQuotes(SelectedFQ) : null;
         selectedFY = SelectedFY != null ?
             String.escapeSingleQuotes(SelectedFY) : null;
         selectedOwner = SelectedOwner != null ?
             String.escapeSingleQuotes(SelectedOwner) : null;
         status = status != null ? String.escapeSingleQuotes(status) : null;

         List<String> lstStatus = new List<String>();
         lstStatus.add(status);
         if (status == CLOSED) {
             lstStatus.add(CLOSED_WON);
         }
         if (SelectedFQ != null && SelectedFY != null&& SelectedOwner != null ) {
                 this.getSpecialistForecastRecords(SelectedFY, SelectedFQ,
                     SelectedOwner,lststatus);
         }
    }

    public void getSpecialistForecastRecords(String fy, String fq, Id ownerId, 
    String[] lststatus) {
        //get months by quarter
        List<String> lstMonths = ForecastHelper.getMonthsInQuarter(fq);
        specialistforecastList=[SELECT Opportunity__C,Opportunity__r.Name,
                                    Account__c, FiscalYear__c, FYMonth__c,
                                    ForecastAmount__c, ForecastStatus__c,
                                    ForecastCloseDate__c, ForecastStage__c
                                    FROM SpecialistForecast__c
                                    WHERE FiscalYear__c = :fy
                                    AND FYMonth__c in :lstMonths
                                    AND OwnerId = :ownerId
                                    AND ForecastStatus__c in :lststatus];
    }
}
