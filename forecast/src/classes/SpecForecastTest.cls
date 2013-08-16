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
public with sharing class SpecForecastTest {

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
            FiscalYear__c = 'FY13', Month__c = 'August', OwnerId = u.Id);

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
            FiscalYear__c = 'FY10', Month__c = 'March', OwnerId = u.Id);

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
    Method Name: testgetAllQuotas
    Parameters : None
    Description: f-test utility that is used to assert special forecast getAllQuotas with the user and entry data
    */
    private static testMethod void testgetAllQuotas() {
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
            String[] searchMons = ForecastHelper.getMonthsInQuarter('Q3');
            //get Quota information for all the users
            AggregateResult[] listSplFCastQuotas = ForecastHelper.getAllQuotas(
                'FY13', searchMons, ownerIdsforQuery);

            for (Sobject so : listSplFCastQuotas)  {
                AggregateResult ar = (AggregateResult) so;
                Id OwnerId = (Id) ar.get('SpecialistForecastUser__c');
                Decimal tempAmount = (Decimal) ar.get('amnt');
                System.assert(tempAmount != null);
            }
        }
    }

    /** Tests the total line for null values. */
    private static testMethod void testTotalLine() {
        deleteAll();
        SpecialistForecastController cont = new SpecialistForecastController();
        SFcastLineItem total = cont.getTotalLine();
        System.assert(total != null, 'Total Line shouldn\'t be null');
        System.assertEquals(total.userName, SpecForecastTestLabel.GRAND_TOTAL);
    }

    /** Test getting all items with no data. */
    private static testMethod void getWOData() {
        SpecialistForecastController cont;
        SFcastLineItem[] items;
        String ERROR_MSG = SpecialistForecastException.USER_MISSING_ID;
        deleteAll();
        // User that can't access Specialist Forecasts
        User u = [SELECT Id FROM User Where UserRoleId = '' limit 1];
        System.runAs(u) {
            cont = new SpecialistForecastController();
            items = cont.getSFcastLineItemsData();
            Boolean exists = false;
            for (ApexPages.Message msg : ApexPages.getMessages()) {
                if (msg.getDetail().contains(ERROR_MSG)) { exists = true; }
            }
            System.assert(exists);
            System.assert(items == null);
        }
        // Test for a User with a roleID
        u = [SELECT Id FROM User Where UserRoleId != '' limit 1];
        System.runAs(u) {
            cont = new SpecialistForecastController();
            items = cont.getSFcastLineItemsData();
            System.assert(items != null);
            System.assert(items.size() >= 1);
        }
        System.assert(true);
    }

    /** This is merely to improve test coverage. These shouldn't be tested...*/
    private static testMethod void testDefaultValue() {
        Integer num;
        Decimal dec;
        String  str;
        Boolean boo;

        System.assertEquals(0, SpecialistForecastController.defaultValue(num));
        System.assertEquals(0.0, SpecialistForecastController.defaultValue(dec));
        System.assertEquals('', SpecialistForecastController.defaultValue(str));
        System.assertEquals(false, SpecialistForecastController.defaultValue(boo));
        num = 42;
        dec = 3.1415926;
        str = 'Mmm. Pi';
        boo = true;
        System.assertEquals(42, SpecialistForecastController.defaultValue(num));
        System.assertEquals(3.1415926,
            SpecialistForecastController.defaultValue(dec));
        System.assertEquals('Mmm. Pi',
            SpecialistForecastController.defaultValue(str));
        System.assertEquals(true, SpecialistForecastController.defaultValue(boo));
        System.assert(true);
    }

    /** Testing the simple boolean getter methods. */
    public static testMethod void testGetBooleans() {
        deleteAll();
        SpecialistForecastController cont = new SpecialistForecastController();
        cont.setCanOverride();
        System.assert(cont.canOverride, 'Can override on new controller');
        System.assert(!cont.getCanGoBack(), 'Cant Go Back Yet');
        System.assert(cont.getCanSubmit(), 'Can\'t submit');
        // Test where user definitely has no access.
        User u = [SELECT Id FROM User Where UserRoleId = '' limit 1];
        System.runAs(u) {
            cont = new SpecialistForecastController();
            System.assert(cont.getViewAccessToRefreshButton(), 'Can\'t Refresh');
        }
    }

    /** Test the Recents list. */
    public static testMethod void testRecentsList() {
        SpecialistForecastController cont = new SpecialistForecastController();
        SelectOption[] recents = cont.getRecentsList();
        System.assert(recents != null);
        System.assert(recents.size() == 0);
        cont.setRecentsList();
        recents = cont.getRecentsList();
        System.assert(recents.size() == 1);
        for (Integer i = 0; i < 20; i += 1) {
            // override the add method to have 20 items.
            cont.recentsList.add(new SelectOption(String.valueOf(i), String.valueOf(i)));
        }
        System.assert(recents.size() == 21);
        // now trigger a removal.
        cont.setRecentsList();
        recents = cont.getRecentsList();
        System.assertEquals(recents.size(), 20);
    }

    /** Test the prepared by methods for user names. */
    public static testMethod void testPrepBy() {
        SpecialistForecastController cont = new SpecialistForecastController();
        // Testing values for the current user/forecast owner.
        System.assertEquals(UserInfo.getName() + (UserInfo.getName().endsWith('s') ? '\'' : '\'s'), cont.preparedByUser);
        // If owner, 'first name' is my.
        System.assertEquals('My', cont.preparedByName);
        User u = [SELECT Id, Name, FirstName FROM User WHERE Id != :UserInfo.getUserId() limit 1];
        // Update the preparedBy Strings
        cont.setPreparedBy(u.Id);
        // Testing values for the current user/forecast owner.
        System.assertEquals(u.Name + (u.Name.endsWith('s') ? '\'' : '\'s'), cont.preparedByUser);
        // If owner, 'first name' is my.
        System.assertEquals(u.FirstName + (u.FirstName.endsWith('s') ? '\'' : '\'s'), cont.preparedByName);
    }

    /** Test the ability to get user wrapper details. */
    public static testMethod void testUserWrapper() {
        String uid = UserInfo.getUserId();
        SpecialistForecastController cont = new SpecialistForecastController();
        // Object uw = SpecialistForecastController.getUserInfo(uid);
        //System.assert(uw.Name == UserInfo.getName());
        //System.assert(uw.uid == uid);
        //System.assert(uw.firstName == UserInfo.getFirstName());
    }

    /** Simple test of SFcastLineItem methods for empty cases. */
    public static testMethod void lineItemTest() {
        SFcastLineItem li = new SFcastLineItem();
        // Test for default values
        // userRole name looks odd, but does need the space.
        System.assertEquals(' ()', li.getUserRoleName());
        System.assertEquals('0.00', li.getPrevVFDirectRepCommitAmount());
        System.assertEquals('0.00', li.getPrevVFDirectRepBestcaseAmount());
        System.assertEquals(false, li.getCommitPreviousAmountMatch());
        System.assertEquals(false, li.getBestCasePreviousAmountMatch());
    }

    /** This is a test to check for popups for overrides and drilldowns. */
    public static testMethod void testPopups() {
        SpecialistForecastController cont = new SpecialistForecastController();
        // Simulate clicking on forecast owner's row.
        cont.lineItemOwnerId = cont.sfLI.ForecastOwner__c;
        System.assert(cont.isSelfOverride());
        // No popup's displayed by default.
        System.assert(!cont.displayPopup);
        System.assert(!cont.displayDrilDownPopup);
        cont.showOverridePopup();
        System.assert(cont.displayPopup);
        cont.closeOverridePopup();
        System.assert(!cont.displayPopup);
        cont.showDrillDownPopup();
        System.assert(cont.displayDrilDownPopup);
        cont.closeDrillDownPopup();
        System.assert(!cont.displayDrilDownPopup);
        cont.initializeOverrideVariables();
        System.assertEquals('', cont.lineItemOwnerId);
    }

    /**************************************************************************/
    /** Detele all forecast records and line items from org. */
    private static void deleteAll() {
        SpecialistForecast__c[] fcasts = [SELECT id FROM SpecialistForecast__c];
        DELETE fcasts;
        SpecialistForecastLineItem__c[] li = [SELECT id FROM SpecialistForecastLineItem__c];
        DELETE li;
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
        return [SELECT Id FROM User WHERE Id = :UserInfo.getUserId() limit 1];
    }

}
