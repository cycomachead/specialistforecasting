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
 *  @author auzzaman,smohapatra
 */
public with sharing class SpecialistForecastController {

    /** Static helper strings and error messages. */
    private static final String CLOSED      = ForecastHelper.CLOSED;
    private static final String CLOSED_WON  = ForecastHelper.CLOSED_WON;
    private static final String COMMIT_MSG  = ForecastHelper.COMMIT_MSG;
    private static final String QTR_ALL     = ForecastHelper.QTR_ALL;
    private static final String FOR_ID      =
            SpecialistForecastException.FOR_ID;
    private static final String USER_ID_NULL =
            SpecialistForecastException.USER_ID_NULL + FOR_ID;
    private static final String ROLE_ID_NULL =
            SpecialistForecastException.ROLE_ID_NULL + FOR_ID;
    private static final String USER_MISSING_ID =
            SpecialistForecastException.USER_MISSING_ID;

    // The max size of the recent users list, mostly to keep things manageable.
    private static final Integer MAX_BACK_SIZE = 16;
    // Access Check if user can view forecast reports
    private Boolean canView = true;
    //
    private Map<Id,Set<Id>> mapSecondLevelUsersAndChild = new Map<Id,Set<Id>>();
    //
    private Map<Id,UserWrapper> mapUsers = new Map<Id,UserWrapper>();

    // UI lists for moving around and viewing forecasts data
    private SelectOption[] recentsList  = new SelectOption[0];
    private SelectOption[] fiscalYears;
    private SelectOption[] fiscalQuarters;

    private Set<Id> allUserIDs      = new Set<Id>();
    private Set<Id> allTotaluserIDs = new Set<Id>();

    // Holds the UID of the user selected from the recents picklist
    public String selectedBackUser      { get;set; }
    // True while the forecast override popup is active
    public Boolean displayPopup         { get;set; }
    // True while the forecast details popup is active
    public Boolean displayDrilDownPopup { get;set; }
    // values used while overriding forecast data (from popup)
    public Decimal originalAmount       { get;set; }
    public Decimal originalOldAmount    { get;set; }
    public Decimal overrideAmount       { get;set; }

    //
    public SpecialistForecast__c[] specialistforecastList { get;set; }
    //
    public SpecialistForecastlineItem__c sfLI             { get;set; }
    // list that drives the main forecasts table / view on VF page
    public SFcastLineItem[] lineItemList = new SFcastLineItem[0];
    // The "Total" (last) line of the table on the VF page.
    public SFcastLineItem   totalLine = new SFcastLineItem();

    public String overrideComment       { get;set; }
    public String strRecordId           { get;set; }
    public String strField              { get;set; }
    public String lineItemOwnerId       { get;set; }
    public String fiscalYear            { get;set; }
    public String fiscalQuarter         { get;set; }
    public String selectedFY            { get;set; }
    public String selectedFQ            { get;set; }
    public String preparedByUser        { get;set; }
    public String selectedUserId        { get;set; }
    public String selectedRoleId        { get;set; }

    // Simple wrapper for role and user info, mostly for use in maps.
    public with sharing class UserWrapper {
        public Id     roleId   { get;set; }
        public Id     userId   { get;set; }
        public String roleName { get;set; }
        public String userName { get;set; }
    }

    /**
     *  Constructs a new Specialist Forecast Controller Class
     *  If no FY or FQ is provided, then the current system time is used.
     */
    public SpecialistForecastController() {
        try {
            sfLI = new SpecialistForecastlineItem__c();
            sfLI.ForecastOwner__c = UserInfo.getUserId();
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

    /** Returns true IFF the user has somewhere to go back to. */
    public Boolean getCanGoBack() {
        if (recentsList == null) { return false; }
        return !recentsList.isEmpty();
    }

    /**  Returns true IFF the user is allowed to override/edit forecasts */
    public Boolean getCanSubmit() {
        return (sfLI.ForecastOwner__c == UserInfo.getUserId() &&
            selectedFQ != QTR_ALL);
    }

    /** Called from VF page to check if user has access to the button */
    public Boolean getViewAccessToSyncButton() {
        try {
            return ForecastHelper.checkForecastDataSyncAccessForCurrentUser();
        } catch (Exception ex) {
            return false;
        }
    }

    /** Returns the list of fiscal quaters as a SelectOption[] for VF page */
    public SelectOption[] getFiscalQuarters() {
        if (fiscalQuarters == null) {
            fiscalQuarters = new SelectOption[0];
            fiscalQuarters.add(new SelectOption(QTR_ALL, QTR_ALL));

            String[] fQuarters = ForecastHelper.getSupportedFiscalQuarters();
            for(String fq : fQuarters) {
                fiscalQuarters.add(new SelectOption(fq, fq));
            }
        }
        return fiscalQuarters;
    }

    /**  Returns the list of selectable fiscal years for VF page  */
    public SelectOption[] getFiscalYears() {
        if (fiscalYears == null) { setFiscalYears(); }
        return fiscalYears;
    }

    /**  Returns the list of selectable forecasts to view for VF page  */
    public SelectOption[] getRecentsList() {
        return recentsList;
    }

    /** Reset fiscal year selections to the base supported years */
    public void setFiscalYears() {
        fiscalYears = new SelectOption[0];
        String[] fYears = ForecastHelper.getSupportedFiscalYears();

        for(String fYear : fYears) {
            fiscalYears.add(new SelectOption(fYear, fYear));
        }
    }

    /**  Set selectable fiscal years to FYEARS (SelectOption list) */
    public void setFiscalYears(SelectOption[] fYears) {
        fiscalYears = fYears;
    }

    /** Add recent user, w/o dupes and list size < MAX_BACK_SIZE */
    private void setRecentsList() {
        if (recentsList == null) { recentsList = new SelectOption[0]; }

        String name = getUserNameFromMap(sfLI.ForecastOwner__c);
        if (!ForecastHelper.listContains(recentsList, sfLI.ForecastOwner__c)) {
            recentsList.add(new SelectOption(sfLI.ForecastOwner__c, name));
        }
        if (recentsList.size() > MAX_BACK_SIZE) {
            recentsList.remove(1); // Force item 0 to always be inital user
        }
    }

    /**  View report button */
    public PageReference viewSpecialistReport() {
        canView = true;
        allTotaluserIds.add(UserInfo.getUserId());
        try {
            getMappedUserInfoFromRoleHierarchy(UserInfo.getUserId());
            if (!allTotaluserIds.contains(sfLI.ForecastOwner__c)) {
                canView = false;
                ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                    'You do not have permission to view reports for this user.'));
                return null;
            }
            canView = true;
            if (sfLI.ForecastOwner__c != null) {
                PreparedByUser = getUserNameFromMap(sfLI.ForecastOwner__c); //
            }
        } catch (Exception ex) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                'Unexpected Report Error: ' + ex.getMessage()));
        }
        return null;
    }

    /** Called from userlink for drilldown */
    public PageReference reportDrildownByUserRole() {
        if (selectedUserId != null && selectedUserId.length() > 10) {
            PreparedbyUser = getUserNameFromMap(selectedUserId);
            sfLI.ForecastOwner__c = selectedUserId;
        }
        return null;
    }

    /** Called from submit button */
    public PageReference submitReport() {
        if (selectedFQ != QTR_ALL) {
            String strMonth = ForecastHelper.getMonthsInQuarter(selectedFQ).get(0);
            SpecialistForecastlineItem__c sfLI = new SpecialistForecastlineItem__c();
            sfLI.ForecastOwner__C =  sfLI.ForecastOwner__c;
            sfLI.FiscalYear__c = SelectedFY;
            sfLI.Month__c = strMonth;
            sfLI.submittedDate__c = System.now();
            sfLI.ExtIdentifier__c = sfLI.ForecastOwner__c + selectedFY + strMonth;
            UPSERT sfLI ExtIdentifier__c;
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.CONFIRM,
                'Report Submitted successfully'));
       } else {
              ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,
                  'You can\'t submit for quarter "All".' +
                  'Please select a different quarter to submit.'));
       }
       return null;
    }

    /** Returns the link of where the uder will go back to. */
    public PageReference backLink() {
        if (!recentsList.isEmpty()) {
            PreparedByUser = getUserNameFromMap(selectedBackUser); //
            sfLI.ForecastOwner__c = selectedBackUser;
        }
        return null;
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
     */
    public SFcastLineItem[] getSFcastLineItemsData() {
        // Background checks to make sure that we have the necessary perms.
        if (UserInfo.getUserRoleId() == null) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                USER_MISSING_ID));
            return null; // kill the method to prevent later exceptions
        }
        if (!canView || !ForecastHelper.canAccessForecasts()) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
            'The current user doesn\'t have access to view Specialist'
            + 'Forecasts')); // TODO - make label
            return null;
        }

        setRecentsList(); // Keep track of forecasts viewed for dropdown menu

        Decimal closedAmount                 = 0, // hold the data for each
                commitAmount                 = 0, // line in the table.
                bestCaseAmount               = 0,
                commitOvrrideAmount          = 0,
                bestCaseOverrideAmount       = 0,
                commitMgrOvrrideAmount       = 0,
                bestCaseMgrOverrideAmount    = 0,
                pipelineAmount               = 0,
                quotaAmount                  = 0,
                subtotclosedAmount           = 0, // FIXME
                subtotcommitAmount           = 0,
                subtotbestCaseAmount         = 0,
                subtotcommitOvrrideAmount    = 0,
                subtotbestCaseOverrideAmount = 0,
                subtotpipelineAmount         = 0,
                subtotQuotaAmount            = 0;

        // String represents "User (Role)"
        // Map<String,SFcastLineItem> mapUserRolelineItems = new
//         Map<String,SFcastLineItem>();
        // TODO`
        Map<Id,SFcastLineItem> ownerlineItemsMap = new Map<Id,SFcastLineItem>();

        // Current copy of individual table row before adding to the main list
        SFcastLineItem sfLine = new SFcastLineItem();
        // Line item holding the values of the total row, set basic properties.
                    totalLine = new SFcastLineItem();
                    totalLine.makeTotalLine();
        // This is returned and the main set of data on the VF page.
        // Clear list before creating a new table
        lineItemList.clear();

        // The user working with the VF page, used to determine view perms
        Id currUser = UserInfo.getUserId();

        HierarchyUtil.RoleNodeWrapper[] lstRoles;
        // Consolidate data TODO -
        SFcastLineItem lineItem;

        // get the searchuser and all level reporting users
        // TODO - searchuser??
        mapSecondLevelUsersAndChild.clear();
        mapUsers.clear();
        // Add users back to mapSecondLevelUsersAndChild
        getMappedUserInfoFromRoleHierarchy(sfLI.ForecastOwner__c);

        // Set Query paramaters and query for line items and quotas
        // Months based on selected fiscal quarter
        String[] queryMonths = ForecastHelper.getMonthsInQuarter(selectedFQ);
        // Create a set of all users to query, starting with the forecast owner
        Set<Id> queryOwnerIds = new Set<Id>();
        queryOwnerIds.add(sfLI.ForecastOwner__c);
        for (Set<Id> idSet : mapSecondLevelUsersAndChild.values()) {
            queryOwnerIds.addAll(idSet);
        }
        // get the data from the table as a list
        SpecialistForecastlineItem__c[] listSplFCastlineItems =
        ForecastHelper.getAllLineItems(selectedFY, queryMonths, queryOwnerIds);
        // get Quota information for all the users
        AggregateResult[] listSplFCastQuotas = ForecastHelper.getAllQuotas(
            selectedFY, queryMonths, queryOwnerIds);

        /********************** consolidation Start *************************/
        // Agreegate the records by owner for display as we are having one
        // record for a quater and we store as months
        // FIXME WAT
        // MORE NEEDED
        ownerlineItemsMap = aggregateLineItemsByOwner(listSplFCastlineItems);
        for (SObject so : listSplFCastQuotas) {
            AggregateResult ar = (AggregateResult) so;
            Id         ownerId = (Id) ar.get('SpecialistForecastUser__c');
            Decimal tempAmount = (Decimal) ar.get('amnt');
                      lineItem = ownerlineItemsMap.get(ownerId);

            if (lineItem != null) {
                lineItem.quotaAmount = tempAmount;
                ownerlineItemsMap.put(ownerId, lineItem);
            }
        }
        /********************** consolidation End *****************************/
        // TODO
        Boolean haslineItemstoDisplay = false;
        // consolidate child records with 2nd level roles/users
        for (Id lineItemOwnerId : mapSecondLevelUsersAndChild.keySet()) {
            sfLIne = new SFcastLineItem(); //TODO
            Boolean mgrCommitOverride   = false; // No manager overrides
            Boolean mgrBestCaseOverride = false;

            for (Id childLinesOwnerId : mapSecondLevelUsersAndChild.get(lineItemOwnerId)) {
                System.debug('\n\n\n MAP SECOND LEVEL: \n\n\t' + mapSecondLevelUsersAndChild);
                // lineItem is TODO
                lineItem = ownerlineItemsMap.get(childLinesOwnerId);
                if (lineItem != null) {
                    haslineItemstoDisplay = true;
                    closedAmount = defaultIfNull(lineItem.closedAmount);
                    // if manager override amount exists then display mgroverrideamt
                    commitAmount = defaultIfNull(lineItem.commitAmount);
                    commitOvrrideAmount = (lineItem.mgrCommitOverrideAmount !=
                        null ? lineItem.mgrCommitOverrideAmount : commitAmount);
                    bestCaseAmount = defaultIfNull(lineItem.bestCaseAmount);
                    bestCaseOverrideAmount = (lineItem.mgrBestCaseOverrideAmount
                        != null ? lineItem.mgrBestCaseOverrideAmount : bestCaseAmount);
                    pipelineAmount = defaultIfNull(lineItem.pipelineAmount);
                    quotaAmount = defaultIfNull(lineItem.quotaAmount);

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

                    subtotbestCaseAmount += (lineItem.myBestCaseOverridden ?
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
                closedAmount           = 0; // reset line values.
                commitAmount           = 0;
                bestCaseAmount         = 0;
                pipelineAmount         = 0;
                quotaAmount            = 0;
                bestCaseOverrideAmount = 0;
                commitOvrrideAmount    = 0;
            }

            //if not search user then assign values
            // haslineItemstoDisplay TODO
            Boolean bool = mapUsers.containsKey(lineItemOwnerId);
            // FIXME  - name, logic error in else if?
            Boolean b2 = lineItemOwnerId != sfLI.ForecastOwner__c;
            if (haslineItemstoDisplay && bool && b2) {

                sfLIne.reportingUser  = false;
                sfLIne.forOverride    = (selectedFQ != QTR_ALL &&
                                sfLI.ForecastOwner__c == currUser);
                sfLIne.userName       = mapUsers.get(lineItemOwnerId).userName;
                sfLIne.roleName       = mapUsers.get(lineItemOwnerId).roleName;
                sfLIne.userId         = mapUsers.get(lineItemOwnerId).userId;
                sfLIne.roleId         = mapUsers.get(lineItemOwnerId).roleId;
                sfLIne.closedAmount   = subtotclosedAmount;
                sfLIne.commitAmount   = subtotcommitAmount;
                sfLIne.bestCaseAmount = subtotbestCaseAmount;
                sfLIne.pipelineAmount = subtotpipelineAmount;
                sfLIne.quotaAmount    = subTotQuotaAmount;
                // lineItem is TODO
                lineItem = ownerlineItemsMap.get(lineItemOwnerId);
                // TODO
                if (lineItem != null) {
                    sfLIne.commitOverrideAmount = (!lineItem.mgrCommitOverridden
                        ? subtotcommitAmount : subtotcommitOvrrideAmount);
                    sfLIne.bestCaseOverrideAmount = (!lineItem.mgrBestCaseOverridden
                        ? subtotbestCaseAmount : subtotbestCaseOverrideAmount);
                    sfLIne.myCommitOverridden = (lineItem.mgrCommitOverridden);
                    sfLIne.myBestCaseOverridden = (lineItem.mgrBestCaseOverridden);
                    sfLIne.strLineItmId = ownerlineItemsMap.get(lineItemOwnerId).strLineItmId;
                    sfLIne.isSubmitted = (defaultIfNull(lineItem.isSubmitted) ||
                         defaultIfNull(sfLIne.isSubmitted));
                    sfLIne.submittedDate = lineItem.submittedDate;
                    sfLIne.mgrCommitOverrideNotes = lineItem.mgrCommitOverrideNotes;
                    sfLIne.mgrBestCaseOverrideNotes = lineItem.mgrBestCaseOverrideNotes;
                    sfLIne.prevDirectRepCommitAmount = lineItem.prevDirectRepCommitAmount;
                    sfLIne.prevDirectRepBestcaseAmount = lineItem.prevDirectRepBestcaseAmount;
                } else { // LineItem == null
                    sfLIne.commitOverrideAmount   = subtotcommitAmount;
                    sfLIne.bestCaseOverrideAmount = subtotbestCaseAmount;
                    sfLIne.myCommitOverridden     = false;
                    sfLIne.myBestCaseOverridden   = false;
                    sfLIne.strLineItmId           = '';
                }

                System.debug('\n\n\n BEFORE LIST ADD 497');
                System.debug(lineItemList);
                lineItemList.add(sfLIne);
                // FIXME
                debugFN();
                System.debug(sfLine);
                // increment total line on each interation
                totalLine.increment(commitAmount,
                                    closedAmount,
                                    bestCaseAmount,
                                    pipelineAmount,
                                    quotaAmount,
                                    bestCaseOverrideAmount,
                                    commitOvrrideAmount);

            } else if (!haslineItemstoDisplay && bool) {
                sfLIne.createEmptyLineItem(sfLI.ForecastOwner__c, selectedFQ,
                    mapUsers.get(lineItemOwnerId), quotaAmount);
                System.debug('\n\n BEFORE LIST ADD: line 515 \n');
                System.debug(lineItemList); //FIXME
                lineItemList.add(sfLIne);
                debugFN();
            } //end if
            // Reset Values ... TODO
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
        lineItem = ownerlineItemsMap.get(sfLI.ForecastOwner__c);
        if (lineItem != null) {
            sfLine = new SFcastLineItem();
            closedAmount           = defaultIfNull(lineItem.closedAmount);
            commitAmount           = defaultIfNull(lineItem.commitAmount);
            bestCaseAmount         = defaultIfNull(lineItem.bestCaseAmount);
            pipelineAmount         = defaultIfNull(lineItem.pipelineAmount);
            quotaAmount            = defaultIfNull(lineItem.quotaAmount);
            commitOvrrideAmount    = ((lineItem.CommitOverrideAmount != null &&
                lineItem.CommitOverrideAmount > 0) ?
                lineItem.CommitOverrideAmount : commitAmount);
            bestCaseOverrideAmount = ((lineItem.bestCaseOverrideAmount != null
                && lineItem.bestcaseOverrideAmount > 0) ?
                    lineItem.bestCaseOverrideAmount : bestCaseAmount);

            sfLIne.reportingUser = false;
            sfLIne.forOverride   = (sfLI.ForecastOwner__c == currUser
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
            sfLIne.myCommitOverridden     = lineItem.myCommitOverridden;
            sfLIne.myBestCaseOverridden   = lineItem.myBestCaseOverridden;
            sfLIne.strLineItmId           = lineItem.strLineItmId;
            sfLIne.searchUser             = true;

            System.debug('\n\n BEFORE LIST ADD line 570 \n');
            System.debug(lineItemList);
            //add the total amounts to the list -- TODO, total?
            lineItemList.add(sfLIne);
            // FIXME
            debugFN();
            System.debug(sfLine);

            // increment total line on each interation
            totalLine.increment(commitAmount,
                                closedAmount,
                                bestCaseAmount,
                                pipelineAmount,
                                quotaAmount,
                                bestCaseOverrideAmount,
                                commitOvrrideAmount);
        } else { // else WHAT? FIXME
            sfLIne = new SFcastLineItem();
            sfLIne.createEmptyLineItem(sfLI.ForecastOwner__c, selectedFQ,
                mapUsers.get(sfLI.ForecastOwner__c), quotaAmount);
            lineItemList.add(sfLIne);
            // FIXME
            debugFN();
            System.debug(sfLine);
        }

        System.debug('\n\n BEFORE DEDUPE \n\t' +
            'SIZE: \t' + lineItemList.size() + '\n\tCONTENTS:\t' + lineItemList);
        //deDupe();
        System.debug('\n\n END TABLE GENERATE \n\t' +
            'SIZE: \t' + lineItemList.size() + '\n\tCONTENTS:\t' + lineItemList);
        return lineItemList;
    }

    /** Return the "Total" line for the Forecasts table on the VF page. */
    public SFcastLineItem getTotalLine() {
        if (totalLine == null) {
            totalLine = new SFcastLineItem();
            totalLine.makeTotalLine();
         }
        return totalLine;
    }

    /**
     *  Get all the users added into the  allUserIds and put the RoleInfo with
     *  UserId Key for each displayable user
     *  @param UserID
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

        UserWrapper ruinfo;
        HierarchyUtil.RoleNodeWrapper[] lstchildUserNodes = rolwraps.myChildNodes;

        if (lstchildUserNodes != null) {
            for (HierarchyUtil.RoleNodeWrapper rolewrapper : lstchildUserNodes) {
                if (roleWrapper.myUsers != null && roleWrapper.myUsers.size() == 1) {
                    alluserIds      = new Set<Id>();
                    ruinfo          = new UserWrapper();
                    ruinfo.roleId   = rolewrapper.myRoleId;
                    ruinfo.roleName = rolewrapper.myroleName;
                    ruinfo.userId   = rolewrapper.myUsers.get(0).Id;
                    ruinfo.userName = rolewrapper.myUsers.get(0).Name;
                    mapUsers.put(ruinfo.userId, ruinfo); // Put key with child
                    roleTraverse (rolewrapper);

                    alluserIds.add(ruinfo.userId);
                    allTotaluserIds.add(ruinfo.userId);
                    mapSecondLevelUsersAndChild.put(ruinfo.userId, alluserIds);

                }
                // if manager and many users assigned to the role then get only mapped user
                if (roleWrapper.myUsers != null && roleWrapper.myUsers.size() > 1
                && !roleWrapper.isLeafNode) {
                    alluserIds = new Set<Id>();

                    ruinfo            = new UserWrapper();
                    ruinfo.roleId     = rolewrapper.myRoleId;
                    ruinfo.roleName   = rolewrapper.myroleName;
                    ruinfo.userId     = rolewrapper.myMappedUser.Id;
                    ruinfo.userName   = rolewrapper.myMappedUser.Name;
                    mapUsers.put(ruinfo.userId, ruinfo); // Put key with child
                    roleTraverse(rolewrapper);

                    alluserIds.add(ruinfo.userId);
                    allTotaluserIds.add(ruinfo.userId);
                    mapSecondLevelUsersAndChild.put(ruinfo.userId, alluserIds);
                }
                //if leafnode role then get all users
                if (roleWrapper.myUsers != null && roleWrapper.myUsers.size() > 1
                && roleWrapper.isLeafNode) {
                    for(User myusr : rolewrapper.myUsers) {
                        alluserIds      = new Set<Id>();
                        ruinfo          = new UserWrapper();
                        ruinfo.roleId   = rolewrapper.myRoleId;
                        ruinfo.roleName = rolewrapper.myroleName;
                        ruinfo.userId   = myusr.Id;
                        ruinfo.userName = myusr.Name;
                        mapUsers.put(myusr.Id, ruinfo); // Put key with child
                        roleTraverse(rolewrapper);

                        alluserIds.add(myusr.Id);
                        allTotaluserIds.add(myusr.Id);
                        mapSecondLevelUsersAndChild.put(myusr.Id, alluserIds);

                    } //end for
                } //end if
            } //end for
        } //end if

        // mapped/search user
        ruinfo           = new UserWrapper();
        ruinfo.roleId    = usr.UserRoleId;
        ruinfo.roleName  = usr.UseRrole.Name;
        ruinfo.userId    = userId;
        ruinfo.userName  = usr.Name;
        mapUsers.put(userId, ruinfo);
    }

    /**
     * Recursively traverse a given roleId
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
                    roleTraverse(rw);
                }
           } //end for
       } //end if
    }

    /**
     *  Agreegate the records by owner for display as we are having one record
     *  for a quater and  we store as months
     *  @param List of SpecialistForecastlineItem__c
     */
    private Map<Id, SFcastLineItem> aggregatelineItemsByOwner(
    SpecialistForecastlineItem__c[] inpLineItemList) {
        Map<Id, SFcastLineItem> ownerItems = new Map<Id, SFcastLineItem>();
        Decimal closedAmount   = 0;
        Decimal commitAmount   = 0;
        Decimal bestCaseAmount = 0;
        Decimal pipelineAmount = 0;
        Decimal commitOverrideAmount;
        Decimal bestCaseOverrideAmount;
        Decimal commitMgrOvrrideAmount;
        Decimal bestCaseMgrOverrideAmount;

        SFcastLineItem splv;
        String currentOwnerId;
        String previousOwnerId;

        for (SpecialistForecastlineItem__c sfl : inpLineItemList) {
            currentOwnerId = sfl.ForecastOwner__c;

            closedAmount   = defaultIfNull(sfl.originalClosedAmount__c);
            commitAmount   = defaultIfNull(sfl.originalCommitAmount__c);
            bestCaseAmount = defaultIfNull(sfl.originalbestCaseAmount__c);
            pipelineAmount = defaultIfNull(sfl.originalPipelineAmount__c);

            commitOverrideAmount =
                defaultIfNull(sfl.SpecialistForecastOverride__r.ownOverriddenCommitAmount__c);
            bestCaseOverrideAmount =
                defaultIfNull(sfl.SpecialistForecastOverride__r.ownOverriddenBestcaseAmount__c);
            commitMgrOvrrideAmount =
                defaultIfNull(sfl.SpecialistForecastOverride__r.managerOverriddenCommitAmount__c);
            bestCaseMgrOverrideAmount =
                defaultIfNull(sfl.SpecialistForecastOverride__r.managerOverriddenBestcaseAmount__c);

            splv = ownerItems.get(currentOwnerId);

            if (splv == null) { splv = new SFcastLineItem(); }

            if (previousOwnerId != currentOwnerId) {
                 previousOwnerId                = currentOwnerId;
                 splv.closedAmount              = closedAmount;
                 splv.commitAmount              = commitAmount;
                 splv.bestCaseAmount            = bestCaseAmount;
                 splv.pipelineAmount            = pipelineAmount;
                 splv.commitOverrideAmount      = commitOverrideAmount;
                 splv.bestCaseOverrideAmount    = bestCaseOverrideAmount;
                 splv.mgrCommitOverrideAmount   = commitMgrOvrrideAmount;
                 splv.mgrBestCaseOverrideAmount = bestCaseMgrOverrideAmount;
                 splv.submittedDate             = (sfl.submitteddate__c != null ?
                     sfl.submitteddate__c.format() : defaultIfNull(splv.submittedDate));
                 splv.isSubmitted               = (sfl.Submitteddate__c != null ||
                      defaultIfNull(splv.isSubmitted));

                 splv.MycommitOverridden =
                     sfl.SpecialistForecastOverride__r.OwnOverriddenCommitAmount__c != null;
                 splv.myBestCaseOverridden =
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
            } else { // previousOwnerId == currentOwnerId
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
                    != null || splv.myCommitOverridden);
                splv.myBestCaseOverridden =
                    (sfl.SpecialistForecastOverride__r.OwnOverriddenBestcaseAmount__c
                    != null || splv.myBestCaseOverridden);
                splv.mgrCommitOverridden =
                    (sfl.SpecialistForecastOverride__r.ManagerOverriddenCommitAmount__c
                    != null || splv.mgrCommitOverridden);
                splv.mgrBestCaseOverridden =
                    (sfl.SpecialistForecastOverride__r.ManagerOverriddenBestcaseAmount__c
                    != null || splv.mgrBestCaseOverridden);
                splv.submittedDate = (sfl.submitteddate__c != null ?
                    sfl.submitteddate__c.format() : defaultIfNull(splv.submittedDate));
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

            ownerItems.put(currentOwnerId, splv);
        } //end for

        return ownerItems;
    }

    /** */
    private void setRoleUserValue(HierarchyUtil.RoleNodeWrapper wrap, User myusr) {
        UserWrapper ruInfo = new UserWrapper();
        ruInfo.roleId      = wrap.myRoleId;
        ruInfo.roleName    = wrap.myroleName;
        ruInfo.userId      = myUsr.Id;
        ruInfo.userName    = myUsr.Name;
        mapUsers.put(myUsr.Id, ruInfo); // Put the key with the child

        roleTraverse(wrap);
        alluserIds.add(myUsr.Id);
        allTotaluserIds.add(myUsr.Id);
        mapSecondLevelUsersAndChild.put(myUsr.Id, alluserIds);
    }

    /***************    Specialist Forecast Override     **********************/
    /** */
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
           UPSERT spovr ExtIdentifier__c;
       } catch(System.DMLException ex) {
           ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
               'Failed to update the Override value'));
           return null;
        }

        String[] lstMonths = ForecastHelper.getMonthsInQuarter(selectedFQ);

        spc.ExtIdentifier__c = lineItemOwnerId + SelectedFY + lstMonths[0];
        spc.SpecialistForecastOverride__c = spovr.Id;
        spc.Month__c = lstMonths[0];
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

    /** */
    private boolean isSelfOverride() {
        return UserInfo.getUserId() == lineItemOwnerId;
    }

    /** Sets default values for all variables. */
    private void initializeOverrideVariables() {
        overrideAmount    = null;
        originalAmount    = 0;
        originalOldAmount = 0;
        overrideComment   = '';
        strRecordId       = '';
        strField          = '';
        lineItemOwnerId   = '';
    }

    /** Returns the current user working with forecasts */
    public String getPreparedByUser() {
        return preparedByUser;
    }

    /** */
    public void showOverridePopup() {
        displayPopup = true;
    }

    /** Close the override popup and set the values to defualt for next time */
    public void closeOverridePopup() {
        displayPopup = false;
        initializeOverrideVariables();
    }
    /***************  Specialist Forecast Override End   **********************/

    /***************    Specialist Forecast DrillDown    **********************/
    /** */
    public void closeDrilDownPopup() {
        displayDrilDownPopup = false;
    }

    /** */
    public void showDrilDownPopup() {
        if (selectedFQ != null && selectedFY != null && lineItemOwnerId != null) {
            getSpecialistForecastRecords();
        }
        displayDrilDownPopup = true;
    }

    /** Sets specialistForecastList to forecast records in selected FQ and FY */
    private void getSpecialistForecastRecords() {
        String[] lstStatus = new String[0];
        lstStatus.add(strField);

        if (strField == CLOSED) { lstStatus.add(CLOSED_WON); }

        String[] lstMonths = ForecastHelper.getMonthsInQuarter(selectedFQ);
        // FIXME why oppty __c?
        specialistForecastList = [SELECT Opportunity__c, Opportunity__r.Name,
            Account__c, FiscalYear__c, Month__c, ForecastAmount__c,
            ForecastCategory__c, ForecastCloseDate__c, ForecastStage__c
            FROM SpecialistForecast__c WHERE FiscalYear__c = :SelectedFY
            AND Month__c in :lstMonths AND OwnerId = :lineItemOwnerId
            AND ForecastCategory__c in :lststatus];
    }
    /***************  Specialist Forecast DrillDown End  **********************/

    /** Return 0 if INP is null, else inp. */
    private static Integer defaultIfNull(Integer inp) {
        return inp == null ? 0 : inp;
    }

    /** Return 0 if INP is null, else inp. */
    private static Decimal defaultIfNull(Decimal inp) {
        return inp == null ? 0 : inp;
    }

    /** Return '' if INP is null, else inp. */
    private static String defaultIfNull(String inp) {
        return inp == null ? '' : inp;
    }

    /** Return false if INP is null, else inp. */
    private static Boolean defaultIfNull(Boolean inp) {
        return inp == null ? false : inp;
    }

    /** TODO */
    public String getUserNameFromMap(String uid) {
        if (mapUsers.containsKey(uid)) {
            return mapUsers.get(uid).userName;
        } else {
            try {
                return [SELECT Name FROM User WHERE Id = :uid limit 1].Name;
            } catch (Exception ex) {
                return ''; // FIXME better error handling
            }
        }
    }

    // FIXME
    private void debugFN() {
        System.debug('\n\n ########### DEBUG ########### \n\n');
        System.debug('\n MAP USERS: \n\t' + mapUsers);
        System.debug('\n LINE ITEMS LIST\n\t' + lineItemList);
        System.debug('\n SIZE: \t' + lineItemList.size());
        if (lineItemList.size() > 0)
            System.debug('\n LAST ADDED: \t' + lineItemList[lineItemlist.size() - 1].userId);
        System.debug('');

        System.debug('\n\n ########### DEBUG ########### \n\n\n');
    }

    // FIXME
    private void deDupe() {
        Set<SFcastLineItem> lineSet = new Set<SFcastLineItem>();
        lineSet.addAll(lineItemList);
        lineItemList.clear();
        lineItemList.addAll(lineSet);
    }
}
