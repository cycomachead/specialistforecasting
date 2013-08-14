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
 * To retrieve forecast configurations
 */

public with sharing class RetrieveForecastConfigurations {

    private static Map<String, Set<String>> forecastConfigs = new
        Map<String, Set<String>>();
    private static Map<String, Set<String>> forecastConfigNames = new
        Map<String, Set<String>>();

    /** Error Messages and standard labels. */
    private static Final String NO_DATA_CAT =
        SpecialistForecastException.NO_DATA_CAT;
    private static Final String EMPTY_CAT =
        SpecialistForecastException.EMPTY_CAT;
    private static final String CRON_SCHED_ERR =
        SpecialistForecastException.CRON_SCHED_ERR;
    private static final String  NO_DATA_NAME =
        SpecialistForecastException.NO_DATA_NAME;

    private static final String NAME = ForecastHelper.NAME;
    private static final String CONFIG_CAT = ForecastHelper.CONFIG_CAT;
    private static final String CONFIG_VAL = ForecastHelper.CONFIG_VAL;
    private static final String ACTIVE = ForecastHelper.ACTIVE;
    private static final String COMMENT = ForecastHelper.COMMENT;

    /**
     *  This method will fetch forecast configurations custom settings
     *  @return A map of the settings data (KVP) used for forecasting
     *  @throws SpecialistForecastException
     */
    public static Map<String, String> getForecastConfigurationsByName(String inpName) {
        Map<String,String> retData = new Map<String,String>();
        ForecastConfigurations__c data =
            ForecastConfigurations__c.getInstance(inpname);
        if (data == null) {
            throw new SpecialistForecastException(NO_DATA_NAME + inpName);
        }
        retData.put(NAME, data.Name);
        retData.put(CONFIG_CAT, data.ConfigurationCategory__c);
        retData.put(CONFIG_VAL, data.ConfigurationValue__c);
        retData.put(ACTIVE, String.valueOf(data.IsActive__c));
        retData.put(COMMENT, data.Comment__c);
        return  retData;
    }

    /**
     *  This method will fetch the list of forecast configurations for a given
     *  category from custom settings
     *  @return Set<String> of all the different configuration options on the org
     *  @throws SpecialistForecastException
     */
    public static Set<String> getForecastConfigurations(String category) {
        if (String.isEmpty(category)) {
            throw new SpecialistForecastException(EMPTY_CAT);
        }
        // Initialize the custom settings, if it's not already initialized
        if (forecastConfigs.size() == 0) {
            for (ForecastConfigurations__c configuration : ForecastConfigurations__c.getAll().values()) {
                if (!configuration.IsActive__c) {
                    // don't add it in the list of valid configuration categories or values
                } else if (forecastConfigs.containsKey(configuration.ConfigurationCategory__c)) {
                    Set<String> currentValues =
                        forecastConfigs.get(configuration.ConfigurationCategory__c);
                    currentValues.add(configuration.ConfigurationValue__c);
                } else {
                    Set<String> newValues = new Set<String>();
                    newValues.add(configuration.ConfigurationValue__c);
                    forecastConfigs.put(configuration.ConfigurationCategory__c, newValues);
                }
            }
        }
        if (!forecastConfigs.containsKey(category)) {
            throw new SpecialistForecastException(NO_DATA_CAT + category);
        }
        return forecastConfigs.get(category);
    }

    /**
     *  This method will fetch the list of forecast configurations for a given
     *  category from custom settings
     *  @return a set of settings for the requested category
     *  @throws SpecialistForecastException
     */
    public static Set<String> getForecastConfigurationNames(String category) {
        if(String.isEmpty(category)) {
            throw new SpecialistForecastException(EMPTY_CAT);
        }

        // Initialize the custom settings, if it's not already initialized
        if (forecastConfigNames.size() == 0) {
            for (ForecastConfigurations__c config : ForecastConfigurations__c.getAll().values()) {
                if (!config.IsActive__c) {
                    // don't add it in the list of valid configuration categories or values
                } else if (forecastConfigNames.containsKey(config.ConfigurationCategory__c)) {
                    Set<String> currentValues =
                        forecastConfigNames.get(config.ConfigurationCategory__c);
                    currentValues.add(config.Name);
                } else {
                    Set<String> newValues = new Set<String>();
                    newValues.add(config.name);
                    forecastConfigNames.put(config.ConfigurationCategory__c, newValues);
                }
            }
        }
        if (!forecastConfigNames.containsKey(category)) {
            throw new SpecialistForecastException(NO_DATA_CAT + category);
        }
        return forecastConfigNames.get(category);
    }

    /**
     * Check in custom settings if the supplied User is present in
     * forecast configuration.
     * @return true IFF the supplied User is present in forecast configuration
     */
    public static boolean checkCategory(String userId, String category) {
        if (String.isEmpty(userId) || String.isEmpty(category)) {
             return false;
        }

        String shortID = (userId.length() > 15) ? userId.substring(0, 15) : userId;
        Set<String> privilegedUsers = getForecastConfigurations(category);

        if (privilegedUsers != null && privilegedUsers.contains(shortID) ||
        privilegedUsers.contains(userId)) {
            return true;
        }
        return false;
    }

}

