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
 * Util Class used for Forecast Test
 *
 * @author Bob
 * @since 178.store
 */
public with sharing class SpecForecastTestUtil {

    public static final String UC_SALES_OUTLOOK_PLAN_REPORT = 'Sales Forecast Report with Finance Outlook and Plan';
    public static final String UC_SALES_FORECAST_REPORT_GEO = 'Sales Forecast Report By Geo';
    public static final String UC_SALES_FORECAST_REPORT_REGION = 'Sales Forecast Report By Region';
    //public static final String UC_DEAL_REPORTS = Label.lblReportsAndDashboards; // 'Reports And Dashboards'

    public static final String BU_ENTERPRISE = 'Enterprise';
    public static final String BU_COMMERCIAL = 'Commercial';
    public static final String BU_PUBLIC_SECTOR = 'Public Sector';
    public static final String BU_DATA_DOT_COM = 'Data.com';
    public static final String BU_RADIAN6 = 'Radian6';
    public static final String BU_HEROKU = 'Heroku';
    public static final String CORE_SUBTOTAL = 'Core Subtotal';
    public static final String GRAND_TOTAL = 'Total';

    public static final String REPORT_TYPE_SALES_FORECAST = 'Sales Forecast';
    public static final String REPORT_TYPE_PLAN = 'Plan';
    public static final String REPORT_TYPE_OUTLOOK = 'Outlook';

    public static final String SUBTYPE_CLOSED = 'Closed';
    public static final String SUBTYPE_COMMIT = 'Commit';
    public static final String SUBTYPE_BEST_CASE = 'Best Case';
    public static final String SUBTYPE_PIPELINE = 'Pipeline';
    public static final String SUBTYPE_PLAN = 'Plan';
    public static final String SUBTYPE_OUTLOOK = 'Outlook';

    public static final String RECORD_TYPE_CURRENT = 'CurrentForecastData';
    public static final String RECORD_TYPE_HISTORICAL = 'HistoricalActualsData';

    public static final String DEFAULT_FOR_BU = 'DefaultForBU';
    public static final String DEFAULT_FOR_GEO = 'DefaultForGeo';
    public static final String NOT_APPLICABLE = 'N/A';

    public static final String LINK_TYPE_BU = 'ByBusinessUnit';
    public static final String LINK_TYPE_GEO = 'ByGeo';

    public static final String CAT_ECOMM_MEMBER = 'EcommMember';
    public static final String CAT_BIG_DEAL_REPORT = 'BigDealReport';
    public static final String CAT_BUSINESS_UNIT = 'BusinessUnitName';
    public static final String CAT_GEO = 'GeoName';
    public static final String CAT_SUPPORTED_DATA = 'SupportedData';
    public static final String CAT_SUPPORTED_FY = 'Supported Fiscal Years';
    public static final String CAT_VALID_SUBTYPES = 'ValidSubtypes';
    public static final String CAT_VALID_SUBTYPES_SALES = 'ValidSubtypesSales';
    public static final String CAT_VALID_SUBTYPES_OUTLOOK = 'ValidSubtypesOutlook';
    public static final String CAT_VALID_SUBTYPES_PLAN = 'ValidSubtypesPlan';
    
    public static final String HIERARCHY_TYPE_ROLE = 'ROLE';
    public static final String HIERARCHY_TYPE_USER = 'USER';    

    
}

