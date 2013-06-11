/**
 *
 *
 */

public with sharing class SplstForecastDrildownOptyController {
    public List<SpecialistForecast__c> specialistforecastList{get;set;}

     /**********Constructor***********/
    public SplstForecastDrildownOptyController(){
         String SELECTedFQ =System.currentPageReference().getParameters().get('fq');
         String SELECTedFY= System.currentPageReference().getParameters().get('fy');
         Id SELECTedOwner= System.currentPageReference().getParameters().get('oId');
         String status= System.currentPageReference().getParameters().get('st');

         SELECTedFQ =(SELECTedFQ !=null?String.escapeSingleQuotes(SELECTedFQ ):null);
         SELECTedFY=(SELECTedFY!=null?String.escapeSingleQuotes(SELECTedFY):null);
         SELECTedOwner=(SELECTedOwner!=null?String.escapeSingleQuotes(SELECTedOwner):null);
         status=(status!=null?String.escapeSingleQuotes(status):null);
         List<String> lstStatus = new List<String>();
         lstStatus.add(status);
         if(status==ForecastHelper.SUBTYPE_CLOSED)
         lstStatus.add('Closed Won');
         if(SELECTedFQ !=null && SELECTedFY!=null&& SELECTedOwner!=null ) {
                 this.getSpecialistForecastRecords(SELECTedFY,SELECTedFQ,SELECTedOwner,lststatus);
         }
    }

    public void getSpecialistForecastRecords(String fy,String fQuarter,Id ownerId,List<String> lststatus){
        //get months by quarter
        List<String> lstMonths = ForecastHelper.getMonthsInQuarter(fQuarter);
        specialistforecastList=[SELECT Opportunity__C,Opportunity__r.Name,
                                    Account__c,FiscalYear__c,FYMonth__c,
                                    ForecastAmount__c,ForecastStatus__c,
                                    ForecastCloseDate__c,ForecastStage__c
                                    FROM SpecialistForecast__c
                                    WHERE FiscalYear__c=:fy
                                    AND FYMonth__c in:lstMonths
                                    AND OwnerId=:ownerId
                                    AND ForecastStatus__c in :lststatus];
    }
}
