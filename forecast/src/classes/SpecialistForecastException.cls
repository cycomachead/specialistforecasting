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
 *  Basic Exception for Specialist Forecasting Unmanaged Package
 *  @author Michael Ball
 */

public with sharing class SpecialistForecastException extends Exception {

    /** These are basic labels which are meant to be shared thoughout the
     *  SpecialistForecasting Package. If they are meant to be appended to,
     *  then the label should end in a space, otherwise labels should be complete
     *  sentences whereever possible, and should avoid overly technical terms.
     *
     *  For simiplicity, all these should be public static final String.
     *  To use a label in another class in the SpecialistForecasting package do:
     *  private static Final String NAME = SpecialistForecasting.NAME;
     *  in the variables declaration section for each file.
     */

    // Complete sentences
    public static Final String EMPTY_CAT =
        'Cannot get forecast configurations for an empty category';
    public static Final String ROLE_ID_NULL =
        'A required User Role ID could be found';
    public static Final String USER_ID_NULL =
        'A required User ID could not be found';
    public static Final String PARENT_ID_NULL =
        'A required Parent User ID could be found';
    public static Final String USER_MISSING_ID =
        'The current user is missing a Role ID and cannot access forecasts. '
        + 'Please add a User Role to the current user profile to use '
        + 'Specialist Forecasting.';
    public static Final String NO_ACCESS =
        'Your user account doesn\'t have access to view Specialist Forecasts.'
        + ' Please check your user profile or contact an administrator.';
    public static Final String QUOTA_ERROR =
        'An error occurred while trying to access Specialist Forecast ' +
        'Quotas.';
    public static Final String LI_ERROR =
        'An error occurred while trying to access Specialist Forecast ' +
        'line items.';
    public static final String NO_PERMS =
        'You do not have permission to view reports for this user.';
    public static final String REFRESH_SUCCESS =
        'Data refresh request submitted successfully.';
    public static final String OVER_ERROR =
        'Failed to update the Forecast Override ';
    public static final String OVERRIDE_SUCCESS =
        'Override Amount updated successfully';
    public static final String REP_SUCCESS =
        'Report Submitted successfully';

    // Sentence Fragments, meant to have something appended when used
    public static final String NO_DATA_NAME = 'No data found by name: ';
    public static Final String NO_DATA_CAT =
        'Unable to get forecast configurations for ';
    public static final String CANT_FIND_USER =
        'Unable able for find a user for UserID: ';
    public static final String FOR_ID = ' for ID: ';
    public static final String UNEXPECTED = 'Unexpected Report Error: ';

}
