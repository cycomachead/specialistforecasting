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
 * Class Name : TestSpecialistForecastBatch
 * Description: Contains test cases to test utility methods for special
 * forecast batch processor
 */

@isTest(SeeAllData = true)
public with sharing class SpecForecastBatchTest {

    /*
    Method Name: testBatch
    Parameters : None
    Description: f-test utility that is used to assert special forecast batch processor
    */
    private static testMethod void testBatch() {
        Test.startTest();
        Integer count = [SELECT count() FROM SpecialistForecast__c];
        count = (count < 1) ? 1 : count;
        Id batchProcessId = Database.executeBatch(new SpecialistForecastBatch(), count);
        Test.stopTest();
        System.debug('specialistForecastBatchID = ' + batchProcessId);

        System.assert(batchProcessId != null);
        AsyncApexJob aaj = [SELECT Id, Status, JobItemsProcessed, TotalJobItems, NumberOfErrors
            FROM AsyncApexJob WHERE Id = :batchProcessId];
        System.debug('aaj for SpecialistForecastBatch = ' + aaj);

    }

    /*
    Method Name: testSubmitBatch
    Parameters : None
    Description: f-test utility that is used to assert special forecast submit batch processor
    */
    private static testMethod void testSubmitBatch() {
        Test.startTest();
        Id batchProcessId = Database.executeBatch(new SpecialistForecastSubmitBatch(), 20);
        Test.stopTest();
        System.debug('specialistForecastSubmitBatchID = ' + batchProcessId);

        System.assert(batchProcessId != null);


    }

}
