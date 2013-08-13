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
 * Trigger for ForecastHierarchyConfigurations
 */
trigger ForecastHierarchyConfigurationsTrigger on ForecastHierarchyConfigurations__c bulk (before insert, before update) {

    String strHash;
    Map<String, ForecastHierarchyConfigurations__c> mapForDupe = new Map<String, ForecastHierarchyConfigurations__c>();
    if (Trigger.isBefore) {
        if (Trigger.isInsert || Trigger.isUpdate) {
            for (ForecastHierarchyConfigurations__c forecastConfigurations : Trigger.new) {
                if (forecastConfigurations.HierarchyRoleUser__c == null || forecastConfigurations.HierarchyRole__c == null) {
                    forecastConfigurations.addError('Failed Validation #1. User and Role are required.');
                }
                if (forecastConfigurations.IsActive__c) {
                    //duplicate check
                    strHash = HierarchyUtil.createHash(forecastConfigurations);
                    if (mapForDupe.get(strHash) == null) {
                        forecastConfigurations.HashForCheck__c = strHash;
                        mapForDupe.put(strHash, forecastConfigurations);
                    } else {
                        forecastConfigurations.addError('Failed Validation #2. Error while inserting or updating a duplicate record.');
                    }
                }
            }
        }

        if (Trigger.isInsert) {
            ForecastHierarchyConfigurations__c[] existingCfrs =
                [SELECT HierarchyRole__c, HierarchyRoleUser__r.id, HashForCheck__c
                    FROM ForecastHierarchyConfigurations__c
                    WHERE HashForCheck__c IN : mapForDupe.keySet()];
            for (ForecastHierarchyConfigurations__c hashCfr : existingCfrs) {
                mapForDupe.get(hashCfr.HashForCheck__c).addError('Failed ' +
                    'Validation #3. Duplicate record exists in database where Role is: '
                    + hashCfr.HierarchyRole__c + ', and User is: ' +
                    hashCfr.HierarchyRoleUser__r.id + '.');
            }
        }

        if (Trigger.isInsert || Trigger.isUpdate) {
            // get the roles that are added or updated
            Set<String> newOrUpdatedRoles = new Set<String>();
            for (ForecastHierarchyConfigurations__c forecastConfigurations : Trigger.new) {
                newOrUpdatedRoles.add(forecastConfigurations.HierarchyRole__c);
            }

            // get the list of existing mappings for these roles
            List<ForecastHierarchyConfigurations__c> existingMappings =
                [SELECT HierarchyRole__c, HierarchyRoleUser__r.Id, IsManager__c, IsActive__c, Comment__c
                 FROM ForecastHierarchyConfigurations__c
                 WHERE IsActive__c=true and IsManager__c=true and HierarchyRole__c IN :newOrUpdatedRoles];

            // fetch the list of existing roles from the existing mappings for
            // the newly added/updated roles
            Set<String> existingRoles = new Set<String>();
            for(ForecastHierarchyConfigurations__c config : existingMappings) {
                existingRoles.add(config.HierarchyRole__c);
            }

            for(ForecastHierarchyConfigurations__c forecastConfigurations : Trigger.new) {
                if (forecastConfigurations.IsActive__c && forecastConfigurations.IsManager__c) {
                    if (Trigger.isInsert) {
                        if (existingRoles.contains(forecastConfigurations.HierarchyRole__c) ) {
                            forecastConfigurations.addError('Validation#4. An active User Mapping for this Role already exists.');
                        }
                    }
                    if (Trigger.isUpdate) {
                        if (existingRoles.contains(forecastConfigurations.HierarchyRole__c) && forecastConfigurations.HierarchyRole__c != Trigger.oldMap.get(forecastConfigurations.Id).HierarchyRole__c) {
                            forecastConfigurations.addError('Validation#5. An active User Mapping for this Role already exists.');
                        }
                    }
                }
            }
        }
    }
}



