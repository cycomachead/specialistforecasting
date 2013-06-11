/**
 * To retrieve forecast configurations
 *
 * @author auzzaman
 * @since 178.store
 */

public with sharing class RetrieveForecastConfigurations {

    private static Map<String, Set<String>> forecastConfigs = new
        Map<String, Set<String>>();
    private static Map<String, Set<String>> forecastConfigNames = new
        Map<String, Set<String>>();

    public with sharing class ForecastConfigurationsException extends Exception {}

    /**
     * This method will fetch forecast configurations custom settings
     * @return TODO
     */
    public static Map<String, String> getForecastConfigurationsByName(String name) {
        Map<String,String> retData = new Map<String,String>();
        ForecastConfigurations__c data = ForecastConfigurations__c.getInstance(name);
        if (data == null) {
            // throw new ForecastConfigurationsException('No data found by name: ' + name);
            return null;
        }
        retData.put('Name', data.Name);
        retData.put('ConfigurationCategory', data.ConfigurationCategory__c);
        retData.put('ConfigurationValue', data.ConfigurationValue__c);
        retData.put('IsActive', String.valueOf(data.IsActive__c));
        retData.put('Comment', data.Comment__c);
        return  retData;
    }

    /**
     *  This method will fetch the list of forecast configurations for a given
     *  category FROM custom settings
     *  @return TODO
     */
    public static Set<String> getForecastConfigurations(String category) {
        if (String.isEmpty(category)) {
            throw new ForecastConfigurationsException('Empty cateogry supplied.');
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
            throw new ForecastConfigurationsException('There is no category as '
                + category);
        }
        return forecastConfigs.get(category);
    }

    /**
     *  This method will fetch the list of forecast configurations for a given
     *  category FROM custom settings
     *  @return TODO
     */
    public static Set<String> getForecastConfigurationNames(String category) {
        if(String.isEmpty(category)) {
            throw new ForecastConfigurationsException('Empty cateogry supplied.');
        }
        // Initialize the custom settings, if it's not already initialized
        if (forecastConfigNames.size() == 0) {
            for (ForecastConfigurations__c configuration : ForecastConfigurations__c.getAll().values()) {
                if (!configuration.IsActive__c) {
                    // don't add it in the list of valid configuration categories or values
                } else if (forecastConfigNames.containsKey(configuration.ConfigurationCategory__c)) {
                    Set<String> currentValues =
                        forecastConfigNames.get(configuration.ConfigurationCategory__c);
                    currentValues.add(configuration.Name);
                } else {
                    Set<String> newValues = new Set<String>();
                    newValues.add(configuration.name);
                    forecastConfigNames.put(configuration.ConfigurationCategory__c,
                         newValues);
                }
                    }
        }
        if (!forecastConfigNames.containsKey(category)) {
            throw new ForecastConfigurationsException('There is no category as '
                + category);
        }
        return forecastConfigNames.get(category);
    }

    /**
     * Check in custom settings if the supplied User is present in
     * forecast configuration.
     * @return true IFF the supplied User is present in forecast configuration
     */
    public static boolean checkCategory(String userId, String category) {
        if (String.isEmpty(userId)) {
            throw new ForecastConfigurationsException('Error: Invalid User ' +
                ' Id supplied.');
        }

        if (String.isEmpty(category)) {
            return false;
        }
        String shortUserId = (userId.length() > 15) ? userId.substring(0, 15) : userId;
        /* FIXME: Remove before prod! */
        System.debug('userId = ' + userId + ' and shortUserId = ' + shortUserId);
        Set<String> privilegedUsers = null;

        try {
            privilegedUsers = getForecastConfigurations(category);
        } catch(Exception ex) {
            /* FIXME: Remove before prod! */
            System.debug('Exception while calling ' +
                'RetrieveForecastConfigurations.getForecastConfigurations(category): '
                    + ex.getMessage());
        }

        if (privilegedUsers != null && privilegedUsers.contains(shortUserId) ||
        privilegedUsers.contains(userId)) {
            // /* FIXME: Remove before prod! */
            System.debug('Returning true for category = ' + category +
                ' WHERE privilegedUsers.size() = ' + privilegedUsers.size());
            return true;
        }

        // /* FIXME: Remove before prod! */
        System.debug('Returning false for userId = ' + userId);
        return false;
    }

}

