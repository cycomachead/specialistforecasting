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
