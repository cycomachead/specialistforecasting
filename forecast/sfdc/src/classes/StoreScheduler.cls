/**
 * Scheduler to use for AppStore
 *
 * To run it FROM developer console, use the below code snippet
 * String sch = '0 50 * * * ?';
 * System.schedule('AppStore Scheduler Job', sch, new StoreScheduler());
 * System.debug('\n\n ==> Done');
 *
 * @author auzzaman
 * @since 180.store
 */
global with sharing class StoreScheduler implements Schedulable {

    global Integer BATCH_SIZE = 2000;

    global void execute(SchedulableContext sc) {
        Map<String, String> jobs = ForecastHelper.getBatchJobs();
        /* FIXME: Remove before prod! */
        System.debug('jobs = ' + jobs);
        if (jobs.containsKey(ForecastHelper.SPECIALIST_FORECAST_BATCH) && jobs.get(ForecastHelper.SPECIALIST_FORECAST_BATCH) == ForecastHelper.VALUE_YES) {
            Id batchProcessId = Database.executeBatch(new SpecialistForecastBatch(), BATCH_SIZE);
            AsyncApexJob aaj = [SELECT Id, Status, JobItemsProcessed, TotalJobItems, NumberOfErrors FROM AsyncApexJob WHERE Id =: batchProcessId ];
            /* FIXME: Remove before prod! */
            System.debug('aaj for SpecialistForecastBatch = ' + aaj);
        }

        if(jobs.containsKey(ForecastHelper.SPECIALIST_FORECAST_SUBMIT_BATCH) && jobs.get(ForecastHelper.SPECIALIST_FORECAST_SUBMIT_BATCH) == ForecastHelper.VALUE_YES) {

            if(system.now().format('EEEE')==ForecastHelper.getCronRunDay()){
                Id batchProcessId = Database.executeBatch(new SpecialistForecastSubmitBatch(), BATCH_SIZE);
                AsyncApexJob aaj = [SELECT Id, Status, JobItemsProcessed, TotalJobItems, NumberOfErrors FROM AsyncApexJob WHERE Id =: batchProcessId ];
                /* FIXME: Remove before prod! */
System.debug('aaj for SpecialistForecastSubmitBatch = ' + aaj);
            }
        }
    }

}
