/**
 *
 * Class Name : SpecForecastHierarchyHelperTest
 * Description: Contains test cases to test utility methods for forecast hierarchu
 * @since
 */

public class SpecForecastHierarchyHelperTest {
    
    public class HierarchyTestData {
        public Id roleId;
        public String roleName;
        public Id parentRoleId;
        public Boolean hasChildren;
        public Boolean isLeafNode;
        public Boolean hasUsers;
        public Integer numberOfChildren;
        public HierarchyTestData(Id roleId, String roleName, Id parentRoleId, 
        Boolean hasChildren, Boolean isLeafNode, Boolean hasUsers, 
        Integer numberOfChildren) {
            this.roleId = roleId;
            this.roleName = roleName;
            this.parentRoleId = parentRoleId;
            this.hasChildren = hasChildren;
            this.isLeafNode  = isLeafNode;
            this.hasUsers = hasUsers;
            this.numberOfChildren = numberOfChildren;
        }
    }
    
    public static Map<Id, HierarchyTestData> roleTestDataMap = new Map<Id, HierarchyTestData>();
    
    public static Map<Id, HierarchyTestData> userTestDataMap = new Map<Id, HierarchyTestData>();

    public static Map<Id, Map<String,String>> testDataAsMap = new Map<Id, Map<String,String>>();

    
    public static String ID_IS_NULL = 'Id is null';
    
    //TODO: need remove the hard code id, so far just comment out the tests
    
    static {
        //EVP WW Corporate Sales Hillary's level by role
        roleTestDataMap.put('00E30000000tBgV', new HierarchyTestData('00E30000000tBgV', 'EVP WW Corporate Sales', '00E30000000raoJ', true, false, true, 5));
        //EMEA CS VP by role
        roleTestDataMap.put('00E00000006DERY', new HierarchyTestData('00E00000006DERY', 'EMEA - CS - VP', '00E30000000tBgV', true, false, true, 4));        
        
        //Hillary's level by user
        userTestDataMap.put('005300000013TxE', new HierarchyTestData('00E30000000tBgV', 'EVP WW Corporate Sales', '00E30000000raoJ', true, false, true, 5));
        //EMEA CS VP by user Rob
        userTestDataMap.put('00530000000qbHh', new HierarchyTestData('00E00000006DERY', 'EMEA - CS - VP', '00E30000000tBgV', true, false, true, 4));
    
        //test data is retrieved as a Map
        testDataAsMap.put('00E30000000tBgV', new Map<String,String>{'EVP WW Corporate Sales'=>'Hilarie Koplow-McAdams',
                                                                        'EMEA - CS - VP'=>'Rob Acker',
                                                                        'AMER - CS - SVP'=>'Michael Morriss',
                                                                        'JP - CS - VP'=>'Yasutaka Fukuda',
                                                                        'APAC - CS - VP'=>'Aaron S. Katz',
                                                                        'LACA - CS - RM'=>'Alex Wolin'}
                                                                        );
                                                      
        testDataAsMap.put('00E00000006DERY', new Map<String,String>{'EMEA - CS - VP'=>'Rob Acker'
                                                     //               ,'EMEA SR & EBR Manager'=>'Elaine O\'Callaghan'
                                                     //               ,'EMEA - CS - UK&I - AVP'=>'Ciaran Shanley'
                                                      //              ,'EMEA - CS - EU - AVP'=>'Ronan Copeland'
                                                      });                                               
                                                      
                                                      //'AMER - CS - AVP - WEST = Robert Zimmermann','>
    }
    

        
    /*
    Method Name: testGetHierarchyByRole
    Return Type: void
    Parameters : None
    Description: f-test utility that is used to assert the Hierarchy values for testing
    */
    /*private static testMethod void testGetHierarchyByRole() {
        ForecastHierarchyHelper.RoleNodeWrapper roleNode;    
        for (Id identifier : roleTestDataMap.keySet()) {            
            roleNode = ForecastHierarchyHelper.getSubordinatesByRole(identifier);    
            verifyTestResults(roleNode,  roleTestDataMap.get(identifier));
        }            
    }*/
    
    /*
    Method Name: testGetHierarchyByUser
    Return Type: void
    Parameters : None
    Description: f-test utility that is used to assert the Hierarchy values for testing
    */    
    /*private static testMethod void testGetHierarchyByUser() {
        ForecastHierarchyHelper.RoleNodeWrapper roleNode;                
        for (Id identifier : userTestDataMap.keySet()) {            
            roleNode = ForecastHierarchyHelper.getSubordinatesByUser(identifier);    
            verifyTestResults(roleNode, userTestDataMap.get(identifier));
        }        
    }*/
    /*
    Method Name: testGetHierarchyByUserOrRole
    Return Type: void
    Parameters : None
    Description: f-test utility that is used to assert the Hierarchy values for testing
    */    
    /*private static testMethod void testGetHierarchyByUserOrRole() {
        ForecastHierarchyHelper.RoleNodeWrapper roleNode;    
        for (Id identifier : roleTestDataMap.keySet()) {            
            roleNode = ForecastHierarchyHelper.getSubordinates(identifier, 'ROLE');    
            verifyTestResults(roleNode, roleTestDataMap.get(identifier));
        }
        for (Id identifier : userTestDataMap.keySet()) {            
            roleNode = ForecastHierarchyHelper.getSubordinates(identifier, 'USER');    
            verifyTestResults(roleNode, userTestDataMap.get(identifier));
        }                    
        
    }*/
    
    /*
    Method Name: testGetHierarchyNegative1
    Return Type: void
    Parameters : None
    Description: f-test utility that is used to test when role id passed as null
    */
    private static testMethod void testGetHierarchyNegative1() {    
        try{
            ForecastHierarchyHelper.RoleNodeWrapper roleNode = ForecastHierarchyHelper.getSubordinatesByRole(null);
        }catch (Exception e){
            System.assertEquals(ID_IS_NULL, e.getMessage());
            return;            
        }
    }
    
    /*
    Method Name: testGetHierarchyNegative2
    Return Type: void
    Parameters : None
    Description: f-test utility that is used to do test when user id is passed as null
    */
    private static testMethod void testGetHierarchyNegative2() {    
    
        try{
            ForecastHierarchyHelper.RoleNodeWrapper roleNode = ForecastHierarchyHelper.getSubordinatesByUser(null);
        }catch (Exception e){
            System.assertEquals(ID_IS_NULL, e.getMessage());
            return;
        }        
    }    
    
    /*
    Method Name: testGetHierarchyNegative3
    Return Type: void
    Parameters : None
    Description: f-test utility that is used to test when passed in id doesn't exist
    */
    /*private static testMethod void testGetHierarchyNegative3() {    
        ForecastHierarchyHelper.RoleNodeWrapper roleNode = ForecastHierarchyHelper.getSubordinatesByRole('00Z00000006ZZZZ');        
        System.assertEquals(roleNode, null);
        roleNode = ForecastHierarchyHelper.getSubordinatesByUser('00Z00000006ZZZZ');        
        System.assertEquals(roleNode, null);
        roleNode = ForecastHierarchyHelper.getSubordinates('00Z00000006ZZZZ', 'ROLE');
        System.assertEquals(roleNode, null);
        roleNode = ForecastHierarchyHelper.getSubordinates('00Z00000006ZZZZ', 'USER');
        System.assertEquals(roleNode, null);                    
   }*/
           
   private static void verifyTestResults(ForecastHierarchyHelper.RoleNodeWrapper roleNode, HierarchyTestData myTestData){
           Id              expectedRoleId;  
           String          expectedRoleName; 
           Id              expectedParentRoleId; 
           Boolean         expectedHasChildren; 
           Boolean         expectedIsLeafNode;
           Boolean         expectedHasUsers;
           Integer         expectedNumberOfChildren;
           

           if(myTestData != null){
               expectedRoleId = myTestData.roleId;
               expectedRoleName = myTestData.roleName;
               expectedParentRoleId = myTestData.parentRoleId;
               expectedHasChildren = myTestData.hasChildren;
               expectedIsLeafNode = myTestData.isLeafNode;
               expectedHasUsers = myTestData.hasUsers;
               expectedNumberOfChildren = myTestData.numberOfChildren;
           }
           
         System.debug('roleNode.myRoleId:'+roleNode.myRoleId);
        System.debug('roleNode.myRoleName:'+roleNode.myRoleName);
        
        System.assertEquals(roleNode.myRoleId,expectedRoleId);
        System.assertEquals(roleNode.myRoleName,expectedRoleName);
        System.assertEquals(roleNode.myParentRoleId,expectedParentRoleId);
        System.assertEquals(roleNode.hasChildren,expectedHasChildren);
        System.assertEquals(roleNode.hasUsers,expectedHasUsers);
        System.assertEquals(roleNode.isLeafNode,expectedIsLeafNode);
        
        List<User> usersInRole = roleNode.myUsers;
        if(usersInRole != null){
            System.debug('myUsers:'+usersInRole.get(0).name);
        }
        
        List <ForecastHierarchyHelper.RoleNodeWrapper> childNodes = new List<ForecastHierarchyHelper.RoleNodeWrapper>();
        childNodes = roleNode.myChildNodes;    
        if(childNodes != null){
            System.assertEquals(childNodes.size(), expectedNumberOfChildren);        
            for(ForecastHierarchyHelper.RoleNodeWrapper child: childNodes){
                System.debug('child.myRoleName:'+child.myRoleName);
                List<User> usersInChildRole = child.myUsers;
                if(usersInChildRole != null){
                    System.debug('myUsers:'+usersInChildRole.get(0).name);
                }
                //for(User u : usersInChildRole){
                //    System.debug('myUsers:'+u.name);
                //}                
            }
        } 
   }     
   
    /*
    Method Name: testSubordinatesAsMap
    Return Type: void
    Parameters : None
    Description: f-test utility that is used to assert the Hierarchy Role and User values returned as a map for testing
    */    
    /*private static testMethod void testGetHierarchyAsMap() {
        Map<String, String> myResults = new Map<String,String>();                
        for (Id identifier : testDataAsMap.keySet()) {            
            myResults = ForecastHierarchyHelper.getSubordinatesWithMappedUsersAsMap(identifier,'ROLE');    
            verifyTestResultsForMap(myResults,testDataAsMap.get(identifier));
        }        
    } */
          
       private static void verifyTestResultsForMap(Map<String, String> actualResults, Map<String,String> expectedResults){
        System.assertEquals(expectedResults.size(),actualResults.size());
        System.assertEquals(expectedResults.keySet().size(),actualResults.keySet().size());
        //assert that the expected user name is the same as the actual user name        
        for(String role : actualResults.keySet()){    
            System.assertEquals(expectedResults.get(role),actualResults.get(role));
        } 
       }    
  /* This is used for verifying 
     private static testMethod void testGetHierarchyByUser123() {
        ForecastHierarchyHelper.RoleNodeWrapper roleNode;   
        roleNode = ForecastHierarchyHelper.getSubordinatesByUser('005x0000000KYMq');    
        System.debug('Mapped User=' + roleNode.myMappedUser);
        if(roleNode.myMappedUser != null){
            System.debug('Mapped User Id=' + roleNode.myMappedUser.Id);
            System.debug('Mapped User Name=' + roleNode.myMappedUser.name);
           
        }
        System.debug('My Role = ' + roleNode.myRoleName);
        System.debug('My roleId = ' + roleNode.myRoleId);
        
        List <ForecastHierarchyHelper.RoleNodeWrapper> childNodes = new List<ForecastHierarchyHelper.RoleNodeWrapper>();
        childNodes = roleNode.myChildNodes; 
        if(childNodes != null){
            for(ForecastHierarchyHelper.RoleNodeWrapper child: childNodes){
                User mappedUserInChildRole = child.myMappedUser;
                if(mappedUserInChildRole != null){
                    System.debug('child.myRoleName:'+child.myRoleName);
                    System.debug('child.myRoleId:'+child.myRoleId);                    
                    System.debug('child.myMappedUsers:'+mappedUserInChildRole);
                }       
            }
        }   
            
    } 
    */
}




