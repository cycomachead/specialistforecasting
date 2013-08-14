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
 */
public with sharing class SFcastLineItem {
    /** Total constant for the last line in the VF table. */
    private static final String GRAND_TOTAL = ForecastHelper.GRAND_TOTAL;

    public Boolean isSubmitted                 { get;set; }
    public Boolean isCommitOverridden          { get;set; }
    public Boolean isBestcaseOverridden        { get;set; }
    public Boolean myCommitOverridden          { get;set; }
    public Boolean myBestCaseOverridden        { get;set; }
    public Boolean mgrCommitOverridden         { get;set; }
    public Boolean mgrBestCaseOverridden       { get;set; }

    public Decimal closedAmount                { get;set; }
    public Decimal commitAmount                { get;set; }
    public Decimal bestCaseAmount              { get;set; }
    public Decimal commitOverrideAmount        { get;set; }
    public Decimal bestCaseOverrideAmount      { get;set; }
    public Decimal mgrBestCaseOverrideAmount   { get;set; }
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
        myCommitOverridden          = false;
        myBestCaseOverridden        = false;
        mgrCommitOverridden         = false;
        mgrBestCaseOverridden       = false;
        isSubmitted                 = false;
        isCommitOverridden          = false;
        isBestcaseOverridden        = false;
        closedAmount                = 0;
        commitAmount                = 0;
        bestCaseAmount              = 0;
        commitOverrideAmount        = 0;
        bestCaseOverrideAmount      = 0;
        mgrBestCaseOverrideAmount   = 0;
        mgrCommitOverrideAmount     = 0;
        prevDirectRepCommitAmount   = 0;
        prevDirectRepBestcaseAmount = 0;
        quotaAmount                 = 0;
        pipelineAmount              = 0;
        roleName                    = '';
        userName                    = '';
        roleId                      = '';
        userId                      = '';
        mgrCommitOverrideNotes      = '';
        mgrbestcaseOverrideNotes    = '';
        strLineItmId                = '';
        submittedDate               = '';
    }

    /** Returns string "UserName (RoleName)" if available. */
    public String getUserRoleName() {
        if (roleName != null && userName != null) {
            return userName + ' (' + roleName  + ')';
        }
        return '';
    }

    /** Returns the previous commit amount, formatted as a string. */
    public String getPrevVFDirectRepCommitAmount() {
        return ForecastHelper.formatAsDollar(prevDirectRepCommitAmount);
    }

    /** Returns the previous best case amount formatted as a string */
    public String getPrevVFDirectRepBestcaseAmount() {
        return ForecastHelper.formatAsDollar(prevDirectRepBestcaseAmount);
    }

    /** True IFF commit and previous commit do not match */
    public Boolean getCommitPreviousAmountMatch() {
        return prevDirectRepCommitAmount != null && commitAmount != null
            && prevDirectRepCommitAmount != commitAmount;
    }

    /** True IFF best case and previous best case do not match */
    public boolean getBestCasePreviousAmountMatch() {
        return prevDirectRepBestcaseAmount != null && bestCaseAmount != null
            && prevDirectRepBestCaseAmount != bestCaseAmount;
    }

    /**
     *  Create  displayble virtual LineItem for the VF page
     *  @param quota is the value of the quota for the forecast owner
     *  @param info is a UserWrapper with the user details for the forecast
     */
    public void createEmptyLineItem(Decimal quota,
    SpecialistForecastController.UserWrapper info) {
        userName               = info.userName;
        roleName               = info.roleName;
        userId                 = info.userID;
        roleId                 = info.roleID;
        quotaAmount            = quota;
        closedAmount           = 0;
        commitAmount           = 0;
        commitOverrideAmount   = 0;
        bestCaseOverrideAmount = 0;
        bestCaseAmount         = 0;
        pipelineAmount         = 0;
        myCommitOverridden     = false;
        myBestCaseOverridden   = false;
    }

    // Increment the values used in the total line for the VF page.
    // The parameter order should follow the column order!
    public void increment(Decimal closedAmt, Decimal commitOverrideAmt,
    Decimal commitAmt, Decimal bestCaseOverrideAmt, Decimal bestCaseAmt,
    Decimal pipelineAmt, Decimal quotaAmt) {
        commitAmount           += commitAmt;
        closedAmount           += closedAmt;
        bestCaseAmount         += bestCaseAmt;
        pipelineAmount         += pipelineAmt;
        quotaAmount            += quotaAmt;
        bestCaseOverrideAmount += bestCaseOverrideAmt;
        commitOverrideAmount   += commitOverrideAmt;
    }

    /** Set the basic properties of an instant to be like the "Total" line. */
    public void makeTotalLine() {
        userName                    = GRAND_TOTAL;
        closedAmount                = 0;
        commitAmount                = 0;
        bestCaseAmount              = 0;
        commitOverrideAmount        = 0;
        bestCaseOverrideAmount      = 0;
        mgrBestCaseOverrideAmount   = 0;
        mgrCommitOverrideAmount     = 0;
        prevDirectRepCommitAmount   = 0;
        prevDirectRepBestcaseAmount = 0;
        quotaAmount                 = 0;
        pipelineAmount              = 0;
    }

}
