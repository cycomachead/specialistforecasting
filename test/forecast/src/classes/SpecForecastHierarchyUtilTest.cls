/*
Copyright (c) 2013, salesforce.com, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
    * Neither the name of the salesforce.com, Inc. nor the names of its contributors
    may be used to endorse or promote products derived from this software
    without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE. Written by Michael Ball.
*/

/*
 * RelEng Perforce/RCS Header - Do not remove!
 *
 * $Author: mkatragadda $
 * $Change: $
 * $DateTime:  $
 * $File: //store/main/test/forecast/src/classes/TestHierarchyUtil.cls $
 * $Id: //store/main/test/forecast/src/classes/TestHierarchyUtil.cls $
 * $Revision: #1 $
 */

/**
 *
 *  Class Name : SpecForecastHierarchyUtilTest
 *  Description: Contains test cases to test utility methods for forecast hierarchy
 *  @since 180.store
 */

public class SpecForecastHierarchyUtilTest {

    public class HierarchyTestData{
        public Id roleId;
        public String roleName;
        public Id parentRoleId;
        public Boolean hasChildren;
        public Boolean isLeafNode;
        public Boolean hasUsers;
        public Integer numberOfChildren;
        public HierarchyTestData(Id roleId, String roleName, Id parentRoleId, Boolean hasChildren, Boolean isLeafNode, Boolean hasUsers, Integer numberOfChildren){
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
                                        
                                                      });
    }

    /*
    Method Name: testGetHierarchyNegative1
    Return Type: void
    Parameters : None
    Description: f-test utility that is used to test when role id passed as null
    */
    private static testMethod void testGetHierarchyNegative1() {
        try{
            HierarchyUtil.RoleNodeWrapper roleNode = HierarchyUtil.getSubordinatesByRole(null);
        }catch (Exception e) {
            System.assertEquals(ID_IS_NULL + ' 3', e.getMessage());
            return;
        }
    }

    /*
    Method Name: testGetHierarchyNegative2
    Return Type: void
    Parameters : None
    Description: f-test utility that is used to do test when user id is passed as null
    */
    //TODO Fix me before prod.
    private static testMethod void testGetHierarchyNegative2() {

        try{
            HierarchyUtil.RoleNodeWrapper roleNode = HierarchyUtil.getSubordinatesByUser(null);
        }catch (Exception e) {
            System.assertEquals(ID_IS_NULL + ' 2', e.getMessage());
            return;
        }
    }


    private static void verifyTestResults(HierarchyUtil.RoleNodeWrapper roleNode,
    HierarchyTestData myTestData) {
        Id      expectedRoleId;
        String  expectedRoleName;
        Id      expectedParentRoleId;
        Boolean expectedHasChildren;
        Boolean expectedIsLeafNode;
        Boolean expectedHasUsers;
        Integer expectedNumberOfChildren;

        if (myTestData != null) {
            expectedRoleId           = myTestData.roleId;
            expectedRoleName         = myTestData.roleName;
            expectedParentRoleId     = myTestData.parentRoleId;
            expectedHasChildren      = myTestData.hasChildren;
            expectedIsLeafNode       = myTestData.isLeafNode;
            expectedHasUsers         = myTestData.hasUsers;
            expectedNumberOfChildren = myTestData.numberOfChildren;
        }

        System.assertEquals(roleNode.myRoleId,expectedRoleId);
        System.assertEquals(roleNode.myRoleName,expectedRoleName);
        System.assertEquals(roleNode.myParentRoleId,expectedParentRoleId);
        System.assertEquals(roleNode.hasChildren,expectedHasChildren);
        System.assertEquals(roleNode.hasUsers,expectedHasUsers);
        System.assertEquals(roleNode.isLeafNode,expectedIsLeafNode);

        List<User> usersInRole = roleNode.myUsers;
        if (usersInRole != null) {
            System.debug('myUsers:' + usersInRole.get(0).name);
        }

        List<HierarchyUtil.RoleNodeWrapper> childNodes = new 
            List<HierarchyUtil.RoleNodeWrapper>();
        childNodes = roleNode.myChildNodes;
        if (childNodes != null) {
            System.assertEquals(childNodes.size(), expectedNumberOfChildren);
            for (HierarchyUtil.RoleNodeWrapper child : childNodes) {
                System.debug('child.myRoleName:' + child.myRoleName);
                List<User> usersInChildRole = child.myUsers;
                if (usersInChildRole != null) {
                    System.debug('myUsers:' + usersInChildRole.get(0).name);
                }
            }
        }
   }

    private static void verifyTestResultsForMap(Map<String, String> actualResults, Map<String,String> expectedResults){
        System.assertEquals(expectedResults.size(),actualResults.size());
        System.assertEquals(expectedResults.keySet().size(),actualResults.keySet().size());
        //assert that the expected user name is the same as the actual user name
        for(String role : actualResults.keySet()){
            System.assertEquals(expectedResults.get(role),actualResults.get(role));
        }
       }
}
