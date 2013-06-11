/**
 * ScheduleWrapper to use for StoreScheduler
 *
 * To run it FROM developer console, use the below code snippet
 * String sch = '0 50 * * * ?';
 * System.schedule('AppStore Scheduler Job', sch, new StoreScheduler());
 * System.debug('\n\n ==> Done');
 *
 * @author auzzaman
 * @since 180.store
 */

global with sharing class StoreScheduleWrapper {

    global static void start(Integer minOfHour) {
        /* FIXME: Remove before prod! */
System.debug('Running StoreScheduleWrapper ' + System.now());
        System.schedule('Store Scheduler for ' + minOfHour, '0 ' + minOfHour + ' ' + ForecastHelper.getCronExpression(), new StoreScheduler());
    }

}
