# Specialist Forecasting
![icon](/assets/icon.png)
## Salesforce Labs Unmanaged Package
Use Specialist Forecasting to allow Specialist AE's to generate more specific and accurate forecasts for each Opportunity based on individual products or services. Generate reports, better manage expectations, and help revenue grow!
## Features (Extended Description)
Use Specialist Forecasting to allow Specialist AE's to generate more specific and accurate forecasts for each Opportunity based on individual products or services. Generate reports, better manage expectations, and help revenue grow!

Specialist Forecasting lets AE's create forecasts separate from the Core AE's. Maybe an AE's wants to forecast only a specific product on as part of a larger deal, and can set his/her own forecast amounts. Maybe an AE expects a deal to close at a later time and would like to have a different close date. Specialist Forecasting give you the power to have more AE's generate reports for their portions of each opportunity.

Key features include:

* Easy to use app with minimal configuration!
* Fully Customizable as an Unmanaged Package
* Support for automatic generation of forecast reports
* The ability to create a separate role hierarchy
* Allow Managers to override their reports' forecasts and submit new reports to executives
* Allow Forecasting based on specific product categories or segments
* And Many More!

## Setup and Customization
To make Specialist Forecasting work for your organization, you'll want to change a few things.
#### Salesforce Roles and Profiles
All users of SpecialistForecasting must have a UserRole to be able to access the application. (Forecasting and reports are based heavily on a role hierarchy). Additionally, a lookup filter is placed on the User object so that when searching for users, only users with a UserRole can be found.

Upon install, you will need to enable access to Specialist Forecasting on the User Profiles of users who will be working with forecasts. These are the tabs and objects they should have access to (by default). All these settings can be changes as necessary.
##### Custom App Settings
The "Specialist Forecasting" app should be set to visible.
#####  Custom Tab Settings
The following tabs should all be set to "Default On" The main tab a user needs to work with Specialist Forecasting, is the "Specialist Forecast Reports" tab. (All other tabs are significantly less important, but useful.)

* Forecast Hierarchy Configurations
* Specialist Forecast Quotas
* Specialist Forecast Batch Processing
* Specialist Forecast Reports
* Specialist Forecast Line Items
* Specialist Forecasts
* Specialist Forecast Overrides

##### Custom Object Settings
CRUD needs to be updated for the five custom objects. While you may tweak these settings as necessary, all users should have Create, Read, and Edit permissions at minimum.

* Forecast Hierarchy Configurations. (Create and and Edit are optional here.)
* Specialist Forecasts
* Specialist Forecast Line Items
* Specialist Forecast Quotas
* Specialist Forecast Overrides

##### Notes
Users, of course, will still need access to standard objects, like Opportunities and Accounts to use Specialist Forecasting. Additionally, you may add the Specialist Forecasts Reports tab to other applications (like Sales) if you feel that it fits better in another place for your company.
#### Basic Configuration Settings
Specialist Forecasting uses custom settings for some configurations. A sample set of configurations is provided as [CSV](/forecast/data/SampleConfigurations.csv) which can be data loaded into your organization. Each setting has comments as to what it controls. Currently, the use of custom settings is limited, however, you could easily expand the application to allow readable settings for a variety of additional parameters. (Read below for basic instructions.)
#### Setting Up Report Scheduling
The specialist Forecasting package allows admins to schedule report generation. This reduces overhead in terms of querying the database and store the results in SpecialistForecastLineItem__c objects. Forecasting functions perfectly fine without scheduled reports, but it requires the use of the "Refresh Now" button to make sure the data is up to date.

There are two types of batch processes which run:

* SpecialistForecastBatch -- this is the main process where reports are generated and consolidated. This is just like using the refresh button.
* SpecialistForecastSubmitBatch -- this will clear the "Submitted Date" field from all specialist forecasts. This is useful is you want to have AE's submit new forecast reports on a weekly basis. 0

Setting up Specialist Forecasting is very easy, and can be done by an admin. You can schedule forecasting to run at anytime. The following code will run Specialist Forecast jobs every Sunday at 4pm. Simply run the code in execute anonymous!

    // This forces the custom settings for batch jobs to be setup.
    // If you have already loaded custom settings, this call is optional.
    ForecastHelper.setupBatchJobs(true);
    // A standard cron style string.
    String sch = '0 0 16 ? * 1';
    // Set the Job to run in the org
    System.schedule('Specialist Forecast Scheduler Job', sch, new ForecastScheduler());
    // Notice that you're done!
    System.debug('\n\n ==> Done');

For more information on how to use the Apex System Scheduler, refer to [this page](http://www.salesforce.com/us/developer/docs/apexcode/Content/apex_scheduler.htm).
#### Forecasting Product Types
A forecast may have a product type associated with it. By default, there are two products, "Product 1" and "Product 2" which are defined in the file `/objects/SpecialistForecast__c.object`. They belong to the `ForecastType__c` field and you may simply add or change the fields in the metadata file with no ill effects. This is used mainly as a way to separate forecasts, and isn't required for the app to work properly.

### Building
Currently, deployment of specialist forecasting is done using `ant`. Move the files from [/tools](/tools), if you'd like and run the command   `ant deployForecasts`. The build file will need to be adjusted fir your installation, so modify [build.properties](/tools/build.properities) to contain the info you need. You should also enter in your Org login information.

There are quite a few targets contained in build.xml that aren't necessary for deployment, but may come in handy.
### Code Overview
Specialist Forecasting is designed much like many other Force.com apps. The bulk of the work (that a user interacts with) belongs to the SpecialistForecastController, in an Apex class and a VisualForce page. The remaining classes all contain supporting code.
##### Controller
`SpecialistForecastController.cls`
This is the back end to the main interface a user sees. It handles the querying and compiling of all information which the user interacts with, including the detail-drilldown and override popups. The interface is contained in `SpecialistForecastPage.page`.
##### Utilities
`SpecialistForecastException.cls`
This provides a generic exception for Specialist Forecasting. In general, nothing fancy is being done with any exceptions. The class also is the place holder for any error messages used in the app, as to make them easy to adapt and reuse. Additionally, it provides a good start if someone wants to translate the app to other languages.
`MonthUtil.cls`
Specialist Forecasting relies heavily on the notion of a fiscal year. This class provides a simple interface for working with dates and translating them into correct fiscal years. It queries a user's Company settings in his/her organization to determine the properties for a fiscal year. However, the functionality could be expanded to allow a user to change the fiscal year properties just for Specialist Forecasting. (Take a Look at the reset method.) Here, all methods which deal with years were treated as integer values to make the math simpler.
`HierarchyUtil.cls`
This class provides an interface for getting a user hierarchy. If no Forecast Hierarchy Configurations are used, then Specialist Forecasting will use the standard role hierarchy setup for the organization. A trigger, `ForecastHierarchyConfigurationsTrigger.trigger`, is used to validate the data for any custom hierarchies which are created.
`SFcastLineItem.cls`
This is a simple class which stores a simplified version of the `SpecialistForecastLineItem__c` object. It's used in the controller to build up the main table view on the VisualForce page. It has a couple methods which make handling totaling of the data easier.
`ForecastHelper.cls`
This class includes methods which are used in various other classes. All methods should be static in this class, as it's not designed to hold any data. Most work with fiscal years, and fiscal quarters in the package uses strings, such as "FY14", or "Q1." Forecast Helper provides and overlay to MonthUtil to handle using strings.
`RetrieveForecastConfigurations.cls`
Specialist Forecasting relies on custom settings to supply different configuration options, mostly related to batch processing. This class provides and easier interface for working with custom settings. If you want to extend Specialist Forecasting to have greater configurability through custom settings, use this class to handle the work.
##### Scheduling
`ForecastCollection.cls`
`ForecastIterable.cls`
These two classes simply provide a way of iterating over all `SpecialistForecast__c` objects.
`ForecastScheduler.cls`
This provides a simple way of scheduling both batch processes to run at the same time, and uses Custom Settings to control when and if processes are run. Currently, it only supports run both batch processes at the same time, but it's easy to extend the class to all running at different times.
`SpecialistForecastBatch.cls`
This is the main batch process which takes specialist forecast records and compiles them into line item objects. The batch process is run either from a set schedule or when a user clicks the "Refresh" button on the VisualForce page. The batch process must be run every so often to make sure that the data a user sees is accurate. (The reason for the batch process is to respect governor limits, especially with a large number of forecast records.)
`SpecialistForecastSubmitBatch.cls`
This batch process sets the submission date to null for all SpecialistForecastLineItem objects.
##License
Copyright (c) 2013, salesforce.com, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.
    * Neither the name of the salesforce.com, Inc. nor the names of its
    contributors may be used to endorse or promote products derived from this
    software without specific prior written permission.

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


