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
