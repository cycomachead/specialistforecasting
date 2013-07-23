/**
 * Trigger for SpecialistForecast__c
 *
 * @author auzzaman,smohapatra
 * @since 180.store
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
                    specForecast.addError('Validation#SF1. No date supplied.');
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



