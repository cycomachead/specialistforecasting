/**
 *
 * Class Name : SpecForecastBatchTest
 * Description: Contains test cases to test utility methods for special forecast batch processor
 * @since
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
        TextUtil.writeDebug('specialistForecastBatchID = ' + batchProcessId);
    
        System.Assert(batchProcessId != null);
        AsyncApexJob aaj = [SELECT Id, Status, JobItemsProcessed, TotalJobItems, NumberOfErrors FROM AsyncApexJob WHERE Id =: batchProcessId];
        TextUtil.writeDebug('aaj for SpecialistForecastBatch = ' + aaj);
    
        
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
        TextUtil.writeDebug('specialistForecastSubmitBatchID = ' + batchProcessId);
    
        System.Assert(batchProcessId != null);
        
    
    }
    
}