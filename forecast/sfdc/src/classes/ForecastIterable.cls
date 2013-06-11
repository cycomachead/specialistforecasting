/**
 * Custom Iterator for Specialist Forecast Batch Job
 *
 * @author auzzaman
 * @since 180.store
 */
global with sharing class ForecastIterable implements Iterator<AggregateResult> {

    global Integer count {get; set;}
    global AggregateResult[] ownerMap {get; set;}

    global ForecastIterable() {
        count = 0;
//      List<String> supportFYs = ForecastHelper.getSupportedFiscalYearsForForecast();
        ownerMap = [SELECT OwnerId, ForecastStatus__c, FiscalYear__c, FYMonth__c,
                     SUM(ForecastAmount__c) amnt FROM SpecialistForecast__c
                    WHERE  ExcludeFROMCalculation__c != true Group By OwnerId,
                    ForecastStatus__c, FiscalYear__c, FYMonth__c Order by OwnerID,
                    FiscalYear__c, FYMonth__c];
    }

    global boolean hasNext() {
        return count < ownerMap.size();

//       if(count >= ownerMap.size()) {
//             return false;
//         } else {
//             return true;
//         }
    }

    global AggregateResult next() {
        return ownerMap[count++];
    }

}
