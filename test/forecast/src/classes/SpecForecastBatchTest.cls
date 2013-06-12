/**
 *
 * Class Name : TestSpecialistForecastBatch
 * Description: Contains test cases to test utility methods for special forecast batch processor
 * @since 178.store
 */

public class SpecForecastBatchTest {

    /*
    Method Name: testBatch
    Parameters : None
    Description: f-test utility that is used to assert special forecast batch processor
    */
    private static testMethod void testBatch() {
        Test.startTest();
        Id batchProcessId = Database.executeBatch(new SpecialistForecastBatch(), 20);
        Test.stopTest();
        System.debug('specialistForecastBatchID = ' + batchProcessId);

        System.Assert(batchProcessId != null);
        AsyncApexJob aaj = [SELECT Id, Status, JobItemsProcessed, TotalJobItems,
            NumberOfErrors FROM AsyncApexJob WHERE Id = :batchProcessId];
        System.debug('aaj for SpecialistForecastBatch = ' + aaj);


        System.Assert(batchProcessId ==  aaj.Id);

    }

    /*
    Method Name: testSubmitBatch
    Parameters : None
    Description: f-test utility that is used to assert special forecast submit batch processor
    */
    private static testMethod void testSubmitBatch() {
        Test.startTest();
        Id batchProcessId = Database.executeBatch(new SpecialistForecastSubmitBatch(), 20);
        Test.stopTest();
        System.debug('specialistForecastSubmitBatchID = ' + batchProcessId);

        System.Assert(batchProcessId != null);


    }

}
