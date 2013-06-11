/**
 *  Class used as value object for Specialist Forecast Lineitem
 *
 *  @author smohapatra
 *  @since 180.store
 */

public with sharing class SpecialistForecastLineItemVO {

       public boolean  withoutLink{get;set;}
       public boolean  reportingUser{get;set;}
       public boolean  forOverride{get;set;}
       public String   roleName{get;set;}
       public String   userName{get;set;}
       public String   roleId{get;set;}
       public String   userId{get;set;}
       public Decimal  closedAmount{get;set;}
       public Decimal  commitAmount{get;set;}
       public Decimal  bestCaseAmount{get;set;}
       public Decimal  commitOverrideAmount{get;set;}
       public Decimal  bestCaseOverrideAmount{get;set;}
       public Decimal  mgrbestCaseOverrideAmount{get;set;}
       public Decimal  mgrcommitOverrideAmount{get;set;}
       public String  mgrCommitOverrideNotes{get;set;}
       public String   mgrbestcaseOverrideNotes{get;set;}
       public Decimal  prevDirectRepCommitAmount{get;set;}
       public Decimal  prevDirectRepBestcaseAmount{get;set;}
       public Decimal  quotaAmount{get;set;}
       public Decimal  pipelineAmount{get;set;}
       public String   strLineItmId{get;set;}
       public String   submittedDate{get;set;}
       public boolean  MycommitOverriden {get;set;}
       public boolean  MyBestcaseOverriden {get;set;}
       public boolean  mgrcommitOverriden {get;set;}
       public boolean  mgrBestcaseOverriden {get;set;}
       public boolean  isSubmitted {get;set;}
       public boolean  isCommitOverriden{get;set;}
       public boolean  isBestcaseOverriden{get;set;}
       public boolean  searchUser{get;set;}



       public String getUserRoleName() {
            if(roleName != null && userName != null) {
                return userName + '(' + roleName  + ')';
            } else if (roleName != null && userName != null) {
                return userName;
            } else if (roleName != null && userName != null) {
                return roleName;
            }
            else return '';
       }

       public boolean getCommitPreviousAmountMatch() {
            return (searchUser != null  || !searchUser) &&
                (prevDirectRepCommitAmount != null && (commitAmount != null
                    && prevDirectRepCommitAmount != commitAmount));
       }

       public string  getprevVFDirectRepCommitAmount(){
           List<String> args = new String[]{'0','number','###,###,##0.00'};
           String sCurrency;
           if (prevDirectRepCommitAmount != null) {
               prevDirectRepCommitAmount.setscale(2);
               sCurrency = String.format(prevDirectRepCommitAmount.format(), args);
               if ( !sCurrency.contains('.' ) ) {
                   sCurrency = sCurrency + '.00';
              }
          }
          return sCurrency;
      }

      public string  getprevVFDirectRepBestcaseAmount(){
          List<String> args = new String[]{'0','number','###,###,##0.00'};
          String sCurrency;
          if (prevDirectRepBestcaseAmount != null){
              prevDirectRepBestcaseAmount.setscale(2);
              sCurrency = String.format(prevDirectRepBestcaseAmount.format(), args);
              if( !sCurrency.contains('.' ) ) {
                    sCurrency = sCurrency + '.00';
              }


           }
            return sCurrency;
       }

       public boolean getbestcasePreviousAmountMatch() {
           return (searchUser != null || !searchUser) && 
               prevDirectRepBestcaseAmount != null && bestCaseAmount != null
               && prevDirectRepbestCaseAmount != bestCaseAmount;
       }


    /**
     *
     * Create  displayble virtual LineItem for the VF page
     *
     * @param LineItem and User Map
     */
    Public void createEmptySPLineItemforVFPage(Id lineItemownerid, Id searchUser,
    Map<Id,RoleUserInfoVO> mapUsers, Decimal quotaamt ) {
        this.withoutLink = !(lineItemownerid != searchUser);
        this.forOverride   = (searchUser != UserInfo.getUserId());
        this.userName = mapUsers.get(lineItemownerid ).userName;
        this.roleName = mapUsers.get(lineItemownerid ).RoleName;
        this.userId   = mapUsers.get(lineItemownerid ).userId;
        this.roleId   = mapUsers.get(lineItemownerid ).roleId;
        this.quotaAmount            = (quotaamt == null ? 0 : quotaamt); //FIXME
        this.closedAmount           = 0;
        this.commitAmount           = 0;
        this.commitOverrideAmount   = 0;
        this.bestCaseOverrideAmount = 0;
        this.bestCaseAmount         = 0;
        this.pipelineAmount         = 0;
        this.reportingUser          = false;
        this.MycommitOverriden      = false;
        this.MyBestcaseOverriden    = false;
        this.searchUser             = false;
    }



}
