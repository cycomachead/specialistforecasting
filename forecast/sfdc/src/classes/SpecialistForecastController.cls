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
 *  TODO -- main class description
 *
 */
public with sharing class SpecialistForecastController {

    /** Static helper strings and error messages. */
    private static final String CLOSED      = ForecastHelper.CLOSED;
    private static final String CLOSED_WON  = ForecastHelper.CLOSED_WON;
    private static final String COMMIT_MSG  = ForecastHelper.COMMIT_MSG;
    private static final String BEST_CASE  = 'Best Case';
    private static final String QTR_ALL     = ForecastHelper.QTR_ALL;
    private static final String FOR_ID      =
            SpecialistForecastException.FOR_ID;
    private static final String USER_ID_NULL =
            SpecialistForecastException.USER_ID_NULL + FOR_ID;
    private static final String ROLE_ID_NULL =
            SpecialistForecastException.ROLE_ID_NULL + FOR_ID;
    private static final String USER_MISSING_ID =
            SpecialistForecastException.USER_MISSING_ID;
    private static final String NO_ACCESS =
            SpecialistForecastException.NO_ACCESS;
    private static final String UNEXPECTED =
            SpecialistForecastException.UNEXPECTED;
    private static final String NO_PERMS = 'You do not have permission to view reports for this user.';
    private static final String REFRESH_SUCCESS = 'Data refresh request submitted successfully.';

    // The max size of the recent users list, mostly to keep things manageable.
    private static final Integer MAX_BACK_SIZE = 16;
    // Access Check if user can view forecast reports
    private Boolean canView = true;
    // TODO
    private Map<Id,Set<Id>> mapSecondLevelUsersAndChild = new Map<Id,Set<Id>>();
    // Keeps track of User and Role details for forecast records
    private Map<Id,UserWrapper> mapUsers = new Map<Id,UserWrapper>();

    // UI lists for moving around and viewing forecasts data
    private SelectOption[] recentsList  = new SelectOption[0];
    private SelectOption[] fiscalYears;
    private SelectOption[] fiscalQuarters;
    // Holds the UID of the user selected from the recents picklist
    public String selectedBackUser      { get;set; }
    //
    public String selectedFY            { get;set; }
    //
    public String selectedFQ            { get;set; }
    // User Name for page section heading on the VF page
    public String preparedByUser        { get;set; }
    // First name of preparedByUser for table column headings
    // This should be set to 'My' when the user is viewing their own data.
    public String preparedByName        { get;set; }

    // TODO
    private Set<Id> allUserIDs      = new Set<Id>();
    //
    private Set<Id> allTotaluserIDs = new Set<Id>();

    // Line Item Object for the forecast owner (person working with the VF page)
    public SpecialistForecastlineItem__c sfLI             { get;set; }
    // list that drives the main forecasts table / view on VF page
    public SFcastLineItem[] lineItemList = new SFcastLineItem[0];
    // Working Line items to be added to the lineItemList as data is processed.
    public SFcastLineItem       currLine = new SFcastLineItem();
    // The "Total" (last) line of the table on the VF page.
    public SFcastLineItem      totalLine = new SFcastLineItem();

    /** Variables used for dealing with override and drilldown views. */
    // True IFF not viewing the quarter "All" and can override a forecast
    public Boolean canOverride          { get;set; }
    // True while the forecast override popup is active
    public Boolean displayPopup         { get;set; }
    // True while the forecast details popup is active
    public Boolean displayDrilDownPopup { get;set; }
    // values used while overriding forecast data (from popup)
    public Decimal originalAmount       { get;set; }
    // TODO
    public Decimal originalOldAmount    { get;set; }
    // TODO
    public Decimal overrideAmount       { get;set; }
    // the list which holds forecast records for a detial / drilldown view
    public SpecialistForecast__c[] forecastDetailsList { get;set; }
    //
    public String overrideComment       { get;set; }
    //
    public String strRecordId           { get;set; }
    // OwnerId of the selected row when an action is taken (override, etc)
    public String lineItemOwnerID       { get;set; }
    //
    public String fiscalYear            { get;set; }
    //
    public String fiscalQuarter         { get;set; }
    //
    public String selectedUserId        { get;set; }
    //
    public String selectedRoleId        { get;set; }
    /**
     *  Used on VF page to title the drilldown popup with proper category title
     *  NOTE: This is also used in the query for ForecastCategory__c when getting
     *  records for the drilldown view.
     *  It's values _MUST_ be identical to ForecastCategory__c!
     */
    public String categoryTitle         { get;set; }

    // Simple wrapper for role and user info, mostly for use in maps.
    public with sharing class UserWrapper {
        public Id     roleId    { get;set; }
        public Id     userId    { get;set; }
        public String roleName  { get;set; }
        public String userName  { get;set; }
        public String firstName { get;set; } //used for the VF page.
    }

    /**
     *  Constructs a new Specialist Forecast Controller Class
     *  If no FY or FQ is provided, then the current system time is used.
     */
    public SpecialistForecastController() {
        try {
            sfLI = new SpecialistForecastlineItem__c();
            sfLI.ForecastOwner__c = UserInfo.getUserId();
            setPreparedBy(sfLI.ForecastOwner__c);
            if (selectedFY == null) {
                selectedFY = ForecastHelper.getCurrentFiscalYear();
            }
            if (selectedFQ == null) {
                selectedFQ = ForecastHelper.getCurrentQuarter();
            }
        } catch (Exception ex) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                UNEXPECTED + ex.getMessage()));
        }
    }

    /** Returns canOverride, controling ability to submit an override. */
    public Boolean getCanOverride() {
        return canOverride;
    }
    /** True IFF the user isn't viewing the year forecast */
    public void setCanOverride() {
        canOverride = selectedFQ != QTR_ALL;
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

    /** Called from VF page; true IFF user can Refresh forecast reports */
    public Boolean getViewAccessToRefreshButton() {
        try {
            return ForecastHelper.checkForecastDataSyncAccessForCurrentUser();
        } catch (Exception ex) {
            return false;
        }
    }

    /** Returns the list of fiscal quaters as a SelectOption[] for VF page */
    public SelectOption[] getFiscalQuarters() {
        if (fiscalQuarters == null) {
            fiscalQuarters = new SelectOption[] {new SelectOption(QTR_ALL, QTR_ALL)};

            for (Integer i = 1; i < 5; i += 1) {
                fiscalQuarters.add(new SelectOption('Q' + i, 'Q' + i));
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

        String name = getUserInfo(sfLI.ForecastOwner__c).userName;
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
                    NO_PERMS));
                return null;
            }
            canView = true;
            if (sfLI.ForecastOwner__c != null) {
                setPreparedBy(sfLI.ForecastOwner__c);
            }
        } catch (Exception ex) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                UNEXPECTED + ex.getMessage()));
        }
        return null;
    }

    /** Called from userlink for drilldown */
    public PageReference reportDrildownByUserRole() {
        if (selectedUserId != null && selectedUserId.length() > 10) {
            sfLI.ForecastOwner__c = selectedUserId;
            setPreparedBy(sfLI.ForecastOwner__c);
        }
        return null;
    }

    /** Called from submit button */
    public PageReference submitReport() {
        if (selectedFQ == QTR_ALL) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,
                'You can\'t submit for quarter "All".' +
                'Please select a different quarter to submit.'));
            return null;
        }
        String strMonth = ForecastHelper.getMonthsInQuarter(selectedFQ)[0];
        SpecialistForecastlineItem__c sfLI = new SpecialistForecastlineItem__c();
        sfLI.ForecastOwner__C =  sfLI.ForecastOwner__c;
        sfLI.FiscalYear__c = SelectedFY;
        sfLI.Month__c = strMonth;
        sfLI.submittedDate__c = System.now();
        sfLI.ExtIdentifier__c = sfLI.ForecastOwner__c + selectedFY + strMonth;
        UPSERT sfLI ExtIdentifier__c;
        ApexPages.addmessage(new ApexPages.message(ApexPages.severity.CONFIRM,
            'Report Submitted successfully'));
       return null;
    }

    /** Returns the link of where the uder will go back to. */
    public PageReference backLink() {
        if (selectedBackUser != null) {
            sfLI.ForecastOwner__c = selectedBackUser;
            setPreparedBy(sfLI.ForecastOwner__c);
        }
        return null;
    }

    /**
     *  Called from specialist forecast page Refresh Button to get the latest
     *  data from SpecialistForecast Object to SpecialistForecast lineItem
     */
    public PageReference refreshSpecialistForecastDataNow() {
        Integer  BATCH_SIZE = 500;
        SpecialistForecastBatch spfBatch = new SpecialistForecastBatch();
        spfBatch.isRunningByUser = true;

        Id batchProcessId = Database.executeBatch(spfBatch, BATCH_SIZE);
        ApexPages.addmessage(new ApexPages.message(ApexPages.severity.CONFIRM,
            REFRESH_SUCCESS));
        return null;
    }

    /**
     *  TODO: REFACTOR REFACTOR REFACTOR
     *  TODO: Description and high level function...
     */
    public SFcastLineItem[] getSFcastLineItemsData() {
        // Background checks to make sure that we have the necessary perms.
        if (UserInfo.getUserRoleId() == null) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                USER_MISSING_ID));
            return null;
        }
        if (!canView || !ForecastHelper.canAccessForecasts()) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
            NO_ACCESS));
            return null;
        }

        setCanOverride();
        setRecentsList(); // Keep track of forecasts viewed for dropdown menu

        Decimal closedAmount                   = 0, // hold the data for each
                commitAmount                   = 0, // line in the table.
                bestCaseAmount                 = 0,
                commitOverrideAmount           = 0,
                bestCaseOverrideAmount         = 0,
                commitMgrOvrrideAmount         = 0,
                bestCaseMgrOverrideAmount      = 0,
                pipelineAmount                 = 0,
                quotaAmount                    = 0,
                subTotalclosedAmount           = 0, // FIXME
                subTotalcommitAmount           = 0,
                subTotalbestCaseAmount         = 0,
                subTotalcommitOverrideAmount   = 0,
                subTotalbestCaseOverrideAmount = 0,
                subTotalpipelineAmount         = 0,
                subTotalQuotaAmount            = 0;

        // TODO
        Map<Id,SFcastLineItem> ownerlineItemsMap = new Map<Id,SFcastLineItem>();

        // Current copy of individual table row before adding to the main list
        currLine = new SFcastLineItem();
        // Line item holding the values of the total row, set basic properties.
        totalLine = new SFcastLineItem();
        totalLine.makeTotalLine();
        // This is returned and the main set of data on the VF page.
        // Clear list before creating a new table / page view
        lineItemList.clear();

        HierarchyUtil.RoleNodeWrapper[] lstRoles;
        // Consolidate data TODO -
        SFcastLineItem lineItem;

        // Clear maps before adding back. Optimize this?
        mapSecondLevelUsersAndChild.clear();
        //mapUsers.clear();
        // get the current user and all level reporting users
        // Add users back to mapSecondLevelUsersAndChild
        getMappedUserInfoFromRoleHierarchy(sfLI.ForecastOwner__c);

        // Set Query paramaters and query for line items and quotas
        // Months based on selected fiscal quarter
        String[] queryMonths = ForecastHelper.getMonthsInQuarter(selectedFQ);
        // Create a set of all users to query, starting with the forecast owner
        Set<Id> queryOwnerIds = new Set<Id>{sfLI.ForecastOwner__c};
        for (Set<Id> idSet : mapSecondLevelUsersAndChild.values()) {
            queryOwnerIds.addAll(idSet);
        }
        // get the data from the table as a list
        SpecialistForecastlineItem__c[] lineItemsList =
        ForecastHelper.getAllLineItems(selectedFY, queryMonths, queryOwnerIds);
        // get Quota information for all the users
        AggregateResult[] forecastQuotas = ForecastHelper.getAllQuotas(
            selectedFY, queryMonths, queryOwnerIds);

        /********************** consolidation Start *************************/
        // Merge Forecast Quota records with aggreated forcast records for each
        // forcast owner
        // MORE NEEDED -- TODO
        ownerlineItemsMap = aggregateLineItemsByOwner(lineItemsList);
        for (SObject so : forecastQuotas) {
            AggregateResult ar = (AggregateResult) so;
            Id         ownerId = (Id) ar.get('SpecialistForecastUser__c');
            Decimal tempAmount = (Decimal) ar.get('amnt');
                      lineItem = ownerlineItemsMap.get(ownerId);

            if (lineItem != null) { //if we have an item, add the quota to it.
                lineItem.quotaAmount = tempAmount;
                ownerlineItemsMap.put(ownerId, lineItem);
            }
        }
        /********************** consolidation End *****************************/
        // TODO
        Boolean haslineItemstoDisplay = false;
        // consolidate child records with 2nd level roles/users
        for (Id lineOwnerID : mapSecondLevelUsersAndChild.keySet()) {
            Boolean mgrCommitOverride   = false; // No manager overrides
            Boolean mgrBestCaseOverride = false;
            // Iterate over a user's forcast data to create their totals.
            // Calculate values, don't add to table yet.
            for (Id childLineOwnerId : mapSecondLevelUsersAndChild.get(lineOwnerID)) {
                // lineItem is TODO
                lineItem = ownerlineItemsMap.get(childLineOwnerId);
                if (lineItem != null) {
                    haslineItemstoDisplay = true;
                    closedAmount = defaultValue(lineItem.closedAmount);
                    // if manager override amount exists then display mgroverrideamt
                    commitAmount = defaultValue(lineItem.commitAmount);
                    commitOverrideAmount = (lineItem.mgrCommitOverrideAmount !=
                        null ? lineItem.mgrCommitOverrideAmount : commitAmount);
                    bestCaseAmount = defaultValue(lineItem.bestCaseAmount);
                    bestCaseOverrideAmount = (lineItem.mgrBestCaseOverrideAmount
                        != null ? lineItem.mgrBestCaseOverrideAmount : bestCaseAmount);
                    pipelineAmount = defaultValue(lineItem.pipelineAmount);
                    quotaAmount = defaultValue(lineItem.quotaAmount);

                    // lineItem.mgrCommitOverridden = true;
                    // add to subTotalal amount variables
                    if (commitOverrideAmount > 0 && commitOverrideAmount != commitAmount
                    && childLineOwnerId != lineOwnerID) {
                        commitAmount = commitOverrideAmount;
                    }
                    subTotalcommitAmount += (lineItem.mycommitOverridden
                        ? lineItem.commitOverrideAmount : commitAmount);
                    subTotalclosedAmount += closedAmount;

                    if (bestCaseOverrideAmount != bestCaseAmount &&
                    bestCaseOverrideAmount > 0 && childLineOwnerId != lineOwnerID) {
                        bestCaseAmount = bestCaseOverrideAmount;
                    }

                    subTotalbestCaseAmount += (lineItem.myBestCaseOverridden ?
                        lineItem.bestCaseOverrideAmount : bestCaseAmount);
                    subTotalpipelineAmount += pipelineAmount;
                    subTotalQuotaAmount    += quotaAmount;

                    // manager overrides the value then diplay only that amount
                    //  no calculation else aggregation of amount will be
                    //  displayed for the user
                    if (!mgrCommitOverride && childLineOwnerId != lineOwnerID) {
                        subTotalcommitOverrideAmount += (commitOverrideAmount <= 0
                            ? commitAmount : commitOverrideAmount);
                    } else {
                        mgrCommitOverride = true;
                        subTotalcommitOverrideAmount = (commitOverrideAmount <= 0
                            ? commitAmount : commitOverrideAmount);
                    }

                    if (!mgrBestCaseOverride && childLineOwnerId != lineOwnerID) {
                        subTotalbestCaseOverrideAmount += (bestCaseOverrideAmount
                            <= 0 ? bestCaseAmount : bestCaseOverrideAmount);
                    } else {
                        mgrBestCaseOverride = true;
                        subTotalbestCaseOverrideAmount = (bestCaseOverrideAmount
                            <= 0 ? bestCaseAmount : bestCaseOverrideAmount);
                    }
                }
                closedAmount           = 0; // reset line values.
                commitAmount           = 0;
                bestCaseAmount         = 0;
                pipelineAmount         = 0;
                quotaAmount            = 0;
                bestCaseOverrideAmount = 0;
                commitOverrideAmount   = 0;
            } // end inner for loop

            UserWrapper ownerInfo = getUserInfo(lineOwnerID);
            // TODO
            Boolean b1 = ownerInfo != null;
            // FIXME  - name, logic error in else if?
            Boolean b2 = lineOwnerID != sfLI.ForecastOwner__c;
            //if not search user then assign values
            if (haslineItemstoDisplay && b1 && b2) {
                // TODO why use subTotalal?
                // currLine.reportingUser  = false; FIXME
                //currLine.canOverride    = selectedFQ != QTR_ALL;
                currLine.userName       = ownerInfo.userName;
                currLine.roleName       = ownerInfo.roleName;
                currLine.userId         = ownerInfo.userId;
                currLine.roleId         = ownerInfo.roleId;
                currLine.closedAmount   = subTotalclosedAmount;
                currLine.commitAmount   = subTotalcommitAmount;
                currLine.bestCaseAmount = subTotalbestCaseAmount;
                currLine.pipelineAmount = subTotalpipelineAmount;
                currLine.quotaAmount    = subTotalQuotaAmount;
                // lineItem is TODO
                lineItem = ownerlineItemsMap.get(lineOwnerID);
                // TODO
                if (lineItem != null) {
                    currLine.commitOverrideAmount        = !lineItem.mgrCommitOverridden
                        ? subTotalcommitAmount : subTotalcommitOverrideAmount;
                    currLine.bestCaseOverrideAmount      = !lineItem.mgrBestCaseOverridden
                        ? subTotalbestCaseAmount : subTotalbestCaseOverrideAmount;
                    currLine.myCommitOverridden          = lineItem.mgrCommitOverridden;
                    currLine.myBestCaseOverridden        = lineItem.mgrBestCaseOverridden;
                    currLine.strLineItmId                = lineItem.strLineItmId;
                    currLine.isSubmitted                 = defaultValue(lineItem.isSubmitted)
                                                        || defaultValue(currLine.isSubmitted);
                    currLine.submittedDate               = lineItem.submittedDate;
                    currLine.mgrCommitOverrideNotes      = lineItem.mgrCommitOverrideNotes;
                    currLine.mgrBestCaseOverrideNotes    = lineItem.mgrBestCaseOverrideNotes;
                    currLine.prevDirectRepCommitAmount   = lineItem.prevDirectRepCommitAmount;
                    currLine.prevDirectRepBestcaseAmount = lineItem.prevDirectRepBestcaseAmount;
                } else { // LineItem == null
                    currLine.commitOverrideAmount   = subTotalcommitAmount;
                    currLine.bestCaseOverrideAmount = subTotalbestCaseAmount;
                    currLine.myCommitOverridden     = false;
                    currLine.myBestCaseOverridden   = false;
                    currLine.strLineItmId           = '';
                }
                addLineItem();
            } else if (!haslineItemstoDisplay && b1) {
                currLine.createEmptyLineItem(selectedFQ, quotaAmount,
                    ownerInfo);
                addLineItem();
            } //end if

            // Reset Values ... TODO
            haslineItemstoDisplay          = false;
            subTotalclosedAmount           = 0;
            subTotalcommitAmount           = 0;
            subTotalbestCaseAmount         = 0;
            subTotalcommitOverrideAmount   = 0;
            subTotalbestCaseOverrideAmount = 0;
            subTotalpipelineAmount         = 0;
            subTotalQuotaAmount            = 0;
        } // end for

        // Create last line, searchUser line item, use data if it exists.
        UserWrapper ownerInfo = getUserInfo(sfLI.ForecastOwner__c);
        lineItem = ownerlineItemsMap.get(sfLI.ForecastOwner__c);
        if (lineItem != null) {
            //currLine.reportingUser          = false; FIXME
            //currLine.canOverride            = (selectedFQ != QTR_ALL);
            currLine.userName               = ownerInfo.userName;
            currLine.roleName               = ownerInfo.roleName;
            currLine.userId                 = ownerInfo.userId;
            currLine.roleId                 = ownerInfo.roleId;
            currLine.closedAmount           = defaultValue(lineItem.closedAmount);
            currLine.commitAmount           = defaultValue(lineItem.commitAmount);
            currLine.bestCaseAmount         = defaultValue(lineItem.bestCaseAmount);
            currLine.pipelineAmount         = defaultValue(lineItem.pipelineAmount);
            currLine.quotaAmount            = defaultValue(lineItem.quotaAmount);
            currLine.bestCaseOverrideAmount = defaultValue(lineItem.bestCaseOverrideAmount);
            currLine.commitOverrideAmount   = defaultValue(lineItem.commitOverrideAmount);
            currLine.myCommitOverridden     = lineItem.myCommitOverridden;
            currLine.myBestCaseOverridden   = lineItem.myBestCaseOverridden;
            currLine.strLineItmId           = lineItem.strLineItmId;
        } else { // Create Forecast line for Owner (No data found)
            currLine.createEmptyLineItem(selectedFQ, quotaAmount, ownerInfo);
        }
        addLineItem(); // Add forecast owner's line to list.

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

    /** Add the current line item to the list, increment totals, and reset */
    public void addLineItem() {
        lineItemList.add(currLine);
        // increment total line on each add
        totalLine.increment(currLine.closedAmount, currLine.commitOverrideAmount,
            currLine.commitAmount, currLine.bestCaseOverrideAmount,
            currLine.bestCaseAmount, currLine.pipelineAmount,
            currLine.quotaAmount);
        // reset line item to prevent overriding list data.
        currLine = new SFcastLineItem();
    }

    /**
     *  Get all the users added into the allUserIds and put the RoleInfo with
     *  UserId Key for each displayable user
     *  @param UserID
     *  @throws SpecialistForecastException
     */
    private void getMappedUserInfoFromRoleHierarchy(Id userId) {
        User usr;
        try {
            usr = [SELECT Name, UserRoleId, UserRole.Name, FirstName
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
                    alluserIds       = new Set<Id>();
                    ruinfo           = new UserWrapper();
                    ruinfo.roleId    = rolewrapper.myRoleId;
                    ruinfo.roleName  = rolewrapper.myroleName;
                    ruinfo.userId    = rolewrapper.myUsers[0].Id;
                    ruinfo.userName  = rolewrapper.myUsers[0].Name;
                    ruinfo.firstName = rolewrapper.myUsers[0].FirstName;
                    mapUsers.put(ruinfo.userId, ruinfo); // Put key with child
                    roleTraverse (rolewrapper);

                    alluserIds.add(ruinfo.userId);
                    allTotaluserIds.add(ruinfo.userId);
                    mapSecondLevelUsersAndChild.put(ruinfo.userId, alluserIds);

                }
                // if manager and many users assigned to the role then get only
                // mapped user
                if (roleWrapper.myUsers != null && roleWrapper.myUsers.size() > 1
                && !roleWrapper.isLeafNode) {
                    alluserIds         = new Set<Id>();
                    ruinfo             = new UserWrapper();
                    ruinfo.roleId      = rolewrapper.myRoleId;
                    ruinfo.roleName    = rolewrapper.myroleName;
                    ruinfo.userId      = rolewrapper.myMappedUser.Id;
                    ruinfo.userName    = rolewrapper.myMappedUser.Name;
                    ruinfo.firstName   = rolewrapper.myMappedUser.FirstName;
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
                        alluserIds       = new Set<Id>();
                        ruinfo           = new UserWrapper();
                        ruinfo.roleId    = rolewrapper.myRoleId;
                        ruinfo.roleName  = rolewrapper.myroleName;
                        ruinfo.userId    = myusr.Id;
                        ruinfo.userName  = myusr.Name;
                        ruinfo.firstName = myusr.FirstName;
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
        ruinfo.roleId     = usr.UserRoleId;
        ruinfo.roleName   = usr.UseRrole.Name;
        ruinfo.userId     = userId;
        ruinfo.userName   = usr.Name;
        ruinfo.firstName  = usr.FirstName;
        mapUsers.put(userId, ruinfo);
    }

    /**
     * Recursively traverse a given roleId
     * @param rolewrapper user/role indicates the starting point of traversal
     */
    private void roleTraverse(HierarchyUtil.RoleNodeWrapper rolewrapper) {
         if (rolewrapper.myChildNodes != null) { // hasChildren?
             for (HierarchyUtil.RoleNodeWrapper rw : rolewrapper.myChildNodes){
                 if (rw.myUsers != null && rw.myUsers.size() > 1 && rw.isLeafNode) {
                     for (User usr : rw.myUsers) {
                         allUserIds.add(usr.Id);
                         allTotalUserIds.add(usr.Id);
                     }
                 } // need to merge these two cases somehow.
                 if (rw.myUsers != null && rw.myUsers.size() == 1) {
                     allUserIds.add(rw.myUsers[0].Id);
                     allTotaluserIds.add(rw.myUsers[0].Id);
                 }
                 if (rw.myUsers != null && rw.myUsers.size() > 1 &&
                 !rw.isLeafNode) {
                        allUserIds.add(rw.myMappedUser.Id);
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

            closedAmount   = defaultValue(sfl.originalClosedAmount__c);
            commitAmount   = defaultValue(sfl.originalCommitAmount__c);
            bestCaseAmount = defaultValue(sfl.originalbestCaseAmount__c);
            pipelineAmount = defaultValue(sfl.originalPipelineAmount__c);

            commitOverrideAmount =
                defaultValue(sfl.SpecialistForecastOverride__r.ownOverriddenCommitAmount__c);
            bestCaseOverrideAmount =
                defaultValue(sfl.SpecialistForecastOverride__r.ownOverriddenBestcaseAmount__c);
            commitMgrOvrrideAmount =
                defaultValue(sfl.SpecialistForecastOverride__r.managerOverriddenCommitAmount__c);
            bestCaseMgrOverrideAmount =
                defaultValue(sfl.SpecialistForecastOverride__r.managerOverriddenBestcaseAmount__c);

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
                     sfl.submitteddate__c.format() : defaultValue(splv.submittedDate));
                 splv.isSubmitted               = (sfl.Submitteddate__c != null ||
                      defaultValue(splv.isSubmitted));

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
                    sfl.submitteddate__c.format() : defaultValue(splv.submittedDate));
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

    /** TODO */
    private void setRoleUserValue(HierarchyUtil.RoleNodeWrapper wrap, User myusr) {
        UserWrapper ruInfo  = new UserWrapper();
        ruInfo.roleId       = wrap.myRoleId;
        ruInfo.roleName     = wrap.myroleName;
        ruInfo.userId       = myUsr.Id;
        ruInfo.userName     = myUsr.Name;
        ruInfo.firstName    = myUsr.FirstName;
        mapUsers.put(myUsr.Id, ruInfo); // Put the key with the child

        roleTraverse(wrap);
        allUserIds.add(myUsr.Id);
        allTotaluserIds.add(myUsr.Id);
        mapSecondLevelUsersAndChild.put(myUsr.Id, alluserIds);
    }

    /******************   Override & Drilldown Methods    *********************/
    /** Returns the current user working with forecasts */
    public String getPreparedByUser() {
        return preparedByUser;
    }

    /** Returns the first name of user working with forecasts */
    public String getPreparedByName() {
        return preparedByName;
    }

    /** Returns the selected forecast category from the VF page */
    public String getCategoryTitle() {
        return categoryTitle;
    }

    /**
     *  NOTE: To be called only after sfLI.ForecastOwner__c is updated
     *  Sets perparedByUser and preparedByName to the proper values.
     */
    public void setPreparedBy(Id uid) {
        if (uid == UserInfo.getUserId()) {
            preparedbyUser = UserInfo.getName();
            preparedByName = 'My';
        } else {
            preparedByUser = getUserInfo(uid).userName;
            preparedByName = getUserInfo(uid).firstName;
            // Add appropriate ownership to the name.
            preparedByName += preparedByName.endsWith('s') ? '\'' : '\'s';
        }
        preparedByUser += preparedByUser.endsWith('s') ? '\'' : '\'s';
    }

    /** TODO */
    public PageReference saveOverride() {
        SpecialistForecastlineItem__c spc = new SpecialistForecastlineItem__c();
        SpecialistForecastOverride__c spovr = new SpecialistForecastOverride__c();
        if (categoryTitle == COMMIT_MSG) {
            if (isSelfOverride()) {
                spovr.OwnOverriddenCommitAmount__c = overrideAmount;
                spovr.OwnOverrideNotes__c = overridecomment;
            } else {
                spovr.ManagerOverriddenCommitAmount__c = overrideAmount;
                spovr.ManagerOverrideCommitNotes__c = overridecomment;
            }
            spovr.PreviousDirectRepCommitAmount__c = originaloldAmount;
        } else if (categoryTitle == BEST_CASE) {
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
       spovr.ExtIdentifier__c = lineItemOwnerID + selectedFY + selectedFQ;
       spovr.OwnerId = lineItemOwnerID;

       try {
           UPSERT spovr ExtIdentifier__c;
       } catch(System.DMLException ex) {
           ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
               'Failed to update the Override value'));
           return null;
        }

        String[] lstMonths = ForecastHelper.getMonthsInQuarter(selectedFQ);

        spc.ExtIdentifier__c = lineItemOwnerID + selectedFY + lstMonths[0];
        spc.SpecialistForecastOverride__c = spovr.Id;
        spc.Month__c = lstMonths[0];
        spc.ForecastOwner__c = lineItemOwnerID;
        spc.FiscalYear__c = selectedFY;
        spc.OwnerId = lineItemOwnerID;
        try {
            UPSERT spc ExtIdentifier__c;
        } catch(System.DMLException ex) {
            ApexPages.addmessage(new ApexPages.message(ApexPages.severity.Error,
                'Failed to update the Override Amount'));
            return null;
        }
        ApexPages.addmessage(new ApexPages.message(ApexPages.severity.CONFIRM,
            'Override Amount updated successfully'));
        return null;
    }

    /** TODO */
    private boolean isSelfOverride() {
        return UserInfo.getUserId() == lineItemOwnerID;
    }

    /** Sets default values for all variables. */
    private void initializeOverrideVariables() {
        overrideAmount    = null;
        originalAmount    = 0;
        originalOldAmount = 0;
        overrideComment   = '';
        strRecordId       = '';
        lineItemOwnerID   = '';
    }

    /** TODO */
    public void showOverridePopup() {
        displayPopup = true;
        setPreparedBy(lineItemOwnerID);
    }

    /** Close the override popup and set the values to defualt for next time */
    public void closeOverridePopup() {
        displayPopup = false;
        initializeOverrideVariables();
        setPreparedBy(sfLI.ForecastOwner__c);
    }

    /** TODO */
    public void closeDrilDownPopup() {
        displayDrilDownPopup = false;
        setPreparedBy(sfLI.ForecastOwner__c);
    }

    /** TODO */
    public void showDrilDownPopup() {
        if (selectedFQ != null && selectedFY != null && lineItemOwnerID != null) {
            setPreparedBy(lineItemOwnerID);
            getSpecialistForecastRecords();
        } else {
            // add warning TODO
        }
        displayDrilDownPopup = true;
    }

    /**
     *  Sets forecastDetailsList to forecast records in selected FQ and FY
     *  Used when generating a list for the forecast detail drilldown
     */
    private void getSpecialistForecastRecords() {
        String[] months = ForecastHelper.getMonthsInQuarter(selectedFQ);
        String[] status = new String[] { categoryTitle };

        if (categoryTitle == CLOSED) { status.add(CLOSED_WON); }

        // Use a try-catch and add warning. TODO
        try {
            forecastDetailsList = [SELECT Opportunity__c, Opportunity__r.Name,
                Account__c, FiscalYear__c, Month__c, ForecastAmount__c,
                ForecastCategory__c, ForecastCloseDate__c, ForecastStage__c
                FROM SpecialistForecast__c WHERE FiscalYear__c = :selectedFY
                AND Month__c in :months AND OwnerId = :lineItemOwnerID
                AND ForecastCategory__c in :status];
            if (forecastDetailsList.size() == 0) {
                throw new SpecialistForecastException(
                    'No Specialist Forecasts were found for ' + preparedByUser +
                    ' in ' + selectedFQ + ', ' + selectedFY + ' for the category'
                    + ' ' + categoryTitle + '.');
            }
        } catch (Exception ex) {
            ApexPages.addMessage(new ApexPages.message(ApexPages.serverity.Error,
                UNEXPECTED + ex.getMessage()));

        }

    }

    /*****************  Utily Methods for general use  ************************/
    /** Return 0 if INP is null, else inp. */
    private static Integer defaultValue(Integer inp) {
        return inp == null ? 0 : inp;
    }

    /** Return 0 if INP is null, else inp. */
    private static Decimal defaultValue(Decimal inp) {
        return inp == null ? 0 : inp;
    }

    /** Return '' if INP is null, else inp. */
    private static String defaultValue(String inp) {
        return inp == null ? '' : inp;
    }

    /** Return false if INP is null, else inp. */
    private static Boolean defaultValue(Boolean inp) {
        return inp == null ? false : inp;
    }

    /**
     *  This is a protected function for accessing the mapUsers data.
     *  Essentially, it tries to grab data from the map, and creates new data
     *  if none was found, then adds it to the map.
     *  @param uid is a UserId to search for.
     *  @return a UserWrapper object with data for the requested user.
     */
    public UserWrapper getUserInfo(String uid) {
        UserWrapper info = mapUsers.get(uid);
        if (info == null) { // Create data if none found.
            info = new UserWrapper();
            try {
                User usr       = [SELECT Name, UserRoleID, UserRole.Name, FirstName
                                  FROM User WHERE Id = :uid limit 1];
                info.userId    = uid;
                info.userName  = usr.Name;
                info.firstName = usr.FirstName;
                info.roleId    = usr.UserRoleId;
                info.roleName  = usr.UserRole.Name;
                mapUsers.put(uid, info); // Add to map to save time later.
            } catch (Exception ex) {
                ApexPages.addMessage(new ApexPages.message(
                    ApexPages.severity.ERROR, ROLE_ID_NULL + uid));
                info.userName = 'No Name Found:' + uid;
                info.userID   = uid;
                info.roleName = 'No Role Found';
                info.roleId   = '';
            }
        }
        return info;
    }

}
