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
 * Custom Iterator for Specialist Forecast Batch Job
 *
 */
global with sharing class ForecastIterable implements Iterator<AggregateResult> {

    global Integer count { get;set; }
    global AggregateResult[] ownerMap { get;set; }

    global ForecastIterable() {
        count = 0;
        ownerMap = [SELECT OwnerId,
                           ForecastCategory__c,
                           FiscalYear__c,
                           Month__c,
                    SUM(ForecastAmount__c) amnt
                    FROM SpecialistForecast__c
                    WHERE ExcludeFromCalculation__c != true
                    GROUP By OwnerId,
                             ForecastCategory__c,
                             FiscalYear__c,
                             Month__c
                    ORDER by OwnerID,
                             FiscalYear__c,
                             Month__c];
    }

    global boolean hasNext() {
        return count < ownerMap.size();
    }

    global AggregateResult next() {
        return ownerMap[count++];
    }

}
