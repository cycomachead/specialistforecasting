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
 *
 * @author Michael Ball
 * @since
 */

public with sharing class MonthUtil {
    
    /** Number of months in a year. */
    public static Final Integer NUM = 12;

    /** US/English Months of the Year */
    public static Final String JAN = 'January';
    public static Final String FEB = 'February';
    public static Final String MAR = 'March';
    public static Final String APR = 'April';
    public static Final String MAY = 'May';
    public static Final String JUN = 'June';
    public static Final String JUL = 'July';
    public static Final String AUG = 'August';
    public static Final String SEP = 'September';
    public static Final String OCT = 'October';
    public static Final String NOV = 'November';
    public static Final String DEC = 'December';

    /** A list of all months. */
    public static Final String[] ALL = new String[] {JAN, FEB, MAR, APR,
        MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC};
    /** List of the months with FY_START as first month. */
    public static String[] orderedFiscalMonths = new String[12];
    
    /** Org query for fiscal year calculations */
    public static Integer FY_START = [SELECT FiscalYearStartMonth 
        FROM Organization].FiscalYearStartMonth;
    /** True IFF Fiscal Year is based on start month. */
    public static Boolean FY_IS_START_MON = [SELECT UsesStartDateAsFiscalYearName
         FROM Organization].UsesStartDateAsFiscalYearName;
    
    /**************************************************************************/
    /*                          BASE MONTH UTILITIES                          */

    /**
     *  Gives an English name of a month, based on any integer,uses mod 12.
     *  @param i is an integer.
     *  @return An American/English month name.
     */
    public static String getMonthName(Integer i) {
        i = (Math.mod(i, NUM) == 0 ? 12 : Math.mod(i, NUM));
        return ALL[i - 1];
    }

    /**
     *  NOTE: Uses only the first 3 characters to find a match.
     *  So, "JanZZZZZ" would still return 1. 
     *  @param mon is a month name or abbreviation
     *  @return [1, 121] if a valid moth is found, else -1
     */
    public static Integer getMonthNumber(String mon) {
        try {
            mon = mon.substring(0, 3);
        } catch (Exception ex) {
            return -1;
        }

        if (mon == JAN.substring(0, 3)) { return  1; }
        if (mon == FEB.substring(0, 3)) { return  2; }
        if (mon == MAR.substring(0, 3)) { return  3; }
        if (mon == APR.substring(0, 3)) { return  4; }
        if (mon == MAY.substring(0, 3)) { return  5; }
        if (mon == JUN.substring(0, 3)) { return  6; }
        if (mon == JUL.substring(0, 3)) { return  7; }
        if (mon == AUG.substring(0, 3)) { return  8; }
        if (mon == SEP.substring(0, 3)) { return  9; }
        if (mon == OCT.substring(0, 3)) { return 10; }
        if (mon == NOV.substring(0, 3)) { return 11; }
        if (mon == DEC.substring(0, 3)) { return 12; }
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
     *  @param fQuarter is a fiscal quarter string, 'Q#'.
     *  @return a list of the months in the specified quarter.
     */
    public static String[] getMonthsInQtr(Integer qtr) {
        if (orderedFiscalMonths[0] == null) {
            orderedFiscalMonths = rotatedFrom(FY_START);
        } // make sure our list has valid data

        String[] monthsInQrtr = new String[3];        
        for (Integer month = (qtr * 3) - 3; month < (qtr * 3); month += 1) {
            monthsInQrtr.add(orderedFiscalMonths[month]);
        }
        return monthsInQrtr;
    }

    /**
     *  @return Months of the fiscal year, in order Q1, month 1 ... Q4, month 3.
     */
    public static String[] getOrderedFiscalMonths() {
        if (orderedFiscalMonths[0] == null) {
            orderedFiscalMonths = rotatedFrom(FY_START);
        }
        return orderedFiscalMonths;
    }
    
    /**************************************************************************/
    /*                        INTEGER UTILITIES                               */

    /**
     *  TODO: Add Test Method
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
     *  TODO: Add test method
     *  Given a month, return the quarter it falls into.
     *  @param FISCALMON is the fiscal month number expcted to be in [1, 12]
     *  @return an integer in [1, 4],
     */
    public static Integer getQtrFromMonth(Integer fiscalMon) {
        return (fiscalMon + 2) / 3;
    }

    /**
     *  TODO: Add Test
     *  Given a month and a year, it will determine the correct FY of that month.
     *  EXAMPLE: For SFDC, June 2013 is in FY14 therefore:
     *  getFYFromYearAndMo(2013, 6) returns 14 
     *  [getFYFromYearAndMo(13, 6) also works]
     *  NOTE: Settings are determined based on the current Org.
     *  @param year is an Integer
     *  @param month is an Integer expected to be in [1, 12]
     *  @return a two-digit (Integer) year.
     */
    public static Integer getFYFromYrAndMo(Integer year, Integer mon) {
        Integer adjust;
        if (FY_IS_START_MON) {
            adjust = (mon >= FY_START) ? 0 : -1; 
        } else {
            adjust = (mon < FY_START) ? 0 : 1; 
        }
        return Math.mod(year + adjust, 100);
    }

    /**
     *  Given a (non-adjusted) month it will return the fiscal quarter it is in
     *  @param MONTH is an integer in [1, 12] WHERE 1 represents Janurary
     *  @return The fiscal quarter of the given month
     */
    public static Integer getFiscalQtrFromMon(Integer mon) {
        return getQtrFromMonth(getFiscalMonthNum(mon, FY_START));
    }
    
    /**
     * @return current fiscal quarter as a string, 'Q#', based on system time.
     */
    public static Integer getCurrentFiscalQtr() {
        return getFiscalQtrFromMon(System.today().month());
    }

    /**
     * @return The current fiscal year as a two-digit integer.
     */
    public static Integer getCurrentFY() {
        Date now = System.today();
        return getFYFromYrAndMo(now.year(), now.month());
    }
}

