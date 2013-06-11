/**
*  Class used as value object for Specialist Forecast Lineitem
*
* @author smohapatra
* @since 180.store
*/

public with sharing class RoleUserInfoVO {
    public Id roleId{get;set;}
    public Id userId{get;set;} 
    public String roleName{get;set;}
    public String userName{get;set;}

}
