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
 *  @see BaseSpecialForecastController
 *  @author auzzaman,smohapatra
 *  @since 180.store
 */
public with sharing class SpecialistForecastController extends
    BaseSpecialistForecastController {

    public with sharing class SpecialistForecastControllerException extends
        Exception {}

    private Set<Id> alluserIds = new Set<Id>();
    private Set<Id> allTotaluserIds = new Set<Id>();
    private Map<Id,set<Id>> mapSecondLeverUsersAndChild = new Map<Id,set<Id>>();
    private Map<Id,RoleUserInfoVO> mapUsers = new Map <Id,RoleUserInfoVO>();
    private Boolean isvalidated = true;

    public boolean displayPopup { get;set; }

    public Decimal overrideAmount    { get;set; }
    public Decimal originalAmount    { get;set; }
    public Decimal originaloldAmount { get;set; }
    public String overrideComment    { get;set; }
    public String strRecordId        { get;set; }
    public String strField           { get;set; }
    public String lineItemOwnerId    { get;set; }

    public String fiscalYear         { get;set; }
    public String fiscalQuarter      { get;set; }
    public String SelectedFY         { get;set; }
    public String SelectedFQ         { get;set; }
    public String PreparedByUser     { get;set; }
    public String SelectedUserId     { get;set; }
    public String SelectedRoleId     { get;set; } //Not Used?

    public SpecialistForecastlineItem__c sfLI;
    private List<SpecialistForecastlineItemVO> splFClineItems;

    private List<SelectOption> fiscalYears;
    private List<SelectOption> fiscalQuarters;

    /**
     *  Constructs a new Specialist Forecast Controller Class
     */
    public SpecialistForecastController () {
        this.sfLI = new SpecialistForecastlineItem__c ();
        this.sfLI.ForecastOwner__c = UserInfo.getUserId();
        PreparedbyUser = UserInfo.getName();
        if (selectedFY == null) {
            selectedFY = ForecastHelper.getCurrentFiscalYear();
        }
        if (selectedFQ == null) {
            selectedFQ = ForecastHelper.getCurrentQuarter();
        }
        // TODO This is probably best spot to setup FY start month.\
        // Place query in SfLI class?
    }

    // Will need to be updated for month editing.
    /**
     *  @retun true IFF the user is allowed to override/edit forecasts
     */
    public boolean getCanSubmit() {
        return (this.sfLI.ForecastOwner__c == UserInfo.getUserId() &&
            SelectedFQ != ForecastHelper.FISCAL_QUARTER_ALL);
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
     *  @parak fYears is a SelectOption[]
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
     * @return the list of fiscal quaters as a SelectOption[]
     */
    public List<SelectOption> getFiscalQuarters() {
        if (this.fiscalQuarters == null) {
            this.fiscalQuarters = new List<SelectOption>();
            this.fiscalQuarters.add(new SelectOption(ForecastHelper.FISCAL_QUARTER_ALL,
                ForecastHelper.FISCAL_QUARTER_ALL));

            List<String> fQuarters = ForecastHelper.getSupportedFiscalQuarters();
            for(String fQuarter : fQuarters) {
                this.fiscalQuarters.add(new SelectOption(fQuarter, fQuarter));
            }
        }
        return this.fiscalQuarters;
    }

    /**
     * @return
     */
     public SpecialistForecastlineItem__c getSfLI() {
        return this.sfLI;
    }

    /**
     * @return
     */
    public String getPreparedByUser() {
          return PreparedByUser;
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
            this.getMappedUserInfoFROMRoleHierarchy(UserInfo.getUserId());
            if (!allTotaluserIds.contains(sfLI.ForecastOwner__c)) {
                isvalidated = false;
                //TODO - Convert the error message to custom label
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                    ' You do not have privileges to view data for this user ?' +
                    ' Not in Role hierarchy / Below user in role hierarchy'));
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
                'Unexpected Report Error'));
        }
        return null;
    }

    /**
     * Called from userlink for drildown
     */
    public PageReference reportDrildownByUser_Role(){
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
        if (SelectedFQ != ForecastHelper.FISCAL_QUARTER_ALL) {
            String strMonth = ForecastHelper.getMonthsInQuarter(SelectedFQ).get(0);
            SpecialistForecastlineItem__c splLI = new SpecialistForecastlineItem__c();
            splLI.ForecastOwner__C =  sfLI.ForecastOwner__c;
            splLI.FiscalYear__c = SelectedFY ;
            splLI.FYMonth__c = strMonth ;
            splLI.submittedDate__c = System.now() ;
            splLI.ExtIdentifier__c = sfLI.ForecastOwner__c + SelectedFY + strMonth;
            upsert splLI ExtIdentifier__c;
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.CONFIRM,
                'Report Submitted successfully'));
       } else {
              ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,
                  'You cann\'t submit for quarter "All".' +
                  'Please SELECT a different quarter to submit.'));
       }

        return null;
    }

    /**
     * Called from specialist forecast page to check if they have access to the button
     */
    public boolean getViewAccessToSyncButton() {
        boolean hasAccess = false;
        try {
            hasAccess = ForecastHelper.CheckForecastDataSyncAccessForCurrentUser();
        } catch (Exception ex) {
            //return false if any error FIXME
            return false;
        }
        return hasAccess;
    }

    /**
     * Called from specialist forecast page Refresh Now  Button to get the
     * latest data from SpecialistForecast Object to SpecialistForecast lineItem
     */
    public PageReference  refreshSpecialistForecastDataNow() {
        Integer  BATCH_SIZE = 500;
        SpecialistForecastBatch spfBatch = new SpecialistForecastBatch();
        spfBatch.isRunningByUser = true;

        Id batchProcessId = Database.executeBatch(spfBatch, BATCH_SIZE);
        ApexPages.addmessage(new ApexPages.message(ApexPages.severity.CONFIRM,
            'Data refresh request submitted successfully'));

        return null;
    }


    /**
     *  
     * 
     */
    public SpecialistForecastlineItemVO[] getSpecialistforecastlineItemsData() {
       Decimal closedAmount              = 0;
       Decimal commitAmount              = 0;
       Decimal bestCaseAmount            = 0;
       Decimal commitOvrrideAmount       = 0;
       Decimal bestCaseOverrideAmount    = 0;
       Decimal commitMgrOvrrideAmount    = 0;
       Decimal bestCaseMgrOverrideAmount = 0;
       Decimal pipelineAmount            = 0;
       Decimal quotaAmount               = 0;

       Decimal totclosedAmount           = 0;
       Decimal totcommitAmount           = 0;
       Decimal totbestCaseAmount         = 0;
       Decimal totcommitOvrrideAmount    = 0;
       Decimal totbestCaseOverrideAmount = 0;
       Decimal totpipelineAmount         = 0;
       Decimal totQuotaAmount            = 0;

       Decimal  subtotclosedAmount           = 0;
       Decimal  subtotcommitAmount           = 0;
       Decimal  subtotbestCaseAmount         = 0;
       Decimal  subtotcommitOvrrideAmount    = 0;
       Decimal  subtotbestCaseOverrideAmount = 0;
       Decimal  subtotpipelineAmount         = 0;
       Decimal  subtotQuotaAmount            = 0;

       if (!isValidated ||
       !ForecastHelper.checkFieldsSplForecastRepAccessibility()) {
           return null;
       }


       // map used for setting the user and role as key and
       //  SpecialistForecastlineItem as the value
       Map<String,SpecialistForecastlineItemVO> mapuserRolelineItems = new
           Map<String,SpecialistForecastlineItemVO>();

       splFClineItems = new List<SpecialistForecastlineItemVO>();

       List<HierarchyUtil.RoleNodeWrapper> lstRoles;
       //get the searchuser and all level reporting users
       mapSecondLeverUsersAndChild.clear();
       mapUsers.clear();
       this.getMappedUserInfoFROMRoleHierarchy(sfLI.ForecastOwner__c);
       /* FIXME: Remove before prod! */
       System.debug('mapUsers' + mapUsers);
       /* FIXME: Remove before prod! */
       System.debug(allTotaluserIds + 'allUsers');
       /* FIXME: Remove before prod! */
       System.debug(allTotaluserIds.size()+'allUsers');
       SpecialistForecastlineItemVO splLine;

       Set<Id> ownerIdsforQuery = new Set<Id>();

       for(Id id : mapSecondLeverUsersAndChild.keyset()) {
           ownerIdsforQuery.addAll(mapSecondLeverUsersAndChild.get(id));
       }
       /* FIXME: Remove before prod! */
       System.debug(mapSecondLeverUsersAndChild + '#####@set' + ownerIdsforQuery);
       ownerIdsforQuery.add(sfLI.ForecastOwner__c);

       //set the query parameters
       ForecastReportQueryParam queryParam = new ForecastReportQueryParam();
       queryParam.setFiscalYear(SelectedFY);
       queryParam.setMonthsInQrtr(ForecastHelper.getMonthsInQuarter(SelectedFQ));
       queryParam.setOwnerIds(ownerIdsforQuery);

       //get the data FROM the table as a list
       List<SpecialistForecastlineItem__c> listSplFCastlineItems =
           ForecastHelper.getSpecialistForecastLinesTable(queryParam );

       //get Quota information for all the users
       AggregateResult[] listSplFCastQuotas =
           ForecastHelper.getSpecialistForecastQuota(queryParam );

       // Consolidate data
       SpecialistForecastlineItemVO lineItem;

       Map<Id,SpecialistForecastlineItemVO> ownerlineItemsMap = new
           Map<Id,SpecialistForecastlineItemVO>();

       //********************** consolidation Start *************************
       // Agreegate the records by owner for display as we are having one
       //  record for a quater and we store as months

       ownerlineItemsMap = aggregatelineItemsByOwner(listSplFCastlineItems);
       for (Sobject so : listSplFCastQuotas) {
           AggregateResult ar = (AggregateResult) so;
           Id OwnerId = (Id) ar.get('SpecialistForecastUser__c');
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
       for ( Id lineItemOwnerId : mapSecondLeverUsersAndChild.keySet() ) {
           splLine = new SpecialistForecastlineItemVO();
           boolean mgrCommitOverride = false;
           boolean mgrBestCaseOverride = false;

           // TODO Refactor nested loops?
           for(Id childLinesOwnerId : mapSecondLeverUsersAndChild.get(lineItemOwnerId)) {
               lineItem = ownerlineItemsMap.get(childLinesOwnerId);
               if (lineItem != null) {
                   haslineItemstoDisplay = true;
                   closedAmount = (lineItem.ClosedAmount != null ?
                       lineItem.ClosedAmount : 0);
                   // if manager override amount exists then display mgroverrideamt
                   commitAmount = (lineItem.CommitAmount != null ?
                       lineItem.CommitAmount : 0);

                   commitOvrrideAmount = (lineItem.mgrCommitOverrideAmount !=
                       null ? lineItem.mgrCommitOverrideAmount : commitAmount);
                   bestCaseAmount = (lineItem.bestCaseAmount != null ?
                           lineItem.bestCaseAmount : 0);
                    bestCaseOverrideAmount = (lineItem.mgrBestCaseOverrideAmount
                        != null ? lineItem.mgrBestCaseOverrideAmount : bestCaseAmount);
                    pipelineAmount = (lineItem.PipelineAmount != null ?
                        lineItem.PipelineAmount : 0);
                    quotaAmount = (lineItem.QuotaAmount != null ?
                        lineItem.quotaAmount : 0);

                    //lineItem.mgrCommitOverriden = true;
                    //add to subtotal amount variables
                    if (commitOvrrideAmount > 0 && commitOvrrideAmount != commitAmount
                    && childLinesOwnerId != lineItemOwnerId) {
                        commitAmount = commitOvrrideAmount;
                    }
                    subtotcommitAmount += (lineItem.mycommitOverriden
                        ? lineItem.commitOverrideAmount : commitAmount);
                    subtotclosedAmount += closedAmount;

                    if (bestCaseOverrideAmount != bestCaseAmount &&
                    bestCaseOverrideAmount > 0 && childLinesOwnerId != lineItemOwnerId) {
                        bestCaseAmount = bestCaseOverrideAmount;
                    }

                    subtotbestCaseAmount += (lineItem.MyBestcaseOverriden ?
                        lineItem.bestCaseOverrideAmount : bestCaseAmount);
                    subtotpipelineAmount += pipelineAmount;
                    subtotQuotaAmount    += quotaAmount;

                    // manager overrides the value then diplay only that amount no calculation else aggregation of amount will be displayed for the user
                    if (!mgrCommitOverride) {

                        if (childLinesOwnerId != lineItemOwnerId) {
                            subtotcommitOvrrideAmount += (commitOvrrideAmount
                                <= 0 ? commitAmount : commitOvrrideAmount);
                        } else {
                            mgrCommitOverride = true;
                            subtotcommitOvrrideAmount = (commitOvrrideAmount
                                <= 0 ? commitAmount : commitOvrrideAmount);
                        }
                    }

                    if (!mgrBestCaseOverride) {
                        if (childLinesOwnerId != lineItemOwnerId) {
                            subtotbestCaseOverrideAmount += (bestCaseOverrideAmount
                                <= 0 ? bestCaseAmount : bestCaseOverrideAmount);
                        } else {
                            mgrBestCaseOverride = true;
                            subtotbestCaseOverrideAmount = (bestCaseOverrideAmount
                                <= 0 ? bestCaseAmount : bestCaseOverrideAmount);
                        }
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

                splLine.withoutLink = false;
                splLine.reportingUser = false;
                splLine.forOverride = (sfLI.ForecastOwner__c == UserInfo.getUserId()
                    && SelectedFQ != ForecastHelper.FISCAL_QUARTER_ALL);
                splLine.userName =  mapUsers.get(lineItemOwnerId).userName;
                splLine.roleName = mapUsers.get(lineItemOwnerId).roleName;
                splLine.userId =  mapUsers.get(lineItemOwnerId).userId;
                splLine.roleId = mapUsers.get(lineItemOwnerId).roleId;
                splLine.closedAmount = subtotclosedAmount;
                splLine.commitAmount = subtotcommitAmount;
                splLine.bestCaseAmount = subtotbestCaseAmount;
                splLine.pipelineAmount = subtotpipelineAmount;
                splLine.quotaAmount = subTotQuotaAmount;

                lineItem = ownerlineItemsMap.get(lineItemOwnerId);
                if (lineItem != null) {
                    splLine.commitOverrideAmount = (!lineItem.mgrCommitOverriden
                        ? subtotcommitAmount : subtotcommitOvrrideAmount);
                    splLine.bestCaseOverrideAmount = (!lineItem.mgrBestCaseOverriden
                        ? subtotbestCaseAmount : subtotbestCaseOverrideAmount);
                    splLine.MycommitOverriden = (lineItem.mgrCommitOverriden);
                    // == true?true:false
                    splLine.MyBestcaseOverriden = (lineItem.mgrBestCaseOverriden);
                        // ==true?true:false
                    splLine.strLineItmId = ownerlineItemsMap.get(lineItemOwnerId).strLineItmId;
                    /* FIXME: Remove before prod! */
                    System.debug(lineItemOwnerId + 'is submit' + lineItem.isSubmitted);
                        // FIXME
                    splLine.isSubmitted = (lineItem.isSubmitted || splLine.isSubmitted);
                    // splLine.isSubmitted = (lineItem.isSubmitted == true || splLine.isSubmitted == true?true:false);
                    splLine.SubmittedDate = lineItem.SubmittedDate;
                    splLine.mgrCommitOverrideNotes = lineItem.mgrCommitOverrideNotes;
                    splLine.mgrBestCaseOverrideNotes = lineItem.mgrBestCaseOverrideNotes;
                    splLine.prevDirectRepCommitAmount = lineItem.prevDirectRepCommitAmount;
                    splLine.prevDirectRepBestcaseAmount = lineItem.prevDirectRepBestcaseAmount;

                } else {
                    splLine.commitOverrideAmount = subtotcommitAmount;
                    splLine.bestCaseOverrideAmount = subtotbestCaseAmount;
                    splLine.MycommitOverriden = false;
                    splLine.MyBestcaseOverriden = false;
                    splLine.strLineItmId = '';
                }
                
                    splFClineItems.add(splLine);

                    totcommitAmount           += splLine.commitAmount;
                    totclosedAmount           += splLine.closedAmount;
                    totbestCaseAmount         += splLine.bestCaseAmount;
                    totpipelineAmount         += splLine.pipelineAmount;
                    totQuotaAmount            += splLine.quotaAmount;
                    totbestCaseOverrideAmount += splLine.bestCaseOverrideAmount;
                    totcommitOvrrideAmount    += splLine.commitOverrideAmount;

                } else if (mapUsers.size() > 0 && !haslineItemstoDisplay &&
                mapUsers.containsKey(lineItemOwnerId)) {
                    splLine.createEmptySPlineItemforVFPage(lineItemOwnerId,
                        sfLI.ForecastOwner__c,mapUsers,quotaAmount);
                    splLine.forOverride = (SelectedFQ != ForecastHelper.FISCAL_QUARTER_ALL
                        ? splLine.forOverride : false);
                    splFClineItems.add(splLine);
                }

                haslineItemstoDisplay        = false;
                subtotclosedAmount           = 0;
                subtotcommitAmount           = 0;
                subtotbestCaseAmount         = 0;
                subtotcommitOvrrideAmount    = 0;
                subtotbestCaseOverrideAmount = 0;
                subtotpipelineAmount         = 0;
                subtotQuotaAmount            = 0;

            } // end for
            /* FIXME: Remove before prod! */
            System.debug(splFClineItems + 'splFClineItems'); // FIXME
            splLine = new SpecialistForecastlineItemVO();

            //create search user line items
            if (ownerlineItemsMap.containsKey(sfLI.ForecastOwner__c) &&
            ownerlineItemsMap.get(sfLI.ForecastOwner__c) != null) {

                lineItem = ownerlineItemsMap.get(sfLI.ForecastOwner__c);
                closedAmount = (lineItem.closedAmount != null ?
                    lineItem.closedAmount : 0);
                commitAmount = (lineItem.commitAmount != null ?
                    lineItem.commitAmount : 0);
                bestCaseAmount = (lineItem.bestCaseAmount != null ?
                    lineItem.bestCaseAmount : 0);
                commitOvrrideAmount = (lineItem.CommitOverrideAmount != null &&
                    lineItem.CommitOverrideAmount > 0 ?
                    lineItem.CommitOverrideAmount : commitAmount);
                bestCaseOverrideAmount = (lineItem.bestCaseOverrideAmount != null
                    && lineItem.bestcaseOverrideAmount > 0 ?
                    lineItem.bestCaseOverrideAmount : bestCaseAmount);
                pipelineAmount = (lineItem.PipelineAmount != null ?
                    lineItem.PipelineAmount : 0);
                quotaAmount = (lineItem.quotaAmount != null ? 
                    lineItem.quotaAmount : 0);
                // spline = getlineItemVO(true,false,(sfLI.ForecastOwner__c == UserInfo.getUserId()?true:false),

                splLine.withoutLink   = true;
                splLine.reportingUser = false;
                splLine.forOverride = (sfLI.ForecastOwner__c == UserInfo.getUserId()
                    && SelectedFQ != ForecastHelper.FISCAL_QUARTER_ALL);
                splLine.userName = mapUsers.get(sfLI.ForecastOwner__c).userName;
                splLine.roleName = mapUsers.get(sfLI.ForecastOwner__c).roleName;
                splLine.userId = mapUsers.get(sfLI.ForecastOwner__c).userId;
                splLine.roleId = mapUsers.get(sfLI.ForecastOwner__c).roleId;
                splLine.closedAmount = closedAmount;
                splLine.commitAmount = commitAmount;
                splLine.bestCaseAmount = bestCaseAmount;
                splLine.commitOverrideAmount = commitOvrrideAmount;
                splLine.bestCaseOverrideAmount = bestCaseOverrideAmount;
                splLine.pipelineAmount = pipelineAmount;
                splLine.quotaAmount = quotaAmount;
                splLine.MycommitOverriden = lineItem.MycommitOverriden;
                splLine.MyBestcaseOverriden = lineItem.MyBestcaseOverriden;
                splLine.strLineItmId = lineItem.strLineItmId;
                splLine.searchUser = true;

                //add the total amounts to the list
                 splFClineItems.add(splLine);

                 totcommitAmount           += commitAmount;
                 totclosedAmount           += closedAmount;
                 totbestCaseAmount         += bestCaseAmount;
                 totpipelineAmount         += pipelineAmount;
                 totQuotaAmount            += quotaAmount;
                 totbestCaseOverrideAmount += bestCaseOverrideAmount;
                 totcommitOvrrideAmount    += commitOvrrideAmount;

       } else {
                splLine.createEmptySPlineItemforVFPage(sfLI.ForecastOwner__c,
                    sfLI.ForecastOwner__c, mapUsers, quotaAmount);
                splLine.forOverride = (SelectedFQ !=
                    ForecastHelper.FISCAL_QUARTER_ALL ? splLine.forOverride : false);
                splFClineItems.add(splLine);
       }

        // create total line items
        splLine = new SpecialistForecastlineItemVO();
        splLine.withoutLink = true;
        splLine.reportingUser = true;
        splLine.userName = ForecastHelper.GRAND_TOTAL;
        splLine.closedAmount = totclosedAmount;
        splLine.commitAmount = totcommitAmount;
        splLine.bestCaseAmount = totbestCaseAmount;
        splLine.commitOverrideAmount = totcommitOvrrideAmount ;
        splLine.bestCaseOverrideAmount = totbestCaseOverrideAmount;
        splLine.pipelineAmount = totpipelineAmount;
        splLine.quotaAmount = totQuotaAmount;

        //add the total amounts Line to the list
        splFClineItems.add(splLine);

        return splFClineItems;
    }


    /**
     * Get all the users added into the  allUserIds and put the RoleInfo with
     * UserId Key for each displayable user
     *
     */
    private void getMappedUserInfoFromRoleHierarchy(Id userId) {

        User usr = [SELECT Name, UserRoleId, UserRole.Name FROM User WHERE Id = :userId];

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
                /* FIXME: Remove before prod! */
                System.debug(rolewrapper.myroleName + '$roleid' +
                     rolewrapper.myRoleId);
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
                    roleTraverse (rolewrapper );
                    /* FIXME: Remove before prod! */
                    System.debug('#####%set1' + alluserIds); // FIXME
                    alluserIds.add(rolewrapper.myUsers.get(0).Id);
                    allTotaluserIds.add(rolewrapper.myUsers.get(0).Id);
                    mapSecondLeverUsersAndChild.put(rolewrapper.myUsers.get(0).Id,alluserIds);
                    /* FIXME: Remove before prod! */
                    System.debug('#####@%setmapSecondLeverUsersAndChild' +
                        mapSecondLeverUsersAndChild); // FIXME
                }
                // if manager and many users assigned to the role then get only mapped user
                if (roleWrapper.myUsers != null && roleWrapper.myUsers.size() > 1
                && !roleWrapper.isLeafNode) {
                    alluserIds = new Set<Id>();

                    ruinfo = new RoleUserInfoVO();
                    ruinfo.roleId = rolewrapper.myRoleId;
                    ruinfo.roleName = rolewrapper.myroleName;
                    ruinfo.userId = rolewrapper.myMappedUser.Id;
                    ruinfo.userName = rolewrapper.myMappedUser.Name;
                    mapUsers.put(rolewrapper.myMappedUser.Id,ruinfo);
                    /* Put the key with the child*/
                    roleTraverse(rolewrapper);
                    /* FIXME: Remove before prod! */
                    System.debug('#####%set2' + alluserIds); // FIXME
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
                        /* FIXME: Remove before prod! */
System.debug('#####%set3' + alluserIds); // FIXME
                        alluserIds.add(myusr.Id);
                        allTotaluserIds.add(myusr.Id);
                        mapSecondLeverUsersAndChild.put(myusr.Id,alluserIds);
                        /* FIXME: Remove before prod! */
System.debug('#####@%setmapSecondLeverUsersAndChild' +
                            mapSecondLeverUsersAndChild); // FIXME
                    }//end for
                }//end if
            }//end for
        }//end if

        // mapped/search user
        ruinfo = new RoleUserInfoVO();
        ruinfo.roleId = usr.UserRoleId;
        ruinfo.roleName = usr.userrole.Name;
        ruinfo.userId = userId;
        ruinfo.userName = usr.Name;
        mapUsers.put(userId,ruinfo);
    }



     /**
      *
      * Recursively traverse a given roleId
      *
      * @param rolewrapper user/role indicates the starting point of traversal
      */
     private void roleTraverse (HierarchyUtil.RoleNodeWrapper rolewrapper) {

         if (rolewrapper.myChildNodes != null) {
             for (HierarchyUtil.RoleNodeWrapper rw : rolewrapper.myChildNodes){
                 /* FIXME: Remove before prod! */
                 System.debug(rw + '$$$$$$$$1' + rw.myroleName); //FIXME
                 //if(rw.myMappedUser != null){
                 if (rw.myUsers != null && rw.myUsers.size() > 1 && rw.isLeafNode) {
                     for(user usr : rw.myUsers){
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
     * Agreegate the records by owner for display as we are having one record
     * for a quater and  we store as months
     * @Parameter List of SpecialistForecastlineItem__c
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

            closedAmount = (sfl.OriginalClosedAmount__c != null ?
                sfl.OriginalClosedAmount__c : 0);
            commitAmount = (sfl.OriginalCommitAmount__c != null ?
                sfl.OriginalCommitAmount__c : 0);
            bestCaseAmount = (sfl.OriginalbestCaseAmount__c != null ?
                sfl.OriginalbestCaseAmount__c : 0);
            pipelineAmount = (sfl.OriginalPipelineAmount__c != null ?
                sfl.OriginalPipelineAmount__c : 0);
            /* FIXME: Remove before prod! */
            System.debug('PipelineAmount##############' +
                sfl.OriginalPipelineAmount__c); // FIXME
            commitOverrideAmount =
                (sfl.SpecialistForecastOverride__r.OwnOverridenCommitAmount__c
                != null ? sfl.SpecialistForecastOverride__r.OwnOverridenCommitAmount__c : 0);
            bestCaseOverrideAmount =
                (sfl.SpecialistForecastOverride__r.OwnOverridenBestcaseAmount__c
                != null ? sfl.SpecialistForecastOverride__r.OwnOverridenBestcaseAmount__c : 0);
            commitMgrOvrrideAmount =
                (sfl.SpecialistForecastOverride__r.ManagerOverridenCommitAmount__c
                != null ? sfl.SpecialistForecastOverride__r.ManagerOverridenCommitAmount__c : 0);
            bestCaseMgrOverrideAmount =
                (sfl.SpecialistForecastOverride__r.ManagerOverridenBestcaseAmount__c
                != null ? sfl.SpecialistForecastOverride__r.ManagerOverridenBestcaseAmount__c : 0);


            splv = ownerlineItemsMap.get(currentOwnerId);
            if (splv == null) {
                splv = new SpecialistForecastlineItemVO();
            }

            if (previousOwnerId != currentOwnerId) {
                 previousOwnerId = currentOwnerId;
                 splv.closedAmount = closedAmount ;
                 splv.commitAmount = commitAmount ;
                 splv.bestCaseAmount = bestCaseAmount;
                 splv.pipelineAmount = pipelineAmount;
                 splv.commitOverrideAmount = commitOverrideAmount;
                 splv.bestCaseOverrideAmount = bestCaseOverrideAmount;
                 splv.mgrCommitOverrideAmount = commitMgrOvrrideAmount;
                 splv.mgrBestCaseOverrideAmount = bestCaseMgrOverrideAmount;
                 splv.submittedDate = (sfl.submitteddate__c != null ?
                     sfl.submitteddate__c.format() :
                     (splv.submittedDate != null ? splv.submittedDate : ''));
                 splv.isSubmitted = (sfl.Submitteddate__c != null || splv.isSubmitted);

                 splv.MycommitOverriden =
                     sfl.SpecialistForecastOverride__r.OwnOverridenCommitAmount__c != null;
                 splv.MyBestcaseOverriden =
                     sfl.SpecialistForecastOverride__r.OwnOverridenBestcaseAmount__c != null;
                 splv.mgrCommitOverriden =
                     sfl.SpecialistForecastOverride__r.ManagerOverridenCommitAmount__c != null;
                 splv.mgrBestCaseOverriden =
                     sfl.SpecialistForecastOverride__r.ManagerOverridenBestcaseAmount__c != null;
                 splv.mgrCommitOverrideNotes =
                     sfl.SpecialistForecastOverride__r.ManagerOverrideCommitNotes__c;
                 splv.mgrBestCaseOverrideNotes =
                     sfl.SpecialistForecastOverride__r.ManagerOverrideBestcaseNotes__c;
                 splv.prevDirectRepCommitAmount =
                     sfl.SpecialistForecastOverride__r.PreviousDirectRepCommitAmount__c;
                 splv.prevDirectRepBestcaseAmount =
                     sfl.SpecialistForecastOverride__r.PreviousDirectRepBestcaseAmount__c;

            } else {
                splv.closedAmount += closedAmount;
                splv.commitAmount += commitAmount;
                splv.bestCaseAmount += bestCaseAmount;
                splv.pipelineAmount += pipelineAmount ;
                splv.commitOverrideAmount += commitOverrideAmount;
                splv.bestCaseOverrideAmount += bestCaseOverrideAmount;
                splv.mgrCommitOverrideAmount += commitMgrOvrrideAmount;
                splv.mgrBestCaseOverrideAmount += bestCaseMgrOverrideAmount;
                splv.MycommitOverriden =
                    (sfl.SpecialistForecastOverride__r.OwnOverridenCommitAmount__c
                    != null || splv.MycommitOverriden);
                splv.MyBestcaseOverriden =
                    (sfl.SpecialistForecastOverride__r.OwnOverridenBestcaseAmount__c
                    != null || splv.MyBestcaseOverriden);
                splv.mgrCommitOverriden =
                    (sfl.SpecialistForecastOverride__r.ManagerOverridenCommitAmount__c
                    != null ||splv.mgrCommitOverriden);
                splv.mgrBestCaseOverriden =
                    (sfl.SpecialistForecastOverride__r.ManagerOverridenBestcaseAmount__c
                    != null ||splv.mgrBestCaseOverriden);
                splv.submittedDate = (sfl.submitteddate__c != null ?
                    sfl.submitteddate__c.format() :
                    (splv.submittedDate != null ? splv.submittedDate : ''));
                splv.isSubmitted = (sfl.Submitteddate__c != null ||
                    splv.isSubmitted);
                splv.mgrCommitOverrideNotes = (splv.mgrCommitOverriden ?
                    sfl.SpecialistForecastOverride__r.ManagerOverrideCommitNotes__c : '');
                splv.mgrBestCaseOverrideNotes = (splv.mgrBestCaseOverriden ?
                    sfl.SpecialistForecastOverride__r.ManagerOverrideBestcaseNotes__c : '');
                splv.prevDirectRepCommitAmount = (splv.mgrCommitOverriden ?
                    sfl.SpecialistForecastOverride__r.PreviousDirectRepCommitAmount__c : 0);
                splv.prevDirectRepBestcaseAmount = (splv.mgrBestCaseOverriden ?
                    sfl.SpecialistForecastOverride__r.PreviousDirectRepBestcaseAmount__c : 0);
            } //end if
            commitMgrOvrrideAmount = 0;
            commitOverrideAmount = 0;
            ownerlineItemsMap.put(currentOwnerId,splv);
            /* FIXME: Remove before prod! */
            System.debug(previousOwnerId + 'Current splv##############' + splv);
            // FIXME
        } //end for

        return ownerlineItemsMap;
    }

    /**
     *
     */
    private void setRoleUserValue(
        HierarchyUtil.RoleNodeWrapper rolewrapper, User myusr) {

        RoleUserInfoVO ruinfo = new RoleUserInfoVO();
        ruinfo.roleId = rolewrapper.myRoleId;
        ruinfo.roleName = rolewrapper.myroleName;
        ruinfo.userId = myusr.Id;
        ruinfo.userName = myusr.Name;
        mapUsers.put(myusr.Id,ruinfo);
        /* Put the key with the child*/
        roleTraverse (rolewrapper );
        alluserIds.add(myusr.Id);
        allTotaluserIds.add(myusr.Id);
        mapSecondLeverUsersAndChild.put(myusr.Id,alluserIds);

        /* FIXME: Remove before prod! */
        System.debug('#####@%set' + mapSecondLeverUsersAndChild); // FIXME

     }

      /***************Specialist Forecast Override**********************/
      /**
       *
       */
      public pagereference saveOverride() {

        SpecialistForecastlineItem__c spc = new SpecialistForecastlineItem__c();
        SpecialistForecastOverride__c spovr = new SpecialistForecastOverride__c();
        String external_Key;
        if (strField =='commit') {

            if (isSelfOverride()) {
                spovr.OwnOverridenCommitAmount__c = overrideAmount;
                spovr.OwnOverrideNotes__c = overridecomment;
            } else {
                spovr.ManagerOverridenCommitAmount__c = overrideAmount;
                spovr.ManagerOverrideCommitNotes__c = overridecomment;

            }
            spovr.PreviousDirectRepCommitAmount__c = originaloldAmount;
        } else if (strField =='bestcase') {

            if(isSelfOverride()){
                spovr.OwnOverridenBestCaseAmount__c = overrideAmount;
                spovr.OwnOverrideNotes__c = overrideComment;
            } else {
                spovr.ManagerOverridenBestCaseAmount__c = overrideAmount;
                spovr.ManagerOverrideBestcaseNotes__c = overrideComment;
            }
           spovr.PreviousDirectRepBestcaseAmount__c = originaloldAmount;
       }

       spovr.PeriodType__c = SelectedFQ;
       external_Key = lineItemOwnerId + SelectedFY + SelectedFQ;
       spovr.ExtIdentifier__c = external_Key;
       spovr.OwnerId = lineItemOwnerId;

       try {
           Upsert spovr ExtIdentifier__c;
       } catch(System.DMLException ex) {
           ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
               ' Failed to update the Override value'));
           return null;
        }

        //upsert Line item
        List<String> lstMonths = ForecastHelper.getMonthsInQuarter(SelectedFQ);

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
                ' Failed to update the Override Amount'));
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
     private void initializeOverrideVariables(){
         overrideAmount = null;
         originalAmount = 0;
         originaloldAmount = 0;
         overrideComment='';
         strRecordId='';
         strField='';
         lineItemOwnerId='';

     }
   /***************Specialist Forecast Override End**********************/


    /***************Specialist Forecast DrillDown**********************/
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
        if (SelectedFQ != null && SelectedFY != null && lineItemOwnerId != null) {
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

        if (strField == ForecastHelper.SUBTYPE_CLOSED) {
            lstStatus.add('Closed Won');
        }

        List<String> lstMonths = ForecastHelper.getMonthsInQuarter(SelectedFQ);
        //get months by quarter

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

 }
