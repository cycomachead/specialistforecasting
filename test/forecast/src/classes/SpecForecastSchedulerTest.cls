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
 * Description: Contains test cases to test utility methods for special forecast scheduler
 * @since 178.store
 */

public class SpecForecastSchedulerTest {


    /*
    Method Name: testStoreScheduler
    Parameters : None
    Description: f-test utility that is used to assert special forecast store scheduler
    */
    private static testMethod void testStoreScheduler() {
        try{
            String sch = '0 50 * * * ?';
            System.schedule('AppStore Scheduler Job', sch, new StoreScheduler());

            System.Assert(true);
        }catch(Exception e){
            System.Assert(false, 'Test failed, useful info is still TODO.');
        }

    }

    /*
    Method Name: testStartScheduledJob
    Parameters : None
    Description: f-test utility that is used to test startScheduledJob of StoreBatchAdminController.cls
    */
    private static testMethod void testStartScheduledJob() {
        try {
            StoreBatchAdminController storeBatchAdmin = new StoreBatchAdminController();
            storeBatchAdmin.setNotificationMinute('50');
            storeBatchAdmin.startScheduledJob();

            System.Assert(true);
        } catch (Exception e) {
            System.Assert(false);
        }

    }

}
