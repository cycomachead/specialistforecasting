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
 *  A class for dealing with the months of the year and relations to fiscal years.
 *  This class keeps track of the Fiscal Year settings in the Org Company object
 *  and makes working with adjusted dates much easier, especially for a project
 *  like forcasting.
 *  @author Michael Ball
 */

public with sharing class MonthUtil {

    /** Number of months in a year. */
    private static Final Integer NUM = 12;
    /** Months of the year. Use getName(Integer) to access each month. */
    private static Final String[] ALL = new String[]
        { 'January', 'February', 'March', 'April', 'May', 'June', 'July',
        'August', 'September', 'October', 'November', 'December' };

    /** The month the org fiscal year starts on. Grabbed from org in OrgData. */
    private static Integer FY_START;
    /** TRUE IFF FY is year of start month. Grabbed from org in OrgData. */
    private static Boolean FY_IS_START_MON;
    /** List of the months of year with FY_START as first month. */
    private static String[] ORDERED_MONTHS;

    /**
     *  Inner class so that org queries pass even if a user does not have read
     *  access to the org data. "without sharing" is required if the user
     *  doesn't have permission to view org settings, but the class is setup to
     *  keep access restricted from changing org settings.
     */
    private without sharing class OrgData {
        /** Org query for fiscal year calculations */
        private Integer ORG_FY_MON = [SELECT FiscalYearStartMonth
            FROM Organization limit 1].FiscalYearStartMonth;
        /** True IFF Fiscal Year is based on start month. */
        private Boolean FY_IS_START = [SELECT UsesStartDateAsFiscalYearName
            FROM Organization limit 1].UsesStartDateAsFiscalYearName;

        /** Methods for easy Access to the Above from MonthUtil. */
        public Integer getFYStart() { return ORG_FY_MON; }
        public Boolean getFYIsStart() { return FY_IS_START; }
    }

    /**
     *  Work-around since inner classes cannot be static and static variables
     *  must be declared at the begging of a class.
     *  Static -- TODO
     */
    private static OrgData setup = new OrgData();
    static { resetFromOrg(); }

    /**
     *  Used to set the base values from the org settings, used in inital setup
     */
    public static void resetFromOrg() {
        /** The number of the first month of the org's fiscal year. */
        FY_START = setup.getFYStart();
        /** True IFF Fiscal Year is based on start month. */
        FY_IS_START_MON = setup.getFYIsStart();
        /** List of the months with FY_START as first month. */
        ORDERED_MONTHS = rotatedFrom(FY_START);
    }

    /**
     *  Use this method to override the base FY properties.
     *  Meant only to be used for testing, though may have other uses.
     *  @param mon is the start month of a FY, in range [1, 12]
     *  @param isStart is true IFF the year of FY is based on year of the start month
     *  @throws SpecialistForecastException
     */
    public static void reset(Integer mon, Boolean isStart) {
        if (mon < 1 || mon > 12) {
            throw new SpecialistForecastException('Month number ' + mon +
                'cannot be used as the start of a fiscal year.');
        }
        FY_START = mon;
        FY_IS_START_MON = isStart;
        ORDERED_MONTHS = rotatedFrom(FY_START);
    }
    /**************************************************************************/
    /*                          BASE MONTH UTILITIES                          */
    /**
     *  Gives an English name of a month, based on any integer,uses mod 12.
     *  @param i is an integer.
     *  @return An American/English month name.
     */
    public static String getMonth(Integer i) {
        if (i > 0) {
            i = (Math.mod(i, NUM) == 0 ? 12 : Math.mod(i, NUM));
            return ALL[i - 1];
        }
        return '';
    }

    /**
     *  NOTE: Uses only the first 3 characters to find a match.
     *  @param mon is a month name or abbreviation
     *  @return [1, 12] if a valid moth is found, else -1
     */
    public static Integer getMonthNumber(String mon) {
        try {
            mon = mon.substring(0, 3);
        } catch (Exception ex) {
            return -1;
        }

        for (Integer i = 0; i < 12; i += 1) {
            if (mon == ALL[i].substring(0, 3)) { return  i + 1; }
        }
        return -1;
    }

    /**
     *  @return the list of the months of the year, as a String[]
     */
    public static String[] getAll() {
        return ALL;
    }

    /**
     *  Gives a simple abbreviation for a month, could really be any word.
     *  @param mon is a string, at least 3 characters expected
     *  @return an all caps string
     */
    public static String abbrev(String mon) {
        try {
            return mon.substring(0, 3).toUpperCase();
        } catch (Exception ex) {
            return mon;
        }
    }

    /**************************************************************************/
    /*                  FISCAL YEAR/MONTH UTILITIES                           */
    /**
     *  Gives the months of the year so the FY start month is first in the list.
     *  @param start is the number of the first month of the FY
     *  @return String[] of month names
     */
    public static String[] rotatedFrom(Integer start) {
        String[] rotated = new String[12];

        Integer count = 1;
        for (String mon : ALL) {
            rotated[getFiscalMonthNum(count, start) - 1] = mon;
            count += 1;
        }
        return rotated;
    }

    /**
     *  @param qtr is an int in [1, 4].
     *  @return a list of the months in the specified quarter.
     */
    public static String[] getMonthsInQtr(Integer qtr) {
        if (qtr < 1 || qtr > 4) {
            qtr = getCurrentFiscalQtr();
        }

        String[] monthsInQtr = new String[] {};
        for (Integer month = (qtr * 3) - 3; month < (qtr * 3); month += 1) {
            monthsInQtr.add(ORDERED_MONTHS[month]);
        }
        return monthsInQtr;
    }

    /**
     *  @return Months of the fiscal year, in order Q1, month 1 ... Q4, month 3.
     */
    public static String[] getOrderedFiscalMonths() {
        if (ORDERED_MONTHS == null) {
            ORDERED_MONTHS = rotatedFrom(FY_START);
        }
        return ORDERED_MONTHS;
    }

    /**************************************************************************/
    /*                        INTEGER UTILITIES                               */
    /**
     *  Given a month, and a FY starting month, determin the fiscal month
     *  If fiscal year starts in March, March is month 1, Feb is 12.
     *
     *  @param MONTH is a normal month integer in [1, 12]
     *  @param START is a month integer that is the start of a fiscal year.
     *  @return the adjusted m`onth FROM the start of the fiscal year.
     */
    public static Integer getFiscalMonthNum(Integer month, Integer start) {
        return (Math.mod((month + (NUM - start)), NUM) + 1);
    }

    /**
     *  Given a month, return the quarter it falls into.
     *  @param fiscalMon is the fiscal month number expcted to be in [1, 12]
     *  @return an integer in [1, 4],
     */
    public static Integer getQtrOfMonth(Integer fiscalMon) {
        return (fiscalMon + 2) / 3;
    }

    /**
     *  Given a month and a year, it will determine the correct FY of that month.
     *  EXAMPLE: For SFDC, June 2013 is in FY14 therefore:
     *  getFYFromYearAndMo(2013, 6) returns 14
     *  [getFYFromYearAndMo(13, 6) also works]
     *  NOTE: Settings are determined based on the current Org.
     *  @param year is an Integer
     *  @param month is an Integer expected to be in [1, 12]
     *  @return a two-digit (Integer) year.
     */
    public static Integer getFY(Integer year, Integer mon) {
        Integer adjust;
        if (FY_IS_START_MON) {
            adjust = (mon >= FY_START) ? 0 : -1;
        } else {
            adjust = (mon < FY_START) ? 0 : 1;
        }
        return normalizeYr(year + adjust);
    }

    /**
     *  @return The current fiscal year as a two-digit integer.
     */
    public static Integer getCurrentFY() {
        return getFYFromDate(System.today());
    }

    /**
     *  @return The fiscal year from the INPDATE provided.
     */
    public static Integer getFYFromDate(Date inpDate) {
        return getFY(inpDate.year(), inpDate.month());
    }

    /**
     *  Given a (non-adjusted) month it will return the fiscal quarter it is in
     *  @param MONTH is an integer in [1, 12] where 1 represents Janurary
     *  @return The fiscal quarter of the given month, in [1, 4]
     */
    public static Integer getFiscalQtr(Integer mon) {
        return getQtrOfMonth(getFiscalMonthNum(mon, FY_START));
    }

    /**
     *  @return current fiscal quarter as a string, 'Q#', based on system time.
     */
    public static Integer getCurrentFiscalQtr() {
        return getFiscalQtr(System.today().month());
    }

    /** Normalizes YEAR so that it returns an int in [0, 99] */
    public static Integer normalizeYr(Integer year) {
        if (year < 0) { //make it so mod wraps around, always > 0
            return 100 + Math.mod(year, 100); // + since year%100 is < 0.
        } else {
            return Math.mod(year, 100);
        }
    }

    /**
     *  Normalizes YEAR so that it returns a String in ['00', '99'].
     */
    public static String normalizeYrToStr(Integer year) {
        year = normalizeYr(year);
        return (year < 10 ? '0' : '') + year;
    }
}

