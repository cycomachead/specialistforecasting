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
 * Trigger for SpecialistForecast__c
 */
trigger SpecialistForecastTrigger on SpecialistForecast__c bulk(before insert,
      before update) {

    if (Trigger.isBefore) {
        if (Trigger.isInsert || Trigger.isUpdate) {
            String[] monthAndFiscalYear;
            for (SpecialistForecast__c specForecast : Trigger.new) {
                if (specForecast.OwnerId != specForecast.OwnerCopy__c) {
                    specForecast.OwnerCopy__c = specForecast.OwnerId;
                }
                if (specForecast.ForecastCloseDate__c == null) {
                    specForecast.addError('Please supply a date for the forecast.');
                }
                monthAndFiscalYear =
                     ForecastHelper.extractFYAndMonthFromDate(specForecast.ForecastCloseDate__c);
                specForecast.Month__c = monthAndFiscalYear[0];
                specForecast.FiscalYear__c = monthAndFiscalYear[1];
                if (specForecast.ForecastAmount__c == null ||
                specForecast.ForecastAmount__c < 0) {
                    specForecast.ForecastAmount__c = 0;
                }
                // @TODO consider ForecastStage__c and ForecastCategory__c validation that are done through dependent picklist in UI now
            }
        }
    }

}



