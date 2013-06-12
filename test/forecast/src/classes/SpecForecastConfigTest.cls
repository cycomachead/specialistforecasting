/**
 * Class for testing forecast configurations custom settings
 * @author 
 * @since
 */

private class SpecForecastConfigTest {

    // Test the data Retrieval from ForecastConfigurations custom settings
    private static testMethod void testRetrieveForecastConfigurations() {
        Map<String, String> fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(TestForecastUtil.BU_ENTERPRISE);
        if (fConfig == null) {
            System.assert(false, 'Null Item Found, test failed');
        }
        System.Assert(fConfig.get('ConfigurationValue').contains('AMER-FS,JP-FS,EMEA-FS,APAC-FS'));
        System.Assert(fConfig.get('ConfigurationCategory').contains(TestForecastUtil.CAT_BUSINESS_UNIT));
        System.Assert(fConfig.get('IsActive') == 'true');

        try {
            Set<String> valuesForEcommMember = RetrieveForecastConfigurations.getForecastConfigurations(TestForecastUtil.CAT_ECOMM_MEMBER);
            System.Assert(false); // should have thrown exception
        } catch(RetrieveForecastConfigurations.ForecastConfigurationsException rfce) {
            System.Assert(rfce.getMessage().contains('There is no category as '));
        }


        Set<String> bUnits = RetrieveForecastConfigurations.getForecastConfigurationNames(TestForecastUtil.CAT_BUSINESS_UNIT);
        System.Assert(bUnits.contains('Enterprise'));
        System.Assert(bUnits.contains('Commercial'));
        System.Assert(bUnits.contains('Public Sector'));
        System.Assert(bUnits.contains('Radian6'));
        System.Assert(bUnits.contains('Data.com'));

        Set<String> geos = RetrieveForecastConfigurations.getForecastConfigurationNames(TestForecastUtil.CAT_GEO);
        System.Assert(geos.contains('AMER-FS'));
        System.Assert(geos.contains('JP-FS'));
        System.Assert(geos.contains('EMEA-FS'));
        System.Assert(geos.contains('APAC-FS'));
        System.Assert(geos.contains('AMER-PUBSEC'));
        System.Assert(geos.contains('R6-COO'));
        System.Assert(geos.contains('Data-Services'));
        System.Assert(geos.contains('Data-SVP'));
        System.Assert(geos.contains('Data-Marketing'));
        System.Assert(geos.contains('Data-Alliances'));

        Set<String> validSubtypesNames = RetrieveForecastConfigurations.getForecastConfigurationNames(TestForecastUtil.CAT_VALID_SUBTYPES);
        System.Assert(validSubtypesNames.contains(TestForecastUtil.CAT_VALID_SUBTYPES_SALES));
        System.Assert(validSubtypesNames.contains(TestForecastUtil.CAT_VALID_SUBTYPES_OUTLOOK));
        System.Assert(validSubtypesNames.contains(TestForecastUtil.CAT_VALID_SUBTYPES_PLAN));

        Set<String> validSubtypesValues = RetrieveForecastConfigurations.getForecastConfigurations(TestForecastUtil.CAT_VALID_SUBTYPES);
        System.Assert(validSubtypesValues.contains(TestForecastUtil.SUBTYPE_CLOSED + ',' + TestForecastUtil.SUBTYPE_COMMIT + ',' + TestForecastUtil.SUBTYPE_BEST_CASE + ',' + TestForecastUtil.SUBTYPE_PIPELINE));
        System.Assert(validSubtypesValues.contains(TestForecastUtil.SUBTYPE_OUTLOOK));
        System.Assert(validSubtypesValues.contains(TestForecastUtil.SUBTYPE_PLAN));

    }

    // Test the extra data Retrieval from ForecastConfigurations custom settings
    private static testMethod void testExtraRetrieveForecastConfigurations() {

        Map<String, String> fConfig;
        //Test Commercial Geos
        fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(TestForecastUtil.BU_COMMERCIAL);
        if (fConfig == null) {
            System.asser t(false, 'Null Item Found, test failed');
        }

        System.Assert(fConfig.get('ConfigurationCategory').contains(TestForecastUtil.CAT_BUSINESS_UNIT));
        System.Assert(fConfig.get('IsActive') == 'true');

        //Test Public Sector Geos
        fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(TestForecastUtil.BU_PUBLIC_SECTOR);
        System.Assert(fConfig.get('ConfigurationValue').contains('AMER-PUBSEC'));
        System.Assert(fConfig.get('ConfigurationCategory').contains(TestForecastUtil.CAT_BUSINESS_UNIT));
        System.Assert(fConfig.get('IsActive') == 'true');

        //Test Data.com Geos
        fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(TestForecastUtil.BU_DATA_DOT_COM);
        
        System.Assert(fConfig.get('ConfigurationCategory').contains(TestForecastUtil.CAT_BUSINESS_UNIT));
        System.Assert(fConfig.get('IsActive') == 'true');

        //Test Radian6 Geos
        fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(TestForecastUtil.BU_RADIAN6);
        System.Assert(fConfig.get('ConfigurationValue').contains('R6-COO'));
        System.Assert(fConfig.get('ConfigurationCategory').contains(TestForecastUtil.CAT_BUSINESS_UNIT));
        System.Assert(fConfig.get('IsActive') == 'true');

        //Test Heroku Geos
        fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(TestForecastUtil.BU_HEROKU);

        System.Assert(fConfig.get('ConfigurationCategory').contains(TestForecastUtil.CAT_BUSINESS_UNIT));
        System.Assert(fConfig.get('IsActive') == 'true');

        Set<String> geos = RetrieveForecastConfigurations.getForecastConfigurationNames(TestForecastUtil.CAT_GEO);
        System.Assert(geos.contains('AMER/EMEA-CS'));
        System.Assert(geos.contains('APAC-CS'));
        System.Assert(geos.contains('JP-CS'));

    }

}
