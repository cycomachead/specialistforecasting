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
       public Boolean mgrBestCaseOverridden       { get;set; }
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
       public Decimal mgrCommitOverrideAmount     { get;set; }
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

       // Basic Constructor, prevent null values.
       public SFCastLineItem() {
           reportingUser               = false;
           forOverride                 = false;
           myCommitOverridden          = false;
           myBestCaseOverridden        = false;
           mgrCommitOverridden         = false;
           mgrBestCaseOverridden       = false;
           isSubmitted                 = false;
           isCommitOverridden          = false;
           isBestcaseOverridden        = false;
           searchUser                  = true; //FIXME
           // closedAmount                = 0;
//            commitAmount                = 0;
//            bestCaseAmount              = 0;
//            commitOverrideAmount        = 0;
//            bestCaseOverrideAmount      = 0;
//            mgrbestCaseOverrideAmount   = 0;
//            mgrCommitOverrideAmount     = 0;
//            prevDirectRepCommitAmount   = 0;
//            prevDirectRepBestcaseAmount = 0;
//            quotaAmount                 = 0;
//            pipelineAmount              = 0;
//            roleName                    = '';
//            userName                    = '';
//            roleId                      = '';
//            userId                      = '';
//            mgrCommitOverrideNotes      = '';
//            mgrbestcaseOverrideNotes    = '';
//            strLineItmId                = '';
//            submittedDate               = '';
       }


       /** Returns string "UserName (RoleName)" if available. */
       public String getUserRoleName() {
            if (roleName != null && userName != null) {
                return userName + ' (' + roleName  + ')';
            }
            return '';
       }

       // FIXME search user needs Init'd
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

      public String getPrevVFDirectRepBestcaseAmount() {
          String[]   args = new String[] {'0','number','###,###,##0.00'};
          String sCurrency;
          if (prevDirectRepBestcaseAmount != null) {
              prevDirectRepBestcaseAmount.setscale(2);
              sCurrency = String.format(prevDirectRepBestcaseAmount.format(), args);
              if (!sCurrency.contains('.')) {
                  sCurrency += '.00';
              }
          }
          return sCurrency;
      }

      // FIXME search user needs Init'd
      public boolean getBestCasePreviousAmountMatch() {
          return (searchUser != null || !searchUser) &&
              prevDirectRepBestcaseAmount != null && bestCaseAmount != null
                  && prevDirectRepBestCaseAmount != bestCaseAmount;
      }

    /**
     *  Create  displayble virtual LineItem for the VF page
     *  @param LineItem and User Map
     */
    public void createEmptySFLineItem(Id lineItemOwnerId, Id inpUser,
    Map<Id,SpecialistForecastController.UserInfoWrapper> mapUsers, Decimal quotaAmt) {

        forOverride            = (inpUser != UserInfo.getUserId());
        userName               = mapUsers.get(lineItemOwnerId).userName;
        roleName               = mapUsers.get(lineItemOwnerId).roleName;
        userId                 = mapUsers.get(lineItemOwnerId).userID;
        roleId                 = mapUsers.get(lineItemOwnerId).roleID;
        quotaAmount            = quotaAmt;
        closedAmount           = 0;
        commitAmount           = 0;
        commitOverrideAmount   = 0;
        bestCaseOverrideAmount = 0;
        bestCaseAmount         = 0;
        pipelineAmount         = 0;
        reportingUser          = false;
        myCommitOverridden     = false;
        myBestCaseOverridden   = false;
        searchUser             = true; //FIXME
    }

}
