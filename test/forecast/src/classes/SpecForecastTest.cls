/**
 *
 * Class Name : SpecForecastTest
 * Description: Contains test cases to test utility methods for special forecast batch processor
 * @since 178.store
 */


public class SpecForecastTest {

    /*
    Method Name: testGetFiscalYears
    Parameters : None
    Description: f-test utility that is used to assert special forecast getFiscalYears
    */
    private static testMethod void testGetFiscalYears() {
        SpecialistForecastController specialistController = new 
            SpecialistForecastController();
        List<SelectOption> fiscalYears = specialistController.getFiscalYears();
        String optionValue;
        for(SelectOption option : fiscalYears) {
            optionValue = optionValue + option.getValue() + ' ';
        }
        System.Assert(optionValue.contains(specialistController.selectedFY));
        
    }
    
    /**
     *  Method Name: testGetFiscalQuarters
     *  Parameters : None
     *  Description: f-test utility that is used to assert special 
     *  forecast getFiscalQuarters
     */
    private static testMethod void testGetFiscalQuarters() {
        SpecialistForecastController specialistController = new 
            SpecialistForecastController();
        List<SelectOption> fiscalQuarters = 
            specialistController.getFiscalQuarters();
        String optionValue;
        for(SelectOption option : fiscalQuarters) {
            optionValue = optionValue + option.getValue() + ' ';
        }
        System.Assert(optionValue.contains(specialistController.selectedFQ));
        
    }
    
    /**
     *  Method Name: testGetSpecialistforecastLineItemsData
     *  Parameters : None
     *  Description: f-test utility that is used to assert special
     *  getSpecialistforecastLineItemsData with the user and entry data
     */
    private static testMethod void testGetSpecialistforecastLineItemsData() {
        
         //insert data if not exist
        
         User u = createOrGetUser();
         
         
         SpecialistForecastLineItem__c specItem =new SpecialistForecastLineItem__c(OriginalBestCaseAmount__c = 6000,
                                        FiscalYear__c='FY13',FYMonth__c='August',OwnerId = u.Id);  
                                        
         insert specItem;                        
                                        
         setUpPageRef('6000', specItem.Id, 'mybestcase', u.Id, 'Q3', 'FY13');
      // This code runs as the system user 
        
         System.runAs(u) {
          // The following code runs as user 'u'  
            SpecialistForecastController specialistController = new SpecialistForecastController();
            
            List<SpecialistForecastLineItemVO> specialistVO = specialistController.getSpecialistforecastLineItemsData();
           
            System.Assert(specialistVO.size() > 0);
        }
    }
    
    /*
    Method Name: testGetSpecialistforecastLineItemsDataNoEntry
    Parameters : None
    Description: f-test utility that is used to assert special forecast getSpecialistforecastLineItemsData with the user and no entry
    */
    private static testMethod void testGetSpecialistforecastLineItemsDataNoEntry() {
          //insert data if not exist
          User u = createOrGetUser();
         
         SpecialistForecastLineItem__c specItem =new SpecialistForecastLineItem__c(OriginalBestCaseAmount__c = 5000,
                                        FiscalYear__c='FY10',FYMonth__c='March',OwnerId = u.Id);  
                                        
         insert specItem;                        
                                        
         setUpPageRef('5000', specItem.Id, 'mybestcase', u.Id, 'Q1', 'FY10');
    
      // This code runs as the system user 
       
         System.runAs(u) {
          // The following code runs as user 'u'  
            SpecialistForecastController specialistController = new SpecialistForecastController();
            
            List<SpecialistForecastLineItemVO> specialistVO = specialistController.getSpecialistforecastLineItemsData();
           
            System.Assert(specialistVO.size() > 0);
        }
    }
    
    /*
    Method Name: testReportDrildownByUser_Role
    Parameters : None
    Description: f-test utility that is used to assert special forecast reportDrildownByUser_Role
    */
    private static testMethod void testReportDrildownByUser_Role() {
          //insert data if not exist
          User u = createOrGetUser();
         
         SpecialistForecastLineItem__c specItem =new SpecialistForecastLineItem__c(OriginalBestCaseAmount__c = 6000,
                                        FiscalYear__c='FY13',FYMonth__c='August',OwnerId = u.Id);  
                                        
         insert specItem;                        
                                        
         setUpPageRef('6000', specItem.Id, 'mybestcase', u.Id, 'Q3', 'FY13');
        
         System.runAs(u) {
                SpecialistForecastController specialistController = new SpecialistForecastController();
                
                specialistController.selectedUserId = u.Id;
                PageReference drillDownPage = specialistController.reportDrildownByUser_Role();
               
                System.Assert(drillDownPage ==null);
         }
    
    }
    
    /*
    Method Name: testOverride
    Parameters : None
    Description: f-test utility that is used to assert special forecast override
    */
    private static testMethod void testOverride() {
         //insert data if not exist
          User u = createOrGetUser();
         
         SpecialistForecastOverride__c overrideItem =new SpecialistForecastOverride__c(OwnOverridenBestCaseAmount__c = 6000,
                                        PeriodType__c='Q3',OwnerId = u.Id);  
                                        
         insert overrideItem;                        
                                        
         setUpPageRef('6000', overrideItem.Id, 'mybestcase', u.Id, 'Q3', 'FY13');
        
         SpecialistForecastOverrideController overrideController = new SpecialistForecastOverrideController();
        overrideController.saveOverride();
        
        SpecialistForecastOverride__c aSpecialistForecastOverride = ([SELECT Id,IsDeleted,IsLocked,LastActivityDate,LastModifiedById,
        LastModifiedDate,ManagerOverridenBestCaseAmount__c,ManagerOverridenCommitAmount__c,ManagerOverrideNotes__c,
        MayEdit,Name,OwnerId,OwnOverridenBestCaseAmount__c,OwnOverridenCommitAmount__c,OwnOverrideNotes__c,PeriodType__c,SystemModstamp 
        FROM SpecialistForecastOverride__c WHERE Id =: overrideItem.id]);
        
        //check override amount 
        System.debug('after override bestcase ' + aSpecialistForecastOverride.OwnOverridenBestCaseAmount__c);
        System.assertEquals(aSpecialistForecastOverride.OwnOverridenBestCaseAmount__c, 6000);
    }
    
    /*
    Method Name: testGetSpecialistForecastQuota
    Parameters : None
    Description: f-test utility that is used to assert special forecast getSpecialistForecastQuota with the user and entry data
    */
    private static testMethod void testGetSpecialistForecastQuota() {
         //insert data if not exist
          User u = createOrGetUser();
         
         SpecialistForecastLineItem__c specItem =new SpecialistForecastLineItem__c(OriginalBestCaseAmount__c = 6000,
                                        FiscalYear__c='FY13',FYMonth__c='August',OwnerId = u.Id);  
                                        
         insert specItem;                        
                                        
         setUpPageRef('6000', specItem.Id, 'mybestcase', u.Id, 'Q3', 'FY13');
        
      // This code runs as the system user 
       
         System.runAs(u) {
          // The following code runs as user 'u'  
               
                 SpecialistForecastLineItem__c  sfLI = new SpecialistForecastLineItem__c();
                 sfLI.ForecastOwner__c = UserInfo.getUserId();
                 set<id> ownerIdsforQuery =new set<Id>();
          
                 ownerIdsforQuery.add(sfLI.ForecastOwner__c);
            //set the query parameters
                ForecastReportQueryParam queryParam = new ForecastReportQueryParam();
                queryParam.setFiscalYear('FY13');
                queryParam.setMonthsInQrtr(ForecastHelper.getMonthsInQuarter('Q3'));
                queryParam.setOwnerIds(ownerIdsforQuery);
                
                //get Quota information for all the users
                AggregateResult[] listSplFCastQuotas = ForecastHelper.getSpecialistForecastQuota(queryParam ); 
                
                 for (Sobject so : listSplFCastQuotas)  {
                           AggregateResult ar = (AggregateResult) so; 
                           Id OwnerId = (Id) ar.get('SpecialistForecastUser__c');
                           Decimal tempAmount = (Decimal) ar.get('amnt');
                           System.Assert(tempAmount !=null);
                 } 
        }
    }
    
    
    /**
      * set up page reference object for testing
      */
      private static void setUpPageRef(String amt, Id recId, String field, Id ownerId, String fq, String fy) {
        PageReference pageRef = new PageReference('/apex/SpecialistForecastPage');
        pageRef.getParameters().put('Amt', amt);
        pageRef.getParameters().put('RecId', recId);
        pageRef.getParameters().put('field', field);
        pageRef.getParameters().put('ownerId', ownerId);
        pageRef.getParameters().put('fq', fq);
        pageRef.getParameters().put('fy', fy);
    
      }
      
      private static User createOrGetUser(){
                 User u = [SELECT Id FROM User WHERE UserName='srikanta.mohapatra@salesforce.com'];
                 if(u == null){
                   u = new User(Email='srikanta.mohapatra@salesforce.com', FirstName='Srikanta', LastName='Mohapatra', IsActive = true,
                 Username='srikanta.mohapatra@salesforce.com', Alias='sm', CommunityNickname='srmo', TimeZoneSidKey='America/Los_Angeles', LocaleSidKey='en_US', EmailEncodingKey='ISO-8859-1', 
                 ProfileId='00e30000000brH1', LanguageLocaleKey='en_US');
             
             insert u;
             }
             return u;
      }
    
}