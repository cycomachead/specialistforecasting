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
 *  Scheduler to use for AppStore
 *
 *  To run it FROM developer console, use the below code snippet
 *  String sch = '0 50 * * * ?';
 *  System.schedule('AppStore Scheduler Job', sch, new StoreScheduler());
 *  System.debug('\n\n ==> Done');
 *
 *  @author auzzaman
 *  @since 180.store
 */
global with sharing class StoreScheduler implements Schedulable {

    global Integer BATCH_SIZE = 2000;

    private static final String SF_BATCH     = ForecastHelper.SF_BATCH;
    private static final String VALUE_YES    = ForecastHelper.VALUE_YES;
    private static final String SUBMIT_BATCH = ForecastHelper.SUBMIT_BATCH;

    global void execute(SchedulableContext sc) {
        Map<String, String> jobs = ForecastHelper.getBatchJobs();

        if (jobs.containsKey(SF_BATCH) && jobs.get(SF_BATCH) == VALUE_YES) {
            Id batchProcessId = Database.executeBatch(
                new SpecialistForecastBatch(), BATCH_SIZE);
            AsyncApexJob aaj = [SELECT Id, Status, JobItemsProcessed,
                TotalJobItems, NumberOfErrors
                    FROM AsyncApexJob WHERE Id = :batchProcessId];
        }

        if (jobs.containsKey(SUBMIT_BATCH) && jobs.get(SUBMIT_BATCH) == VALUE_YES
        && system.now().format('EEEE') == ForecastHelper.getCronRunDay()) {
            Id batchProcessId = Database.executeBatch(new SpecialistForecastSubmitBatch(), BATCH_SIZE);
            AsyncApexJob aaj = [SELECT Id, Status, JobItemsProcessed,
                TotalJobItems, NumberOfErrors
                    FROM AsyncApexJob WHERE Id = :batchProcessId];
        }
    }

}
