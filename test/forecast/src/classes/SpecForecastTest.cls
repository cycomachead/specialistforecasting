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
 *  Description: Contains test cases to test utility methods for special forecast
 *  batch processor
 */

@isTest
public class SpecForecastTest {

    private static testMethod void testGetFiscalYears() {
        SpecialistForecastController specialistController = new SpecialistForecastController();
        SelectOption[] fiscalYears = specialistController.getFiscalYears();
        String optionValue;
        for(SelectOption option : fiscalYears) {
            optionValue = optionValue + option.getValue() + ' ';
        }
        System.assert(optionValue.contains(specialistController.selectedFY));

    }

    /*
    Method Name: testGetFiscalQuarters
    Parameters : None
    Description: f-test utility that is used to assert special forecast getFiscalQuarters
    */
    private static testMethod void testGetFiscalQuarters() {
        SpecialistForecastController specialistController = new
            SpecialistForecastController();
        SelectOption[] fiscalQuarters = specialistController.getFiscalQuarters();
        String optionValue;
        for(SelectOption option : fiscalQuarters) {
            optionValue = optionValue + option.getValue() + ' ';
        }
        System.assert(optionValue.contains(specialistController.selectedFQ));

    }

    /*
    Method Name: testgetSFcastLineItemsData
    Parameters : None
    Description: f-test utility that is used to assert special forecast getSFcastLineItemsData with the user and entry data
    */
    private static testMethod void testgetSFcastLineItemsData() {
        //insert data if not exist
        User u = createOrGetUser();

        SpecialistForecastLineItem__c specItem = new
            SpecialistForecastLineItem__c(OriginalBestCaseAmount__c = 6000,
            FiscalYear__c = 'FY13', FYMonth__c = 'August', OwnerId = u.Id);

        INSERT specItem;

        setUpPageRef('6000', specItem.Id, 'mybestcase', u.Id, 'Q3', 'FY13');
        // This code runs as the system user

        System.runAs(u) {
            // The following code runs as user 'u'
            SpecialistForecastController specialistController = new
                SpecialistForecastController();
            SFcastLineItem[] specialistVO =
                specialistController.getSFcastLineItemsData();
            System.assert(specialistVO.size() > 0); //FIXME
        }
    }

    /*
    Method Name: testgetSFcastLineItemsDataNoEntry
    Parameters : None
    Description: f-test utility that is used to assert special forecast getSFcastLineItemsData with the user and no entry
    */
    private static testMethod void testgetSFcastLineItemsDataNoEntry() {
        //insert data if not exist
        User u = createOrGetUser();

        SpecialistForecastLineItem__c specItem = new
            SpecialistForecastLineItem__c(OriginalBestCaseAmount__c = 5000,
            FiscalYear__c = 'FY10', FYMonth__c = 'March', OwnerId = u.Id);

        INSERT specItem;

        setUpPageRef('5000', specItem.Id, 'mybestcase', u.Id, 'Q1', 'FY10');

        // This code runs as the system user

        System.runAs(u) {
            // The following code runs as user 'u'
            SpecialistForecastController specialistController = new
                SpecialistForecastController();
            List<SFcastLineItem> specialistVO =
                specialistController.getSFcastLineItemsData();
            System.assert(specialistVO.size() > 0);
        }
    }

    /*
    Method Name: testreportDrildownByUserRole
    Parameters : None
    Description: f-test utility that is used to assert special forecast
        reportDrildownByUserRole
    */
    private static testMethod void testreportDrildownByUserRole() {
        User u = createOrGetUser();

        SpecialistForecastLineItem__c specItem = new SpecialistForecastLineItem__c(
            OriginalBestCaseAmount__c = 6000, FiscalYear__c = 'FY13',
            Month__c = 'August', OwnerId = u.Id);
        INSERT specItem;
        setUpPageRef('6000', specItem.Id, 'mybestcase', u.Id, 'Q3', 'FY13');
        System.runAs(u) {
            SpecialistForecastController specialistController = new SpecialistForecastController();

            specialistController.selectedUserId = u.Id;
            PageReference drillDownPage = specialistController.reportDrildownByUserRole();

            System.assert(drillDownPage == null);
        }
    }

    /*
    Method Name: testGetSpecialistForecastQuota
    Parameters : None
    Description: f-test utility that is used to assert special forecast getSpecialistForecastQuota with the user and entry data
    */
    private static testMethod void testGetSpecialistForecastQuota() {
        //insert data if not exist
        User u = createOrGetUser();

        SpecialistForecastLineItem__c specItem =
            new SpecialistForecastLineItem__c(
            OriginalBestCaseAmount__c = 6000,
            FiscalYear__c = 'FY13', Month__c = 'August', OwnerId = u.Id);

        insert specItem;

        setUpPageRef('6000', specItem.Id, 'mybestcase', u.Id, 'Q3', 'FY13');

        // This code runs as the system user

        System.runAs(u) {
            // The following code runs as user 'u'

            SpecialistForecastLineItem__c  sfLI = new SpecialistForecastLineItem__c();
            sfLI.ForecastOwner__c = UserInfo.getUserId();
            Set<Id> ownerIdsforQuery = new Set<Id>();

            ownerIdsforQuery.add(sfLI.ForecastOwner__c);
            //set the query parameters
            ForecastReportQueryParam queryParam = new ForecastReportQueryParam();
            queryParam.setFiscalYear('FY13');
            queryParam.setMonthsInQrtr(ForecastHelper.getMonthsInQuarter('Q3'));
            queryParam.setOwnerIds(ownerIdsforQuery);

            //get Quota information for all the users
            AggregateResult[] listSplFCastQuotas = ForecastHelper.getSpecialistForecastQuota(queryParam );

            for (Sobject so : listSplFCastQuotas)  {
                AggregateResult ar = (AggregateResult) so;
                Id OwnerId = (Id) ar.get('SpecialistForecastUser__c');
                Decimal tempAmount = (Decimal) ar.get('amnt');
                System.assert(tempAmount != null);
            }
        }
    }

    /**
     * set up page reference object for testing
     */
    private static void setUpPageRef(String amt, Id recId, String field,
    Id ownerId, String fq, String fy) {
        PageReference pageRef = new PageReference('/apex/SpecialistForecastPage');
        pageRef.getParameters().put('Amt', amt);
        pageRef.getParameters().put('RecId', recId);
        pageRef.getParameters().put('field', field);
        pageRef.getParameters().put('ownerId', ownerId);
        pageRef.getParameters().put('fq', fq);
        pageRef.getParameters().put('fy', fy);
    }

    /**
     *  Returns the current user who must have permissions to work with forecasts.
     */
    private static User createOrGetUser() {
        return [ select Id from User where Id = :UserInfo.getUserId() ];
    }

}
