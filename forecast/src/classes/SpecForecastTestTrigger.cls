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
 *  triggers.
 */

@isTest
public with sharing class SpecForecastTestTrigger {

    // Test Proper Insertion of a SpecialistForecast__c record
    private static testMethod void fcastInsert() {
        deleteAll();
        SpecialistForecast__c sfbad = new SpecialistForecast__c(
            ForecastAmount__c    = 100.00);
        try {
            INSERT sfbad;
            System.assert(false, 'Insert should not pass');
        } catch (Exception ex) {
            System.assert(true, 'Should have errors on insert of bad data');
        }
        Opportunity oppty = new Opportunity(
                     Name = 'Test Oppty',
                CloseDate = Date.newInstance(2013, 8, 16),
                StageName = 'Closed');
        INSERT oppty;
        SpecialistForecast__c sf = new SpecialistForecast__c(
            Opportunity__c       = oppty.Id,
            ForecastCategory__c  = 'Commit',
            ForecastCloseDate__c = Date.newinstance(2013, 08, 16),
            ForecastStage__c     = '06 - Finalizing Closure'
            );
        try {
            INSERT sf;
            System.assert(true, 'Insert should pass');
            // Trigger should automatically popular null data.
            System.assert(sf.ForecastAmount__c == 0);
            System.assert(sf.Month__c == 'August');
            // Could be FY12 or FY13 or FY14 depending on Org settings.
            System.assert(sf.FiscalYear__c != null);
        } catch (Exception ex) {
            System.assert(false, 'no errors on insert of good data');
        }
    }

    // Test proper insertion of a ForecastHierarchyConfigurations__c
    private static testMethod void hierarchyInsert() {
        deleteAll();
        ForecastHierarchyConfigurations__c fhcbad =
            new ForecastHierarchyConfigurations__c();
        try {
            INSERT fhcbad;
            System.assert(false, 'Insert should not pass');
        } catch (Exception ex) {
            System.assert(true, 'Should have errors on insert of bad data');
        }
        ForecastHierarchyConfigurations__c fhc =
            new ForecastHierarchyConfigurations__c(
                HierarchyRole__c = 'CEO',
                IsManager__c = true,
                IsActive__c = true,
                Comment__c = 'no comment',
                HierarchyRoleUser__c = UserInfo.getUserId()
                );
        try {
            INSERT fhc;
            System.assert(true, 'Insert should pass');
        } catch (Exception ex) {
            System.assert(false, 'no errors on insert of good data');
        }
    }

    /** Detele all forecast records and config items from org. */
    private static void deleteAll() {
        SpecialistForecast__c[] fcasts = [SELECT Id FROM SpecialistForecast__c];
        DELETE fcasts;
        ForecastHierarchyConfigurations__c[] fhc =
            [SELECT Id FROM ForecastHierarchyConfigurations__c];
        DELETE fhc;
    }
}
