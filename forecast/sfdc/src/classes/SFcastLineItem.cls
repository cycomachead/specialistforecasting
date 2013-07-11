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

public with sharing class SFcastLineItem {

       public Boolean reportingUser               { get;set; }
       public Boolean forOverride                 { get;set; }
       public Boolean myCommitOverridden          { get;set; }
       public Boolean myBestCaseOverridden        { get;set; }
       public Boolean mgrCommitOverridden         { get;set; }
       public Boolean mgrBestcaseOverridden       { get;set; }
       public Boolean isSubmitted                 { get;set; }
       public Boolean isCommitOverridden          { get;set; }
       public Boolean isBestcaseOverridden        { get;set; }
       public Boolean searchUser                  { get;set; }

       public Decimal closedAmount                { get;set; }
       public Decimal commitAmount                { get;set; }
       public Decimal bestCaseAmount              { get;set; }
       public Decimal commitOverrideAmount        { get;set; }
       public Decimal bestCaseOverrideAmount      { get;set; }
       public Decimal mgrbestCaseOverrideAmount   { get;set; }
       public Decimal mgrcommitOverrideAmount     { get;set; }
       public Decimal prevDirectRepCommitAmount   { get;set; }
       public Decimal prevDirectRepBestcaseAmount { get;set; }
       public Decimal quotaAmount                 { get;set; }
       public Decimal pipelineAmount              { get;set; }

       public String roleName                     { get;set; }
       public String userName                     { get;set; }
       public String roleId                       { get;set; }
       public String userId                       { get;set; }
       public String mgrCommitOverrideNotes       { get;set; }
       public String mgrbestcaseOverrideNotes     { get;set; }
       public String strLineItmId                 { get;set; }
       public String submittedDate                { get;set; }



       /**
        *   Returns a string "UserName (RoleName)" if available or '' if not.
        */
       public String getUserRoleName() {
            if (roleName != null && userName != null) {
                return userName + ' (' + roleName  + ')';
            }
            return '';
       }

       public Boolean getCommitPreviousAmountMatch() {
            return (searchUser != null  || !searchUser) &&
                (prevDirectRepCommitAmount != null && (commitAmount != null
                    && prevDirectRepCommitAmount != commitAmount));
       }

       public String getPrevVFDirectRepCommitAmount() {
           String[] args = new String[] {'0','number','###,###,##0.00'};
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

      public String getprevVFDirectRepBestcaseAmount() {
          List<String> args = new String[] {'0','number','###,###,##0.00'};
          String sCurrency;
          if (prevDirectRepBestcaseAmount != null) {
              prevDirectRepBestcaseAmount.setscale(2);
              sCurrency = String.format(prevDirectRepBestcaseAmount.format(), args);
              if (!sCurrency.contains('.')) {
                  sCurrency = sCurrency + '.00';
              }
          }
          return sCurrency;
      }

      public boolean getBestCasePreviousAmountMatch() {
          return (searchUser != null || !searchUser) &&
              prevDirectRepBestcaseAmount != null && bestCaseAmount != null
                  && prevDirectRepBestCaseAmount != bestCaseAmount;
      }

    /**
     *  Create  displayble virtual LineItem for the VF page
     *  @param LineItem and User Map
     */
    public void createEmptySPLineItemforVFPage(Id lineItemOwnerId, Id searchUser,
    Map<Id,Object> mapUsers, Decimal quotaAmt) {

        this.forOverride            = (searchUser != UserInfo.getUserId());
        this.userName               = mapUsers.get(lineItemOwnerId).userName;
        this.roleName               = mapUsers.get(lineItemOwnerId).roleName;
        this.userId                 = mapUsers.get(lineItemOwnerId).userID;
        this.roleId                 = mapUsers.get(lineItemOwnerId).roleID;
        this.quotaAmount            = quotaAmt;

        this.closedAmount           = 0;
        this.commitAmount           = 0;
        this.commitOverrideAmount   = 0;
        this.bestCaseOverrideAmount = 0;
        this.bestCaseAmount         = 0;
        this.pipelineAmount         = 0;
        this.reportingUser          = false;
        this.myCommitOverridden     = false;
        this.myBestCaseOverridden  = false;
        this.searchUser             = false;
    }

}