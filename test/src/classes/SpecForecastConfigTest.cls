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
 */

@isTest(SeeAllData = true)
public class SpecForecastConfigTest {

    // Fill in SFCast Custom Settings.
    private static testMethod void createTestData() {
        // New forecast config objects
    }

    // Test the data Retrieval from ForecastConfigurations custom settings
    private static testMethod void testRetrieveForecastConfigurations() {
        Map<String, String> fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(SpecForecastTestLabel.BU_ENTERPRISE);
        if (fConfig == null) {
            System.assert(false, 'Null Item Found, test failed');
        }
        System.assert(fConfig.get('ConfigurationValue').contains('AMER-FS,JP-FS,EMEA-FS,APAC-FS'));
        System.assert(fConfig.get('ConfigurationCategory').contains(SpecForecastTestLabel.CAT_BUSINESS_UNIT));
        System.assert(fConfig.get('IsActive') == 'true');

        try {
            Set<String> valuesForEcommMember = RetrieveForecastConfigurations.getForecastConfigurations(SpecForecastTestLabel.CAT_ECOMM_MEMBER);
            System.assert(false); // should have thrown exception
        } catch(SpecialistForecastException ex) {
            System.assert(true);
        } catch(Exception ex) {
            System.assert(false, 'An uxpected exception has occurred. ' + ex.getMessage());
        }


        Set<String> bUnits = RetrieveForecastConfigurations.getForecastConfigurationNames(SpecForecastTestLabel.CAT_BUSINESS_UNIT);
        System.assert(bUnits.contains('Enterprise'));
        System.assert(bUnits.contains('Commercial'));
        System.assert(bUnits.contains('Public Sector'));
        System.assert(bUnits.contains('Radian6'));
        System.assert(bUnits.contains('Data.com'));

        Set<String> geos = RetrieveForecastConfigurations.getForecastConfigurationNames(SpecForecastTestLabel.CAT_GEO);
        System.assert(geos.contains('AMER-FS'));
        System.assert(geos.contains('JP-FS'));
        System.assert(geos.contains('EMEA-FS'));
        System.assert(geos.contains('APAC-FS'));
        System.assert(geos.contains('AMER-PUBSEC'));
        System.assert(geos.contains('R6-COO'));
        System.assert(geos.contains('Data-Services'));
        System.assert(geos.contains('Data-SVP'));
        System.assert(geos.contains('Data-Marketing'));
        System.assert(geos.contains('Data-Alliances'));

        Set<String> validSubtypesNames = RetrieveForecastConfigurations.getForecastConfigurationNames(SpecForecastTestLabel.CAT_VALID_SUBTYPES);
        System.assert(validSubtypesNames.contains(SpecForecastTestLabel.CAT_VALID_SUBTYPES_SALES));
        System.assert(validSubtypesNames.contains(SpecForecastTestLabel.CAT_VALID_SUBTYPES_OUTLOOK));
        System.assert(validSubtypesNames.contains(SpecForecastTestLabel.CAT_VALID_SUBTYPES_PLAN));

        Set<String> validSubtypesValues = RetrieveForecastConfigurations.getForecastConfigurations(SpecForecastTestLabel.CAT_VALID_SUBTYPES);
        System.assert(validSubtypesValues.contains(SpecForecastTestLabel.SUBTYPE_CLOSED
            + ',' + SpecForecastTestLabel.SUBTYPE_COMMIT + ',' +
            SpecForecastTestLabel.SUBTYPE_BEST_CASE + ',' +
            SpecForecastTestLabel.SUBTYPE_PIPELINE));
        System.assert(validSubtypesValues.contains(SpecForecastTestLabel.SUBTYPE_OUTLOOK));
        System.assert(validSubtypesValues.contains(SpecForecastTestLabel.SUBTYPE_PLAN));

    }

    // Test the extra data Retrieval from ForecastConfigurations custom settings
    private static testMethod void testExtraRetrieveForecastConfigurations() {

        Map<String, String> fConfig;
        //Test Commercial Geos
        fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(SpecForecastTestLabel.BU_COMMERCIAL);
        if (fConfig == null) {
            System.assert(false, 'Null Item Found, test failed');
        }

        System.assert(fConfig.get('ConfigurationCategory').contains(SpecForecastTestLabel.CAT_BUSINESS_UNIT));
        System.assert(fConfig.get('IsActive') == 'true');

        //Test Public Sector Geos
        fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(SpecForecastTestLabel.BU_PUBLIC_SECTOR);
        System.assert(fConfig.get('ConfigurationValue').contains('AMER-PUBSEC'));
        System.assert(fConfig.get('ConfigurationCategory').contains(SpecForecastTestLabel.CAT_BUSINESS_UNIT));
        System.assert(fConfig.get('IsActive') == 'true');

        //Test Data.com Geos
        fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(SpecForecastTestLabel.BU_DATA_DOT_COM);

        System.assert(fConfig.get('ConfigurationCategory').contains(SpecForecastTestLabel.CAT_BUSINESS_UNIT));
        System.assert(fConfig.get('IsActive') == 'true');

        //Test Radian6 Geos
        fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(SpecForecastTestLabel.BU_RADIAN6);
        System.assert(fConfig.get('ConfigurationValue').contains('R6-COO'));
        System.assert(fConfig.get('ConfigurationCategory').contains(SpecForecastTestLabel.CAT_BUSINESS_UNIT));
        System.assert(fConfig.get('IsActive') == 'true');

        //Test Heroku Geos
        fConfig = RetrieveForecastConfigurations.getForecastConfigurationsByName(SpecForecastTestLabel.BU_HEROKU);

        System.assert(fConfig.get('ConfigurationCategory').contains(SpecForecastTestLabel.CAT_BUSINESS_UNIT));
        System.assert(fConfig.get('IsActive') == 'true');

        Set<String> geos = RetrieveForecastConfigurations.getForecastConfigurationNames(SpecForecastTestLabel.CAT_GEO);
        System.assert(geos.contains('AMER/EMEA-CS'));
        System.assert(geos.contains('APAC-CS'));
        System.assert(geos.contains('JP-CS'));

    }

}
