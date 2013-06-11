/**
 * Helper Class used for Forecast Hierarchy related functionalities
 *
 * @author mkatragadda
 * @since 180.store
 */
public with sharing class HierarchyUtil {

    public with sharing class HierarchyUtilException extends Exception {}

    // map to hold roles with Id as the key
    private static Map<Id, UserRole>  roleUsersMap;

    // map to hold child roles with parentRoleId as the key
    private static Map<Id, List<UserRole>> parentChildRoleMap;

    // List holds all subordinates
    private static List<User> allSubordinates;

    //Map to hole the role and forecast hierarchy configurations
    private static Map<Id, Id>  forecastHierarchyConfigRoleUserMap;

    /**
     * public method to get all the subordinates
     *
     * @return TODO
     */
    public static RoleNodeWrapper getSubordinates(Id identifier, String type){
        if (identifier == null) {
            throw new HierarchyUtilException('Id is null 1');
            return null;
        }
        if (type == null) {
            type = ForecastHelper.HIERARCHY_TYPE_ROLE;
        }
        if (type == ForecastHelper.HIERARCHY_TYPE_ROLE) {
            return getSubordinatesByRole(identifier);
        } else if (type == ForecastHelper.HIERARCHY_TYPE_USER) {
            return getSubordinatesByUser(identifier);
        }
        return getSubordinatesByRole(identifier);
    }

    //Helper method to send the roles and user names as a Map
    //The user name for the role is retrieved FROM the custom setting
    //  ForecastHierarchyConfigurations
    // if it is not defined there then null is returned
    public static Map<String,String> getSubordinatesWithMappedUsersAsMap(
    Id identifier, String type) {
        Map<String,String> subOrdinates = new Map<String,String>();
        RoleNodeWrapper currentNode = getSubordinates(identifier, type);
        if (currentNode != null) {
            //get the current role and user
            if (currentNode.myMappedUser != null) {
                subOrdinates.put(currentNode.myRoleName, currentNode.myMappedUser.name);
                /* FIXME: Remove before prod! */
                System.debug('myROleName:' + currentNode.myRoleName + ' userName = ' + currentNode.myMappedUser.name);
            }
            //end of current node user info
            List<RoleNodeWrapper> childNodes = currentNode.myChildNodes;
            if (childNodes != null) {
                /* FIXME: Remove before prod! */
                System.debug('childNodes.size() = ' + childNodes.size());
                for (RoleNodeWrapper child: childNodes){
                    if (child.myUsers != null && child.myUsers.size()> 0){
                        if (child.myMappedUser != null){
                            /* FIXME: Remove before prod! */
                            System.debug('child.myRoleName:' + child.myRoleName);
                            /* FIXME: Remove before prod! */
                            System.debug('myMappedUsers:' + child.myMappedUser.name);
                            subOrdinates.put(child.myRoleName, child.myMappedUser.name);
                        }
                    }
                }
            }
        }
        return subOrdinates;
    }


    /* // public method to get all the direct subordinates by userId */
    public static RoleNodeWrapper getSubordinatesByUser(Id userId){
        if (userId == null) {
            throw new HierarchyUtilException('Id is null 2');
            return null;
        }
        User myUser;
        try {
            myUser = [SELECT Id, Name, UserRoleId FROM User WHERE Id =: userId];
        } catch (Exception ex) {
            /* FIXME: Remove before prod! */
            System.debug('Exception in getSubordinatesByUser' + ex.getMessage());
            return null;
        }
        return getSubordinatesByRole(myUser.UserRoleId);
    }

    /* // public method to get all the direct subordinates by role */
    public static RoleNodeWrapper getSubordinatesByRole(Id parentRoleId) {
        if (parentRoleId == null) {
            throw new HierarchyUtilException('Id is null 3');
            return null;
        }
        /* FIXME: Remove before prod! */
        System.debug('parentRoleId of starting node = ' + parentRoleId);

        allSubordinates = new List<User>();

        // Get role to users mapping in a map with key as role id
        try {
            roleUsersMap = new Map<Id, UserRole>([SELECT Id, Name, parentRoleId,
                (SELECT id, name
                FROM users WHERE isActive = true and usertype = 'STANDARD')
                FROM UserRole WHERE parentRoleId = :parentRoleId]);
            //add the parent to the map as well to fetch the parent information
            UserRole parentRoleInfo = [SELECT id, Name, parentRoleId,
                (SELECT id, name
                FROM users WHERE isActive = true and usertype = 'STANDARD')
                FROM UserRole WHERE id = :parentRoleId];
            roleUsersMap.put(parentRoleInfo.id, parentRoleInfo);
        } catch (Exception ex) {
            /* FIXME: Remove before prod! */
            System.debug('Exception in getSubordinatesByRole, ' + ex.getMessage());
            return null;
        }
        // populate parent role - child roles map
        parentChildRoleMap = new Map<Id, List<UserRole>>();
        for (UserRole r : roleUsersMap.values()) {
            List<UserRole> childUserRoleList;
            if (!parentChildRoleMap.containsKey(r.parentRoleId)){
                childUserRoleList = new List<UserRole>();
                childUserRoleList.add(r);
                parentChildRoleMap.put(r.parentRoleId, childUserRoleList);
            }
            else {
                childUserRoleList = parentChildRoleMap.get(r.parentRoleId);
                childUserRoleList.add(r);
                parentChildRoleMap.put(r.parentRoleId, childUserRoleList);
            }
        }
        return createNode(parentRoleId);
    }

    /**
     *  TODO: Refactor, Better Exception Handling
     *  public method to get all the seven level subordinates by userId
     *  @return
     */
    public static RoleNodeWrapper getMappedAllLevelSubordinatesByUser(Id userId){
        if (userId == null) {
            throw new HierarchyUtilException('Id is null 4');
            return null;
        }
        User myUser;
        try {
            myUser = [SELECT UserRoleId FROM User WHERE Id = :userId];
        } catch (Exception ex) {
            /* FIXME: Remove before prod! */
            System.debug('Exception in getSubordinatesByUser' + ex.getMessage());
            return null;
        }
        if (myUser.UserRoleId != null) {
            return getMappedAllLevelSubordinatesByRole(myUser.UserRoleId);
        } else {
            return null;
        }
    }

    /**
     *  public method to get all the seven level subordinates by role
     *  @return
     */
    public static RoleNodeWrapper getMappedAllLevelSubordinatesByRole(
    Id parentRoleId) {
        if (parentRoleId == null) {
            throw new HierarchyUtilException('Unable To Find Subordinate Roles for the Current User.');
            return null;
        }
        /* FIXME: Remove before prod! */
        System.debug('parentRoleId of starting node = ' + parentRoleId);

        allSubordinates = new List<User>();
        parentChildRoleMap = new Map<Id, List<UserRole>>();

        //fetch all the forecast hierarchy configurations Role/User Map
        if(forecastHierarchyConfigRoleUserMap == null){
            forecastHierarchyConfigRoleUserMap =
                getForecastHierarchyConfigurationsRoleUserMap();
        }

        //first fetch the list of all parent role ids upto level 7
        Integer level = 0;
        Set<Id> parentRoleIds = new Set<Id>();
        parentRoleIds.add(parentRoleId);

        while (level < 7) {
            List<UserRole> currentRoles = new List<UserRole>();
            currentRoles = [SELECT id FROM UserRole
                            WHERE parentRoleId in :parentRoleIds];
            for (UserRole role : currentRoles){
                //check if mapping for this role exists, only if mapping exists
                //  then we will query data, this is to avoid reaching SOQL
                //  limits for users
                if(forecastHierarchyConfigRoleUserMap.containsKey(role.id)){
                    parentRoleIds.add(role.id);
                }
            }

            level += 1;
            /* FIXME: Remove before prod! */
            System.debug('parent roles fetched = ' + currentRoles);
        }

        /* FIXME: Remove before prod! */
        System.debug('all parent roles fetched = ' + parentRoleIds);

        // Get role to users mapping in a map with key as role id
        try {
            roleUsersMap = new Map<Id, UserRole>([SELECT Id, Name, parentRoleId,
                (SELECT id, name FROM users WHERE isActive = true and usertype='STANDARD')
                    FROM UserRole WHERE parentRoleId in :parentRoleIds]);
            //add the parent to the map as well to fetch the top parent
            //  information
            UserRole parentRoleInfo = [SELECT id, Name, parentRoleId,
                (SELECT id, name
                FROM users WHERE isActive = true and usertype='STANDARD')
                FROM UserRole WHERE id = :parentRoleId];
            roleUsersMap.put(parentRoleInfo.id, parentRoleInfo);
        } catch (Exception ex) {
            /* FIXME: Remove before prod! */
            System.debug('Exception in getSubordinatesByRole, ' + ex.getMessage());
            return null;
        }
        // populate parent role - child roles map
        for (UserRole r : roleUsersMap.values()) {
            List<UserRole> childUserRoleList;
            if (!parentChildRoleMap.containsKey(r.parentRoleId)){
                childUserRoleList = new List<UserRole>();
                childUserRoleList.add(r);
                parentChildRoleMap.put(r.parentRoleId, childUserRoleList);
            } else {
                childUserRoleList = parentChildRoleMap.get(r.parentRoleId);
                childUserRoleList.add(r);
                parentChildRoleMap.put(r.parentRoleId, childUserRoleList);
            }
        }
        return createNode(parentRoleId);
    }

    /* // createNode starts */
    private static RoleNodeWrapper createNode(Id roleId) {
        /* FIXME: Remove before prod! */
        System.debug('Inside createNode: roleId of starting node = ' + roleId);
        RoleNodeWrapper n = new RoleNodeWrapper();
        if (!roleUsersMap.get(roleId).Users.isEmpty()) {
             n.myUsers = roleUsersMap.get(roleId).Users;
             allSubordinates.addAll(n.myUsers);
             n.hasUsers = true;
        }
        n.myRoleId = roleId;
        n.myRoleName = roleUsersMap.get(roleId).Name;
        n.myParentRoleId = roleUsersMap.get(roleId).ParentRoleId;

        if (parentChildRoleMap.containsKey(roleId)){
            n.hasChildren = true;
            n.isLeafNode = false;
            List<RoleNodeWrapper> lst = new List<RoleNodeWrapper>();
            for (UserRole r : parentChildRoleMap.get(roleId)) {
                lst.add(createNode(r.Id));
            }
            n.myChildNodes = lst;
        }
        else {
            n.isLeafNode = true;
            n.hasChildren = false;
        }

        //set the mapped user information, fetch the user if there is a mapping
        //  in the hierarchy configuration table, else it returns null
        if (n.myUsers != null && n.myUsers.size() > 0) {
            Id currentNodeMappedUser;
            //fetch all the forecast hierarchy configurations Role/User Map
            forecastHierarchyConfigRoleUserMap =
                   getForecastHierarchyConfigurationsRoleUserMap();
            currentNodeMappedUser =
                forecastHierarchyConfigRoleUserMap.get(n.myRoleId);
            if (currentNodeMappedUser != null) {
                // make sure this user in the custom setting exists in the list
                //  of users
                for (User currUser : n.myUsers) {
                    if (currentNodeMappedUser == currUser.id) {
                        n.myMappedUser = currUser;
                        break;
                    }
                }
            }
        }
        return n;
    }

    public class RoleNodeWrapper {

        // Role info properties - begin
        public String myRoleName {get; set;}

        public Id myRoleId {get; set;}

        public Id myParentRoleId {get; set;}
        // Role info properties - end

        // Node children identifier properties - begin
        public Boolean hasChildren {get; set;}

        public Boolean isLeafNode {get; set;}

        public Boolean hasUsers {get; set;}
        // Node children identifier properties - end

        // Node children properties - begin
        public List<User> myUsers {get; set;}

        // mapped user FROM the forecast hierarchy configurations, if no mapping
        //  exists, then it returns null
        public User myMappedUser {get; set;}

        public List<RoleNodeWrapper> myChildNodes {get; set;}
        // Node children properties - end

        public RoleNodeWrapper(){
            hasUsers = false;
            hasChildren = false;
        }
    }

    /**
     * This method will fetch Manager/Owner user as id FROM the forecast
     * hieararchy configurations
     * @return TODO
     */
    public static Map<Id, Id> getForecastHierarchyConfigurationsRoleUserMap() {
        forecastHierarchyConfigRoleUserMap = new Map<Id, Id>();
        List<ForecastHierarchyConfigurations__c> configurationsList = new
            List<ForecastHierarchyConfigurations__c>();
        //fetch all the forecast hierarchy configurations
           try {
             configurationsList = [SELECT HierarchyRole__c,
                                    HierarchyRoleUser__r.id, IsManager__c,
                                    IsActive__c, Comment__c, HashForCheck__c
                                    FROM ForecastHierarchyConfigurations__c
                                    WHERE IsActive__c = true and
                                    IsManager__c = true];
        } catch (Exception ex) {
            /* FIXME: Remove before prod! */
            System.debug('Exception in getForecastHierarchyConfigurationsByRole'
                + ex.getMessage());
            return null;
        }
        /* FIXME: Remove before prod! */
        System.debug('the list of configurations retrieved=' + configurationsList);
        for(ForecastHierarchyConfigurations__c config : configurationsList){
            forecastHierarchyConfigRoleUserMap.put(config.HierarchyRole__c,
                config.HierarchyRoleUser__r.id);
        }
        return forecastHierarchyConfigRoleUserMap;
    }

    /**
     * This method is used to create a hash to validate duplicate records during
     * insert.  We need only one manager user assigned to a specific role. This
     * is called FROM the trigger..
     * @return TODO
     */
    public static String createHash(ForecastHierarchyConfigurations__c fhc) {
        /* FIXME: Remove before prod! */
        System.debug('roleId = ' + fhc.HierarchyRole__c + 'userId = ' +
            fhc.HierarchyRoleUser__c);
        String roleID;
        String userID;
        try {
            roleID = ('' + fhc.HierarchyRole__c).substring(0, 15);
            userID = ('' + fhc.HierarchyRoleUser__c).substring(0, 15);
        } catch (Exception ex) {
            return ''; // FIXME: What should happen on bad inp?
        }
        Blob bPrehash = Blob.valueOf('FORECAST_HIERARCHY_CONFIG-' + roleId +
             userId);
        Blob bsig = Crypto.generateDigest('SHA1', bPrehash);
        return EncodingUtil.convertToHex(bsig);

    }

 }
