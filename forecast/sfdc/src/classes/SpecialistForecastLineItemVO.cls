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
 *  Class used as value object for Specialist Forecast Lineitem
 *
 *  @author smohapatra
 *  @since 180.store
 */

public with sharing class SpecialistForecastLineItemVO {

       public Boolean  withoutLink                 { get;set; }
       public Boolean  reportingUser               { get;set; }
       public Boolean  forOverride                 { get;set; }
       public Boolean  myCommitOverriden           { get;set; }
       public Boolean  myBestcaseOverriden         { get;set; }
       public Boolean  mgrCommitOverriden          { get;set; }
       public Boolean  mgrBestcaseOverriden        { get;set; }
       public Boolean  isSubmitted                 { get;set; }
       public Boolean  isCommitOverriden           { get;set; }
       public Boolean  isBestcaseOverriden         { get;set; }
       public Boolean  searchUser                  { get;set; }
       
       public Decimal  closedAmount                { get;set; }
       public Decimal  commitAmount                { get;set; }
       public Decimal  bestCaseAmount              { get;set; }
       public Decimal  commitOverrideAmount        { get;set; }
       public Decimal  bestCaseOverrideAmount      { get;set; }
       public Decimal  mgrbestCaseOverrideAmount   { get;set; }
       public Decimal  mgrcommitOverrideAmount     { get;set; }
       public Decimal  prevDirectRepCommitAmount   { get;set; }
       public Decimal  prevDirectRepBestcaseAmount { get;set; }
       public Decimal  quotaAmount                 { get;set; }
       public Decimal  pipelineAmount              { get;set; }
       
       public String   roleName                    { get;set; }
       public String   userName                    { get;set; }
       public String   roleId                      { get;set; }
       public String   userId                      { get;set; }
       public String   mgrCommitOverrideNotes      { get;set; }
       public String   mgrbestcaseOverrideNotes    { get;set; }
       public String   strLineItmId                { get;set; }
       public String   submittedDate               { get;set; }



       /**
        *   Returns a string "UserName (RoleName)" if available or '' if not.
        */
       public String getUserRoleName() {
            if (roleName != null && userName != null) {
                return userName + ' (' + roleName  + ')';
            }
            return '';
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
          List<String> args = new String[] {'0','number','###,###,##0.00'};
          String sCurrency;
          if (prevDirectRepBestcaseAmount != null){
              prevDirectRepBestcaseAmount.setscale(2);
              sCurrency = String.format(prevDirectRepBestcaseAmount.format(), args);
              if (!sCurrency.contains('.')) {
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
        
        this.withoutLink            = !(lineItemownerid != searchUser);
        this.forOverride            = (searchUser != UserInfo.getUserId());
        this.userName               = mapUsers.get(lineItemownerid).userName;
        this.roleName               = mapUsers.get(lineItemownerid).roleName;
        this.userId                 = mapUsers.get(lineItemownerid).userID;
        this.roleId                 = mapUsers.get(lineItemownerid).roleID;
        this.quotaAmount            = quotaamt;
            
        this.closedAmount           = 0;
        this.commitAmount           = 0;
        this.commitOverrideAmount   = 0;
        this.bestCaseOverrideAmount = 0;
        this.bestCaseAmount         = 0;
        this.pipelineAmount         = 0;
        this.reportingUser          = false;
        this.myCommitOverriden      = false;
        this.myBestcaseOverriden    = false;
        this.searchUser             = false;
    }



}
