/**
 * Trigger for ForecastHierarchyConfigurations
 *
 * @author mkatragadda
 * @since 180.store
 */
trigger ForecastHierarchyConfigurationsTrigger on ForecastHierarchyConfigurations__c bulk (before insert, before update) {

    String strHash;
    Map<String, ForecastHierarchyConfigurations__c> mapForDupe = new Map<String, ForecastHierarchyConfigurations__c>();
    if (Trigger.isBefore) {
        if (Trigger.isInsert || Trigger.isUpdate) {
            for (ForecastHierarchyConfigurations__c forecastConfigurations : Trigger.new) {
                if (forecastConfigurations.HierarchyRoleUser__c == null || forecastConfigurations.HierarchyRole__c == null) {
                    forecastConfigurations.addError('Validation#1. User and Role is required.');
                }
                if (forecastConfigurations.IsActive__c) {
                    //duplicate check
                    strHash = HierarchyUtil.createHash(forecastConfigurations);
                    if (mapForDupe.get(strHash) == null) {
                        forecastConfigurations.HashForCheck__c = strHash;
                        mapForDupe.put(strHash, forecastConfigurations);
                    } else {
                        forecastConfigurations.addError('Validation#2. Error while inserting or updating a duplicate record.');
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
                mapForDupe.get(hashCfr.HashForCheck__c).addError('Validation#3. Duplicate record exists in database where Role = ' + hashCfr.HierarchyRole__c + ' and User = ' + hashCfr.HierarchyRoleUser__r.id);
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



