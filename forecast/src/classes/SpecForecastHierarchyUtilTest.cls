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

/**
 *
 *  Class Name : SpecForecastHierarchyUtilTest
 *  Description: Contains test cases to test utility methods for forecast hierarchy
 */

@isTest
public with sharing class SpecForecastHierarchyUtilTest {

    public class HierarchyTestData {
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

    /** Standard error messages. */
    private static Final String ROLE_ID_NULL =
        SpecialistForecastException.ROLE_ID_NULL;
    private static Final String USER_ID_NULL =
        SpecialistForecastException.USER_ID_NULL;
    private static Final String PARENT_ID_NULL =
        SpecialistForecastException.PARENT_ID_NULL;

    static {
        roleTestDataMap.put('00E30000000tBgV', new HierarchyTestData('00E30000000tBgV', 'SVP Corporate Sales', '00E30000000raoJ', true, false, true, 5));
        roleTestDataMap.put('00E00000006DERY', new HierarchyTestData('00E00000006DERY', 'Reg 1 - A - B', '00E30000000tBgV', true, false, true, 4));
        userTestDataMap.put('005300000013TxE', new HierarchyTestData('00E30000000tBgV', 'SVP Corporate Sales', '00E30000000raoJ', true, false, true, 5));
        userTestDataMap.put('00530000000qbHh', new HierarchyTestData('00E00000006DERY', 'Reg 1 - A - B', '00E30000000tBgV', true, false, true, 4));
        //test data is retrieved as a Map
        testDataAsMap.put('00E30000000tBgV', new Map<String,String> {'SVP Corporate Sales'=>'Joe Six-Pack',
                                                                        'Reg2 - a - VP'=>'Ronald McDonald',
                                                                        'US - All - VP'=>'Joe Biden',
                                                                        'Reg3 - a - VP'=>'Nameless Person',
                                                                        'Reg3 - a - VP'=>'With A. Name',
                                                                        'Reg1 - a - RM'=>'Passing Tests'}
                                                                        );

        testDataAsMap.put('00E00000006DERY', new Map<String,String> {'Reg 1 - a - VP'=>'Bob'} );
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
            System.assertEquals(PARENT_ID_NULL, e.getMessage());
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
        try {
            HierarchyUtil.RoleNodeWrapper roleNode = HierarchyUtil.getSubordinatesByUser(null);
        } catch (Exception e) {
            System.assertEquals(USER_ID_NULL, e.getMessage());
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

        HierarchyUtil.RoleNodeWrapper[] childNodes = new
            HierarchyUtil.RoleNodeWrapper[0];
        childNodes = roleNode.myChildNodes;
        if (childNodes != null) {
            System.assertEquals(childNodes.size(), expectedNumberOfChildren);
            for (HierarchyUtil.RoleNodeWrapper child : childNodes) {
                System.debug('child.myRoleName:' + child.myRoleName);
                User[] usersInChildRole = child.myUsers;
                if (usersInChildRole != null) {
                    System.debug('myUsers:' + usersInChildRole.get(0).name);
                }
            }
        }
   }

    private static void verifyTestResultsForMap(Map<String, String> actualResults,
    Map<String,String> expectedResults){
        System.assertEquals(expectedResults.size(),actualResults.size());
        System.assertEquals(expectedResults.keySet().size(),actualResults.keySet().size());
        //assert that the expected user name is the same as the actual user name
        for(String role : actualResults.keySet()){
            System.assertEquals(expectedResults.get(role),actualResults.get(role));
        }
    }
}
