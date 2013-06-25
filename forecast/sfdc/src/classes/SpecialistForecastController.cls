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
 *  Controller to display and override Special AE Forecast
 *
 *  @author auzzaman,smohapatra
 *  @since
 */
public with sharing class SpecialistForecastController {

    private Boolean isValidated = true;

    private static final String CLOSED      = ForecastHelper.CLOSED;
    private static final String CLOSED_WON  = ForecastHelper.CLOSED_WON;
    private static final String COMMIT_MSG  = ForecastHelper.COMMIT_MSG;
    private static final String GRAND_TOTAL = ForecastHelper.GRAND_TOTAL;
    private static final String QTR_ALL     = ForecastHelper.QTR_ALL;

    private static final String FOR_ID =
            SpecialistForecastException.FOR_ID;
    private static final String USER_ID_NULL =
            SpecialistForecastException.USER_ID_NULL + FOR_ID;
    private static final String ROLE_ID_NULL =
            SpecialistForecastException.ROLE_ID_NULL + FOR_ID;

    private Set<Id> allUserIDs = new Set<Id>();
    private Set<Id> allTotaluserIDs = new Set<Id>();

    private List<SelectOption> fiscalYears;
    private List<SelectOption> fiscalQuarters;

    private List<SpecialistForecastlineItemVO> splFClineItems;

    private Map<Id,set<Id>> mapSecondLeverUsersAndChild = new Map<Id,set<Id>>();
    private Map<Id,RoleUserInfoVO> mapUsers = new Map<Id,RoleUserInfoVO>();

    public Boolean displayPopup      { get;set; }

    public Decimal overrideAmount    { get;set; }
    public Decimal originalAmount    { get;set; }
    public Decimal originaloldAmount { get;set; }

    public SpecialistForecastlineItem__c sfLI;

    public String overrideComment    { get;set; }
    public String strRecordId        { get;set; }
    public String strField           { get;set; }
    public String lineItemOwnerId    { get;set; }
    public String fiscalYear         { get;set; }
    public String fiscalQuarter      { get;set; }
    public String selectedFY         { get;set; }
    public String selectedFQ         { get;set; }
    public String preparedByUser     { get;set; }
    public String selectedUserId     { get;set; }
    public String selectedRoleId     { get;set; }

    /**
     *  Constructs a new Specialist Forecast Controller Class
     */
    public SpecialistForecastController() {
        try {
            this.sfLI = new SpecialistForecastlineItem__c();
            this.sfLI.ForecastOwner__c = UserInfo.getUserId();
            preparedbyUser = UserInfo.getName();
            if (selectedFY == null) {
                selectedFY = ForecastHelper.getCurrentFiscalYear();
            }
            if (selectedFQ == null) {
                selectedFQ = ForecastHelper.getCurrentQuarter();
            }
        } catch (Exception ex) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                'Unexpected Report Error: ' + ex.getMessage()));
        }
    }

    // Will need to be updated for month editing.
    /**
     *  @retun true IFF the user is allowed to override/edit forecasts
     */
    public boolean getCanSubmit() {
        return (this.sfLI.ForecastOwner__c == UserInfo.getUserId() &&
            selectedFQ != QTR_ALL);
    }

    /**
     *
     */
    public List<SelectOption> getFiscalYears() {
        if (this.fiscalYears == null) {
            this.setFiscalYears();
        }
        return this.fiscalYears;
    }

    /**
     *  @param fYears is a SelectOption[]
     */
    public void setFiscalYears(List<SelectOption> fYears) {
        this.fiscalYears = fYears;
    }

    /**
     *
     */
    public void setFiscalYears() {
        this.fiscalYears = new List<SelectOption>();
        List<String> fYears = ForecastHelper.getSupportedFiscalYears();

        for(String fYear : fYears) {
            this.fiscalYears.add(new SelectOption(fYear, fYear));
        }
    }

    /**
     *  @return the list of fiscal quaters as a SelectOption[]
     */
    public List<SelectOption> getFiscalQuarters() {
        if (this.fiscalQuarters == null) {
            this.fiscalQuarters = new List<SelectOption>();
            this.fiscalQuarters.add(new SelectOption(QTR_ALL, QTR_ALL));

            List<String> fQuarters = ForecastHelper.getSupportedFiscalQuarters();
            for(String fQuarter : fQuarters) {
                this.fiscalQuarters.add(new SelectOption(fQuarter, fQuarter));
            }
        }
        return this.fiscalQuarters;
    }

    /**
     *  @return
     */
     public SpecialistForecastlineItem__c getSfLI() {
        return this.sfLI;
    }

    /**
     *  @return
     */
    public String getPreparedByUser() {
          return preparedByUser;
    }


    /** */
    public void closeOverridePopup() {
        displayPopup = false;
        initializeOverrideVariables();
    }

    /** */
    public void showOverridePopup() {
        displayPopup = true;
    }

    //View report button
    public PageReference viewSpecialistReport() {
        isvalidated = true;
        allTotaluserIds.add(UserInfo.getUserId());
        try {
            this.getMappedUserInfoFromRoleHierarchy(UserInfo.getUserId());
            if (!allTotaluserIds.contains(sfLI.ForecastOwner__c)) {
                isvalidated = false;
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                    'You do not have permission to view reports for this user.'));
                return null;
            }
            isvalidated = true;
            if (sfLI.ForecastOwner__c != null) {
                User usr = [SELECT Name FROM User
                    WHERE Id = :sfLI.ForecastOwner__c limit 1];
                PreparedbyUser = usr.Name;
            }
        } catch (Exception ex) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                'Unexpected Report Error: ' + ex.getMessage()));
        }
        return null;
    }

    /**
     * Called from userlink for drildown
     */
    public PageReference reportDrildownByUser_Role() {
        if (SelectedUserId != null && SelectedUserId.length() > 10) {
            User usr = [SELECT Name FROM User WHERE Id = :SelectedUserId limit 1];
            PreparedbyUser = usr.Name;

            sfLI.ForecastOwner__c = SelectedUserId;
        }
        return null;
    }

    /**
     * Called from submit button
     */
    public PageReference submitReport() {
        if (selectedFQ != QTR_ALL) {
            String strMonth = ForecastHelper.getMonthsInQuarter(selectedFQ).get(0);
            SpecialistForecastlineItem__c sfLI = new SpecialistForecastlineItem__c();
            sfLI.ForecastOwner__C =  sfLI.ForecastOwner__c;
            sfLI.FiscalYear__c = SelectedFY;
            sfLI.FYMonth__c = strMonth; //This needs to be fixed. TODO
            sfLI.submittedDate__c = System.now() ;
            sfLI.ExtIdentifier__c = sfLI.ForecastOwner__c + SelectedFY + strMonth;
            upsert sfLI ExtIdentifier__c;
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.CONFIRM,
                'Report Submitted successfully'));
       } else {
              ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,
                  'You can\'t submit for quarter "All".' +
                  'Please select a different quarter to submit.'));
       }
       return null;
    }

    /**
     * Called from specialist forecast page to check if they have access to the button
     */
    public boolean getViewAccessToSyncButton() {
        try {
            return ForecastHelper.checkForecastDataSyncAccessForCurrentUser();
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Called from specialist forecast page Refresh Now  Button to get the
     * latest data from SpecialistForecast Object to SpecialistForecast lineItem
     */
    public PageReference refreshSpecialistForecastDataNow() {
        Integer  BATCH_SIZE = 500;
        SpecialistForecastBatch spfBatch = new SpecialistForecastBatch();
        spfBatch.isRunningByUser = true;

        Id batchProcessId = Database.executeBatch(spfBatch, BATCH_SIZE);
        ApexPages.addmessage(new ApexPages.message(ApexPages.severity.CONFIRM,
            'Data refresh request submitted successfully'));
        return null;
    }

    /**
     *  TODO: REFACTOR REFACTOR REFACTOR
     *
     */
    public SpecialistForecastlineItemVO[] getSpecialistforecastlineItemsData() {
        try { // Background check to make sure that we have the necessary data.
            User usr;
            if (sfLI.ForecastOwner__c != null) {
                usr = [SELECT Id, UserRoleId FROM User
                    WHERE Id = :sfLI.ForecastOwner__c limit 1];
            }
            if (sfLI.ForecastOwner__c == null || usr == null ||
            usr.UserRoleId == null) {
                String uid = (usr.Id == null) ? 'unknown.' : (usr.Id + '.');
                throw new SpecialistForecastException(ROLE_ID_NULL + uid);
            }
        } catch (Exception ex) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                'Unexpected Error: ' + ex.getMessage()));
            return null; // kill the method to prevent later exceptions
        }


        Decimal closedAmount                 = 0;
        Decimal commitAmount                 = 0;
        Decimal bestCaseAmount               = 0;
        Decimal commitOvrrideAmount          = 0;
        Decimal bestCaseOverrideAmount       = 0;
        Decimal commitMgrOvrrideAmount       = 0;
        Decimal bestCaseMgrOverrideAmount    = 0;
        Decimal pipelineAmount               = 0;
        Decimal quotaAmount                  = 0;

        Decimal totclosedAmount              = 0;
        Decimal totcommitAmount              = 0;
        Decimal totbestCaseAmount            = 0;
        Decimal totcommitOvrrideAmount       = 0;
        Decimal totbestCaseOverrideAmount    = 0;
        Decimal totpipelineAmount            = 0;
        Decimal totQuotaAmount               = 0;

        Decimal subtotclosedAmount           = 0;
        Decimal subtotcommitAmount           = 0;
        Decimal subtotbestCaseAmount         = 0;
        Decimal subtotcommitOvrrideAmount    = 0;
        Decimal subtotbestCaseOverrideAmount = 0;
        Decimal subtotpipelineAmount         = 0;
        Decimal subtotQuotaAmount            = 0;

        if (!isValidated ||
        !ForecastHelper.checkFieldsSplForecastRepAccessibility()) {
            return null;
        }

        // map used for setting the user and role as key and
        // SpecialistForecastlineItem as the value
        Map<String,SpecialistForecastlineItemVO> mapUserRolelineItems = new
        Map<String,SpecialistForecastlineItemVO>();

        splFClineItems = new List<SpecialistForecastlineItemVO>();

        List<HierarchyUtil.RoleNodeWrapper> lstRoles;
        // get the searchuser and all level reporting users
        mapSecondLeverUsersAndChild.clear();
        mapUsers.clear();
        getMappedUserInfoFromRoleHierarchy(sfLI.ForecastOwner__c);

        SpecialistForecastlineItemVO sfLIne = new SpecialistForecastlineItemVO();

        Set<Id> ownerIdsforQuery = new Set<Id>();

        for (Id id : mapSecondLeverUsersAndChild.keyset()) {
            ownerIdsforQuery.addAll(mapSecondLeverUsersAndChild.get(id));
        }

        ownerIdsforQuery.add(sfLI.ForecastOwner__c);

        // set the query parameters
        ForecastReportQueryParam queryParam = new ForecastReportQueryParam();
        queryParam.setFiscalYear(selectedFY);
        queryParam.setMonthsInQrtr(ForecastHelper.getMonthsInQuarter(selectedFQ));
        queryParam.setOwnerIds(ownerIdsforQuery);

        // get the data from the table as a list
        List<SpecialistForecastlineItem__c> listSplFCastlineItems =
        ForecastHelper.getSpecialistForecastLinesTable(queryParam );

        // get Quota information for all the users
        AggregateResult[] listSplFCastQuotas =
        ForecastHelper.getSpecialistForecastQuota(queryParam );

        // Consolidate data
        SpecialistForecastlineItemVO lineItem;

        Map<Id,SpecialistForecastlineItemVO> ownerlineItemsMap = new
        Map<Id,SpecialistForecastlineItemVO>();

        /********************** consolidation Start *************************/
        // Agreegate the records by owner for display as we are having one
        //  record for a quater and we store as months

        ownerlineItemsMap = aggregatelineItemsByOwner(listSplFCastlineItems);
        for (Sobject so : listSplFCastQuotas) {
            AggregateResult ar = (AggregateResult) so;
            Id ownerId = (Id) ar.get('SpecialistForecastUser__c');
            Decimal tempAmount = (Decimal) ar.get('amnt');
            lineItem = ownerlineItemsMap.get(OwnerId);

            if (lineItem != null) {
                lineItem.quotaAmount = tempAmount ;
                ownerlineItemsMap.put(OwnerId, lineItem);
            }
        }
        // ********************** consolidation End ***************************

        boolean haslineItemstoDisplay = false;
        // consolidate child records with 2nd level roles/users
        for (Id lineItemOwnerId : mapSecondLeverUsersAndChild.keySet()) {
            sfLIne = new SpecialistForecastlineItemVO();
            Boolean mgrCommitOverride = false;
            Boolean mgrBestCaseOverride = false;

            // TODO Refactor nested loops?
            for (Id childLinesOwnerId : mapSecondLeverUsersAndChild.get(lineItemOwnerId)) {
                lineItem = ownerlineItemsMap.get(childLinesOwnerId);
                if (lineItem != null) {
                    haslineItemstoDisplay = true;
                    closedAmount = zeroIfNull(lineItem.closedAmount);
                    // if manager override amount exists then display mgroverrideamt
                    commitAmount = zeroIfNull(lineItem.commitAmount);

                    commitOvrrideAmount = (lineItem.mgrCommitOverrideAmount !=
                        null ? lineItem.mgrCommitOverrideAmount : commitAmount);
                    bestCaseAmount = zeroIfNull(lineItem.bestCaseAmount);
                    bestCaseOverrideAmount = (lineItem.mgrBestCaseOverrideAmount
                        != null ? lineItem.mgrBestCaseOverrideAmount : bestCaseAmount);
                    pipelineAmount = zeroIfNull(lineItem.pipelineAmount);
                    quotaAmount = zeroIfNull(lineItem.quotaAmount);

                    // lineItem.mgrCommitOverridden = true;
                    // add to subtotal amount variables
                    if (commitOvrrideAmount > 0 && commitOvrrideAmount != commitAmount
                        && childLinesOwnerId != lineItemOwnerId) {
                        commitAmount = commitOvrrideAmount;
                    }
                    subtotcommitAmount += (lineItem.mycommitOverridden
                        ? lineItem.commitOverrideAmount : commitAmount);
                    subtotclosedAmount += closedAmount;

                    if (bestCaseOverrideAmount != bestCaseAmount &&
                        bestCaseOverrideAmount > 0 && childLinesOwnerId != lineItemOwnerId) {
                        bestCaseAmount = bestCaseOverrideAmount;
                    }

                    subtotbestCaseAmount += (lineItem.MyBestcaseOverridden ?
                        lineItem.bestCaseOverrideAmount : bestCaseAmount);
                    subtotpipelineAmount += pipelineAmount;
                    subtotQuotaAmount    += quotaAmount;

                    // manager overrides the value then diplay only that amount
                    //  no calculation else aggregation of amount will be
                    //  displayed for the user
                    if (!mgrCommitOverride && childLinesOwnerId != lineItemOwnerId) {
                        subtotcommitOvrrideAmount += (commitOvrrideAmount <= 0
                            ? commitAmount : commitOvrrideAmount);
                    } else {
                        mgrCommitOverride = true;
                        subtotcommitOvrrideAmount = (commitOvrrideAmount <= 0
                            ? commitAmount : commitOvrrideAmount);
                    }

                    if (!mgrBestCaseOverride && childLinesOwnerId != lineItemOwnerId) {
                        subtotbestCaseOverrideAmount += (bestCaseOverrideAmount
                            <= 0 ? bestCaseAmount : bestCaseOverrideAmount);
                    } else {
                        mgrBestCaseOverride = true;
                        subtotbestCaseOverrideAmount = (bestCaseOverrideAmount
                            <= 0 ? bestCaseAmount : bestCaseOverrideAmount);
                    }
                }
                closedAmount           = 0;
                commitAmount           = 0;
                bestCaseAmount         = 0;
                commitOvrrideAmount    = 0;
                bestCaseOverrideAmount = 0;
                pipelineAmount         = 0;
                quotaAmount            = 0;
            }

            //if not search user then assign values
            if (mapUsers.size() > 0 && haslineItemstoDisplay &&
                mapUsers.containsKey(lineItemOwnerId) &&
                lineItemOwnerId != sfLI.ForecastOwner__c) {

                sfLIne.withoutLink = false;
                sfLIne.reportingUser = false;
                sfLIne.forOverride = (sfLI.ForecastOwner__c == UserInfo.getUserId()
                                       && selectedFQ != ForecastHelper.QTR_ALL);
                sfLIne.userName =  mapUsers.get(lineItemOwnerId).userName;
                sfLIne.roleName = mapUsers.get(lineItemOwnerId).roleName;
                sfLIne.userId =  mapUsers.get(lineItemOwnerId).userId;
                sfLIne.roleId = mapUsers.get(lineItemOwnerId).roleId;
                sfLIne.closedAmount = subtotclosedAmount;
                sfLIne.commitAmount = subtotcommitAmount;
                sfLIne.bestCaseAmount = subtotbestCaseAmount;
                sfLIne.pipelineAmount = subtotpipelineAmount;
                sfLIne.quotaAmount = subTotQuotaAmount;

                lineItem = ownerlineItemsMap.get(lineItemOwnerId);
                if (lineItem != null) {
                    sfLIne.commitOverrideAmount = (!lineItem.mgrCommitOverridden
                        ? subtotcommitAmount : subtotcommitOvrrideAmount);
                    sfLIne.bestCaseOverrideAmount = (!lineItem.mgrBestCaseOverridden
                        ? subtotbestCaseAmount : subtotbestCaseOverrideAmount);
                    sfLIne.myCommitOverridden = (lineItem.mgrCommitOverridden);
                    sfLIne.myBestcaseOverridden = (lineItem.mgrBestCaseOverridden);
                    sfLIne.strLineItmId = ownerlineItemsMap.get(lineItemOwnerId).strLineItmId;
                    sfLIne.isSubmitted = (lineItem.isSubmitted || sfLIne.isSubmitted);
                    sfLIne.submittedDate = lineItem.submittedDate;
                    sfLIne.mgrCommitOverrideNotes = lineItem.mgrCommitOverrideNotes;
                    sfLIne.mgrBestCaseOverrideNotes = lineItem.mgrBestCaseOverrideNotes;
                    sfLIne.prevDirectRepCommitAmount = lineItem.prevDirectRepCommitAmount;
                    sfLIne.prevDirectRepBestcaseAmount = lineItem.prevDirectRepBestcaseAmount;

                } else {
                    sfLIne.commitOverrideAmount = subtotcommitAmount;
                    sfLIne.bestCaseOverrideAmount = subtotbestCaseAmount;
                    sfLIne.MycommitOverridden = false;
                    sfLIne.MyBestcaseOverridden = false;
                    sfLIne.strLineItmId = '';
                }

                splFClineItems.add(sfLIne);

                totcommitAmount           += sfLIne.commitAmount;
                totclosedAmount           += sfLIne.closedAmount;
                totbestCaseAmount         += sfLIne.bestCaseAmount;
                totpipelineAmount         += sfLIne.pipelineAmount;
                totQuotaAmount            += sfLIne.quotaAmount;
                totbestCaseOverrideAmount += sfLIne.bestCaseOverrideAmount;
                totcommitOvrrideAmount    += sfLIne.commitOverrideAmount;

            } else if (mapUsers.size() > 0 && !haslineItemstoDisplay &&
            mapUsers.containsKey(lineItemOwnerId)) {
                sfLIne.createEmptySPlineItemforVFPage(lineItemOwnerId,
                    sfLI.ForecastOwner__c,mapUsers,quotaAmount);
                sfLIne.forOverride = (selectedFQ != ForecastHelper.QTR_ALL
                    ? sfLIne.forOverride : false);
                splFClineItems.add(sfLIne);
            } //end if

            haslineItemstoDisplay        = false;
            subtotclosedAmount           = 0;
            subtotcommitAmount           = 0;
            subtotbestCaseAmount         = 0;
            subtotcommitOvrrideAmount    = 0;
            subtotbestCaseOverrideAmount = 0;
            subtotpipelineAmount         = 0;
            subtotQuotaAmount            = 0;

        } // end for

        // create search user line items
        if (ownerlineItemsMap.containsKey(sfLI.ForecastOwner__c) &&
            ownerlineItemsMap.get(sfLI.ForecastOwner__c) != null) {

            lineItem               = ownerlineItemsMap.get(sfLI.ForecastOwner__c);
            closedAmount           = zeroIfNull(lineItem.closedAmount);
            commitAmount           = zeroIfNull(lineItem.commitAmount);
            bestCaseAmount         = zeroIfNull(lineItem.bestCaseAmount);
            pipelineAmount         = zeroIfNull(lineItem.pipelineAmount);
            quotaAmount            = zeroIfNull(lineItem.quotaAmount);
            commitOvrrideAmount    = ((lineItem.CommitOverrideAmount != null &&
                lineItem.CommitOverrideAmount > 0) ?
                lineItem.CommitOverrideAmount : commitAmount);
            bestCaseOverrideAmount = ((lineItem.bestCaseOverrideAmount != null
                && lineItem.bestcaseOverrideAmount > 0) ?
                    lineItem.bestCaseOverrideAmount : bestCaseAmount);

            sfLIne.withoutLink   = false;
            sfLIne.reportingUser = false;
            sfLIne.forOverride   = (sfLI.ForecastOwner__c == UserInfo.getUserId()
                                   && selectedFQ != QTR_ALL);

            sfLIne.userName = mapUsers.get(sfLI.ForecastOwner__c).userName;
            sfLIne.roleName = mapUsers.get(sfLI.ForecastOwner__c).roleName;
            sfLIne.userId   = mapUsers.get(sfLI.ForecastOwner__c).userId;
            sfLIne.roleId   = mapUsers.get(sfLI.ForecastOwner__c).roleId;
            sfLIne.closedAmount           = closedAmount;
            sfLIne.commitAmount           = commitAmount;
            sfLIne.bestCaseAmount         = bestCaseAmount;
            sfLIne.commitOverrideAmount   = commitOvrrideAmount;
            sfLIne.bestCaseOverrideAmount = bestCaseOverrideAmount;
            sfLIne.pipelineAmount         = pipelineAmount;
            sfLIne.quotaAmount            = quotaAmount;
            sfLIne.myCommitOverridden     = lineItem.MycommitOverridden;
            sfLIne.myBestcaseOverridden   = lineItem.MyBestcaseOverridden;
            sfLIne.strLineItmId           = lineItem.strLineItmId;
            sfLIne.searchUser             = true;

            //add the total amounts to the list
            splFClineItems.add(sfLIne);

            totcommitAmount           += commitAmount;
            totclosedAmount           += closedAmount;
            totbestCaseAmount         += bestCaseAmount;
            totpipelineAmount         += pipelineAmount;
            totQuotaAmount            += quotaAmount;
            totbestCaseOverrideAmount += bestCaseOverrideAmount;
            totcommitOvrrideAmount    += commitOvrrideAmount;

        } else {
            sfLIne = new SpecialistForecastlineItemVO();
            sfLIne.createEmptySPlineItemforVFPage(sfLI.ForecastOwner__c,
                sfLI.ForecastOwner__c, mapUsers, quotaAmount);
            sfLIne.forOverride = (selectedFQ != QTR_ALL ? sfLIne.forOverride : false);
            splFClineItems.add(sfLIne);
        }


        // create total line items
        sfLIne = new SpecialistForecastlineItemVO();
        sfLIne.withoutLink            = true;
        sfLIne.reportingUser          = true;
        sfLIne.userName               = GRAND_TOTAL;
        sfLIne.closedAmount           = totclosedAmount;
        sfLIne.commitAmount           = totcommitAmount;
        sfLIne.bestCaseAmount         = totbestCaseAmount;
        sfLIne.commitOverrideAmount   = totcommitOvrrideAmount ;
        sfLIne.bestCaseOverrideAmount = totbestCaseOverrideAmount;
        sfLIne.pipelineAmount         = totpipelineAmount;
        sfLIne.quotaAmount            = totQuotaAmount;

        //add the total amounts Line to the list
        splFClineItems.add(sfLIne);

        return splFClineItems;
    }


    /**
     *  Get all the users added into the  allUserIds and put the RoleInfo with
     *  UserId Key for each displayable user
     *  @param UserID is a SFDC ID
     *  @throws SpecialistForecastException
     */
    private void getMappedUserInfoFromRoleHierarchy(Id userId) {
        User usr;
        try {
            usr = [SELECT Name, UserRoleId, UserRole.Name
                    FROM User WHERE Id = :userId];
            if (usr == null || usr.UserRoleID == null) {
                throw new SpecialistForecastException();
            }
        } catch (Exception ex) {
            throw new SpecialistForecastException(ROLE_ID_NULL + userID + '.');
        }

        HierarchyUtil.RoleNodeWrapper rolwraps;
        try {
             rolwraps = HierarchyUtil.getMappedAllLevelSubordinatesByRole(
                    usr.UserRoleId);
        } catch (Exception ex) {
            return;
        }

        RoleUserInfoVO ruinfo;
        List<HierarchyUtil.RoleNodeWrapper> lstchildUserNodes =
            rolwraps.myChildNodes;

        if (lstchildUserNodes != null) {
            for (HierarchyUtil.RoleNodeWrapper rolewrapper : lstchildUserNodes) {
                if (roleWrapper.myUsers != null
                && roleWrapper.myUsers.size() == 1) {
                    alluserIds = new Set<Id>();
                    ruinfo = new RoleUserInfoVO();
                    ruinfo.roleId = rolewrapper.myRoleId;
                    ruinfo.roleName = rolewrapper.myroleName;
                    ruinfo.userId = rolewrapper.myUsers.get(0).Id;
                    ruinfo.userName = rolewrapper.myUsers.get(0).Name;
                    mapUsers.put(rolewrapper.myUsers.get(0).Id,ruinfo);
                    /* Put the key with the child*/
                    roleTraverse (rolewrapper);

                    alluserIds.add(rolewrapper.myUsers.get(0).Id);
                    allTotaluserIds.add(rolewrapper.myUsers.get(0).Id);
                    mapSecondLeverUsersAndChild.put(rolewrapper.myUsers.get(0).Id,alluserIds);

                }
                // if manager and many users assigned to the role then get only mapped user
                if (roleWrapper.myUsers != null && roleWrapper.myUsers.size() > 1
                && !roleWrapper.isLeafNode) {
                    alluserIds = new Set<Id>();

                    ruinfo            = new RoleUserInfoVO();
                    ruinfo.roleId     = rolewrapper.myRoleId;
                    ruinfo.roleName   = rolewrapper.myroleName;
                    ruinfo.userId     = rolewrapper.myMappedUser.Id;
                    ruinfo.userName   = rolewrapper.myMappedUser.Name;
                    mapUsers.put(rolewrapper.myMappedUser.Id,ruinfo);
                    /* Put the key with the child*/
                    roleTraverse(rolewrapper);

                    alluserIds.add(rolewrapper.myMappedUser.Id);
                    allTotaluserIds.add(rolewrapper.myMappedUser.Id);
                    mapSecondLeverUsersAndChild.put(rolewrapper.myMappedUser.Id,
                        alluserIds);
                }
                //if leafnode role then get all users
                if (roleWrapper.myUsers != null && roleWrapper.myUsers.size() > 1
                && roleWrapper.isLeafNode) {
                    for(User myusr : rolewrapper.myUsers) {
                        alluserIds = new Set<Id>();
                        ruinfo = new RoleUserInfoVO();
                        ruinfo.roleId = rolewrapper.myRoleId;
                        ruinfo.roleName = rolewrapper.myroleName;
                        ruinfo.userId = myusr.Id;
                        ruinfo.userName = myusr.Name;
                        mapUsers.put(myusr.Id,ruinfo);
                        /* Put the key with the child*/
                        roleTraverse (rolewrapper);

                        alluserIds.add(myusr.Id);
                        allTotaluserIds.add(myusr.Id);
                        mapSecondLeverUsersAndChild.put(myusr.Id,alluserIds);

                    }//end for
                }//end if
            }//end for
        }//end if

        // mapped/search user
        ruinfo = new RoleUserInfoVO();
        ruinfo.roleId = usr.UserRoleId;
        ruinfo.roleName = usr.userrole.Name;
        ruinfo.userId = userId;
        ruinfo.userName  = usr.Name;
        mapUsers.put(userId,ruinfo);
    }

     /**
      * Recursively traverse a given roleId
      *
      * @param rolewrapper user/role indicates the starting point of traversal
      */
     private void roleTraverse(HierarchyUtil.RoleNodeWrapper rolewrapper) {

         if (rolewrapper.myChildNodes != null) {
             for (HierarchyUtil.RoleNodeWrapper rw : rolewrapper.myChildNodes){
                 if (rw.myUsers != null && rw.myUsers.size() > 1 && rw.isLeafNode) {
                     for(User usr : rw.myUsers){
                         alluserIds.add(usr.Id);
                         allTotaluserIds.add(usr.Id);
                     }
                 }
                 if (rw.myUsers != null && rw.myUsers.size() == 1) {
                     alluserIds.add(rw.myUsers.get(0).Id);
                     allTotaluserIds.add(rw.myUsers.get(0).Id);
                 }
                 if (rw.myUsers != null && rw.myUsers.size() > 1 &&
                 !rw.isLeafNode) {
                        alluserIds.add(rw.myMappedUser.Id);
                        allTotaluserIds.add(rw.myMappedUser.Id);
                }
                if (rw.hasChildren) {
                    roletraverse (rw);
                }

               //}//end if
           } //end for
       } //end if
    }

    /**
     *  Agreegate the records by owner for display as we are having one record
     *  for a quater and  we store as months
     *  @param List of SpecialistForecastlineItem__c
     */
    private Map<Id,SpecialistForecastlineItemVO> aggregatelineItemsByOwner(
        List<SpecialistForecastlineItem__c> listSplFCastlineItems) {

        Map<Id,SpecialistForecastlineItemVO> ownerlineItemsMap =
            new Map<Id,SpecialistForecastlineItemVO>();
        Decimal closedAmount   = 0;
        Decimal commitAmount   = 0;
        Decimal bestCaseAmount = 0;
        Decimal pipelineAmount = 0;
        Decimal commitOverrideAmount;
        Decimal bestCaseOverrideAmount;
        Decimal commitMgrOvrrideAmount;
        Decimal bestCaseMgrOverrideAmount;

        SpecialistForecastlineItemVO splv;
        String currentOwnerId;
        String previousOwnerId;

        for (SpecialistForecastlineItem__c sfl : listSplFCastlineItems) {
            currentOwnerId = sfl.ForecastOwner__c;

            closedAmount   = zeroIfNull(sfl.originalClosedAmount__c);
            commitAmount   = zeroIfNull(sfl.originalCommitAmount__c);
            bestCaseAmount = zeroIfNull(sfl.originalbestCaseAmount__c);
            pipelineAmount = zeroIfNull(sfl.originalPipelineAmount__c);

            commitOverrideAmount =
                zeroIfNull(sfl.SpecialistForecastOverride__r.ownOverriddenCommitAmount__c);
            bestCaseOverrideAmount =
                zeroIfNull(sfl.SpecialistForecastOverride__r.ownOverriddenBestcaseAmount__c);
            commitMgrOvrrideAmount =
                zeroIfNull(sfl.SpecialistForecastOverride__r.managerOverriddenCommitAmount__c);
            bestCaseMgrOverrideAmount =
                zeroIfNull(sfl.SpecialistForecastOverride__r.managerOverriddenBestcaseAmount__c);


            splv = ownerlineItemsMap.get(currentOwnerId);
            if (splv == null) {
                splv = new SpecialistForecastlineItemVO();
            }

            if (previousOwnerId != currentOwnerId) {
                 previousOwnerId = currentOwnerId;
                 splv.closedAmount = closedAmount;
                 splv.commitAmount = commitAmount;
                 splv.bestCaseAmount = bestCaseAmount;
                 splv.pipelineAmount = pipelineAmount;
                 splv.commitOverrideAmount = commitOverrideAmount;
                 splv.bestCaseOverrideAmount = bestCaseOverrideAmount;
                 splv.mgrCommitOverrideAmount = commitMgrOvrrideAmount;
                 splv.mgrBestCaseOverrideAmount = bestCaseMgrOverrideAmount;
                 splv.submittedDate = (sfl.submitteddate__c != null ?
                     sfl.submitteddate__c.format() : zeroIfNull(splv.submittedDate));
                 splv.isSubmitted = (sfl.Submitteddate__c != null || 
                     (splv.isSubmitted == null ? false : splv.isSubmitted));

                 splv.MycommitOverridden =
                     sfl.SpecialistForecastOverride__r.OwnOverriddenCommitAmount__c != null;
                 splv.MyBestcaseOverridden =
                     sfl.SpecialistForecastOverride__r.OwnOverriddenBestcaseAmount__c != null;
                 splv.mgrCommitOverridden =
                     sfl.SpecialistForecastOverride__r.ManagerOverriddenCommitAmount__c != null;
                 splv.mgrBestCaseOverridden =
                     sfl.SpecialistForecastOverride__r.ManagerOverriddenBestcaseAmount__c != null;
                 splv.mgrCommitOverrideNotes =
                     sfl.SpecialistForecastOverride__r.ManagerOverrideCommitNotes__c;
                 splv.mgrBestCaseOverrideNotes =
                     sfl.SpecialistForecastOverride__r.ManagerOverrideBestcaseNotes__c;
                 splv.prevDirectRepCommitAmount =
                     sfl.SpecialistForecastOverride__r.PreviousDirectRepCommitAmount__c;
                 splv.prevDirectRepBestcaseAmount =
                     sfl.SpecialistForecastOverride__r.PreviousDirectRepBestcaseAmount__c;
            } else {
                splv.closedAmount              += closedAmount;
                splv.commitAmount              += commitAmount;
                splv.bestCaseAmount            += bestCaseAmount;
                splv.pipelineAmount            += pipelineAmount;
                splv.commitOverrideAmount      += commitOverrideAmount;
                splv.bestCaseOverrideAmount    += bestCaseOverrideAmount;
                splv.mgrCommitOverrideAmount   += commitMgrOvrrideAmount;
                splv.mgrBestCaseOverrideAmount += bestCaseMgrOverrideAmount;
                splv.MycommitOverridden =
                    (sfl.SpecialistForecastOverride__r.OwnOverriddenCommitAmount__c
                    != null || splv.MycommitOverridden);
                splv.MyBestcaseOverridden =
                    (sfl.SpecialistForecastOverride__r.OwnOverriddenBestcaseAmount__c
                    != null || splv.MyBestcaseOverridden);
                splv.mgrCommitOverridden =
                    (sfl.SpecialistForecastOverride__r.ManagerOverriddenCommitAmount__c
                    != null ||splv.mgrCommitOverridden);
                splv.mgrBestCaseOverridden =
                    (sfl.SpecialistForecastOverride__r.ManagerOverriddenBestcaseAmount__c
                    != null ||splv.mgrBestCaseOverridden);
                splv.submittedDate = (sfl.submitteddate__c != null ?
                    sfl.submitteddate__c.format() : zeroIfNull(splv.submittedDate));
                splv.isSubmitted = (sfl.Submitteddate__c != null ||
                    splv.isSubmitted);
                splv.mgrCommitOverrideNotes = (splv.mgrCommitOverridden ?
                    sfl.SpecialistForecastOverride__r.ManagerOverrideCommitNotes__c : '');
                splv.mgrBestCaseOverrideNotes = (splv.mgrBestCaseOverridden ?
                    sfl.SpecialistForecastOverride__r.ManagerOverrideBestcaseNotes__c : '');
                splv.prevDirectRepCommitAmount = (splv.mgrCommitOverridden ?
                    sfl.SpecialistForecastOverride__r.PreviousDirectRepCommitAmount__c : 0);
                splv.prevDirectRepBestcaseAmount = (splv.mgrBestCaseOverridden ?
                    sfl.SpecialistForecastOverride__r.PreviousDirectRepBestcaseAmount__c : 0);
            } //end if
            commitMgrOvrrideAmount = 0;
            commitOverrideAmount = 0;
            ownerlineItemsMap.put(currentOwnerId,splv);

        } //end for

        return ownerlineItemsMap;
    }

    /**
     *
     */
    private void setRoleUserValue(HierarchyUtil.RoleNodeWrapper rolewrapper,
    User myusr) {

        RoleUserInfoVO ruinfo = new RoleUserInfoVO();
        ruinfo.roleId = rolewrapper.myRoleId;
        ruinfo.roleName = rolewrapper.myroleName;
        ruinfo.userId = myusr.Id;
        ruinfo.userName = myusr.Name;
        mapUsers.put(myusr.Id,ruinfo);
        /* Put the key with the child */
        roleTraverse(rolewrapper);
        alluserIds.add(myusr.Id);
        allTotaluserIds.add(myusr.Id);
        mapSecondLeverUsersAndChild.put(myusr.Id,alluserIds);

    }

    /***************    Specialist Forecast Override     **********************/
    /**
     *
     */
    public PageReference saveOverride() {

        SpecialistForecastlineItem__c spc = new SpecialistForecastlineItem__c();
        SpecialistForecastOverride__c spovr = new SpecialistForecastOverride__c();
        String external_Key;
        if (strField == COMMIT_MSG) {
            if (isSelfOverride()) {
                spovr.OwnOverriddenCommitAmount__c = overrideAmount;
                spovr.OwnOverrideNotes__c = overridecomment;
            } else {
                spovr.ManagerOverriddenCommitAmount__c = overrideAmount;
                spovr.ManagerOverrideCommitNotes__c = overridecomment;
            }
            spovr.PreviousDirectRepCommitAmount__c = originaloldAmount;
        } else if (strField =='bestcase') {
            if (isSelfOverride()) {
                spovr.OwnOverriddenBestCaseAmount__c = overrideAmount;
                spovr.OwnOverrideNotes__c = overrideComment;
            } else {
                spovr.ManagerOverriddenBestCaseAmount__c = overrideAmount;
                spovr.ManagerOverrideBestcaseNotes__c = overrideComment;
            }
            spovr.PreviousDirectRepBestcaseAmount__c = originaloldAmount;
        }

       spovr.PeriodType__c = selectedFQ;
       external_Key = lineItemOwnerId + SelectedFY + selectedFQ;
       spovr.ExtIdentifier__c = external_Key;
       spovr.OwnerId = lineItemOwnerId;

       try {
           Upsert spovr ExtIdentifier__c;
       } catch(System.DMLException ex) {
           ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
               'Failed to update the Override value'));
           return null;
        }

        //upsert Line item
        String[] lstMonths = ForecastHelper.getMonthsInQuarter(selectedFQ);

        spc.ExtIdentifier__c = lineItemOwnerId + SelectedFY + lstMonths.get(0);
        spc.SpecialistForecastOverride__c = spovr.Id;
        spc.FYMonth__c = lstMonths.get(0);
        spc.ForecastOwner__c = lineItemOwnerId;
        spc.FiscalYear__c = SelectedFY;
        spc.OwnerId = lineItemOwnerId;
        try {
            Upsert spc ExtIdentifier__c;
        } catch(System.DMLException ex) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                'Failed to update the Override Amount'));
            return null;
        }
        ApexPages.addmessage(new ApexPages.message(ApexPages.severity.CONFIRM,
            'Override Amount updated successfully'));
        return null;
    }

    /**
     *
     */
    private boolean isSelfOverride() {
        return UserInfo.getUserId() == lineItemOwnerId;
     }

     /**
      *
      */
     private void initializeOverrideVariables() {
         overrideAmount = null;
         originalAmount = 0;
         originaloldAmount = 0;
         overrideComment = '';
         strRecordId = '';
         strField = '';
         lineItemOwnerId = '';
     }
     /***************  Specialist Forecast Override End  **********************/

     /***************    Specialist Forecast DrillDown   **********************/
     public List<SpecialistForecast__c> specialistforecastList{ get;set; }

     public boolean displayDrilDownPopup {get; set;}

     /**
      *
      */
     public void closeDrilDownPopup() {
         displayDrilDownPopup = false;
     }

     /**
      *
      */
     public void showDrilDownPopup() {
         if (selectedFQ != null && selectedFY != null && lineItemOwnerId != null) {
             this.getSpecialistForecastRecords();
         }
         displayDrilDownPopup = true;
     }

    /**
     *
     */
    private void getSpecialistForecastRecords() {
        List<String> lstStatus = new List<String>();
        lstStatus.add(strField);

        if (strField == CLOSED) { lstStatus.add(CLOSED_WON); }

        List<String> lstMonths = ForecastHelper.getMonthsInQuarter(selectedFQ);
        // get months by quarter

        specialistforecastList = [SELECT Opportunity__C, Opportunity__r.Name,
                                    Account__c, FiscalYear__c, FYMonth__c,
                                    ForecastAmount__c, ForecastStatus__c,
                                    ForecastCloseDate__c, ForecastStage__c
                                    FROM SpecialistForecast__c
                                    WHERE FiscalYear__c = :SelectedFY
                                    AND FYMonth__c in :lstMonths
                                    AND OwnerId = :lineItemOwnerId
                                    AND ForecastStatus__c in :lststatus];
    }

    private static Integer zeroIfNull(Integer inp) {
        return inp == null ? 0 : inp;
    }

    private static Decimal zeroIfNull(Decimal inp) {
        return inp == null ? 0 : inp;
    }

    private static String zeroIfNull(String inp) {
        return inp == null ? '' : inp;
    }

}
