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
 * Test class for MonthUtil class in SpecialistForecasting Package
 *
 * @author Michael Ball
 */

@isTest
public with sharing class SpecForecastMonthUtilTest {

    /** Static Variables Setup */
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
    public static Final String[] ALL = new String[]
        { JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC };
    /** String[]s for testing fiscal month rotations. */
    public static Final String[] ROT_JAN = ALL;
    public static Final String[] ROT_FEB = new String[]
        { FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC, JAN };
    public static Final String[] ROT_MAR = new String[]
        { MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC, JAN, FEB };
    public static Final String[] ROT_APR = new String[]
        { APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC, JAN, FEB, MAR };
    public static Final String[] ROT_MAY = new String[]
        { MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC, JAN, FEB, MAR, APR};
    public static Final String[] ROT_JUN = new String[]
        { JUN, JUL, AUG, SEP, OCT, NOV, DEC, JAN, FEB, MAR, APR, MAY };
    public static Final String[] ROT_JUL = new String[]
        { JUL, AUG, SEP, OCT, NOV, DEC, JAN, FEB, MAR, APR, MAY, JUN };
    public static Final String[] ROT_AUG = new String[]
        { AUG, SEP, OCT, NOV, DEC, JAN, FEB, MAR, APR, MAY, JUN, JUL };
    public static Final String[] ROT_SEP = new String[]
        { SEP, OCT, NOV, DEC, JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG };
    public static Final String[] ROT_OCT = new String[]
        { OCT, NOV, DEC, JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP };
    public static Final String[] ROT_NOV = new String[]
        { NOV, DEC, JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT };
    public static Final String[] ROT_DEC = new String[]
        { DEC, JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV };

    /**
     *  Test simple getter methods that returns ALL.
     *  Test Names and Numbers methods for moths (not super comprehensive)
     *  These are all basic utility methods w/ simple implementations
     */
    public static testMethod void sanityCheck() {
        System.assertEquals(ALL, MonthUtil.getAll());
        System.assertEquals(1, MonthUtil.getMonthNumber(JAN));
        System.assertEquals(5, MonthUtil.getMonthNumber(MAY));
        System.assertEquals(10, MonthUtil.getMonthNumber(OCT));
        System.assertEquals(2, MonthUtil.getMonthNumber(FEB));
        System.assertEquals(JAN, MonthUtil.getMonth(1));
        System.assertEquals(JUN, MonthUtil.getMonth(6));
        System.assertEquals(DEC, MonthUtil.getMonth(12));
        System.assertEquals(OCT, MonthUtil.getMonth(10));
        System.assert(MonthUtil.getOrderedFiscalMonths().size() == 12);
        // Expect Error Results and weird cases.
        System.assertEquals(-1, MonthUtil.getMonthNumber('WOOT!'));
        System.assertEquals(-1, MonthUtil.getMonthNumber(':D'));
        System.assertEquals(MAY, MonthUtil.getMonth(1337));
        System.assertEquals('', MonthUtil.getMonth(0));
        System.assertEquals('', MonthUtil.getMonth(-1));
    }

    /** Test the abbrev method, both positive and negative tests. */
    public static testMethod void testAbbrev() {
        // positive tests
        System.assert('JAN'.equals(MonthUtil.abbrev(JAN)));
        System.assert('FEB'.equals(MonthUtil.abbrev(FEB)));
        System.assert('MAR'.equals(MonthUtil.abbrev(MAR)));
        System.assert('APR'.equals(MonthUtil.abbrev(APR)));
        System.assert('MAY'.equals(MonthUtil.abbrev(MAY)));
        System.assert('JUN'.equals(MonthUtil.abbrev(JUN)));
        System.assert('JUL'.equals(MonthUtil.abbrev(JUL)));
        System.assert('AUG'.equals(MonthUtil.abbrev(AUG)));
        System.assert('SEP'.equals(MonthUtil.abbrev(SEP)));
        System.assert('OCT'.equals(MonthUtil.abbrev(OCT)));
        System.assert('NOV'.equals(MonthUtil.abbrev(NOV)));
        System.assert('DEC'.equals(MonthUtil.abbrev(DEC)));
        // negative tests, checking case sensitivity
        System.assert(!'may'.equals(MonthUtil.abbrev(MAY)));
        System.assert(!'jun'.equals(MonthUtil.abbrev(JUN)));
        System.assert(!'jul'.equals(MonthUtil.abbrev(JUL)));
        // check error handling
        System.assert('Ja'.equals(MonthUtil.abbrev('Ja')));
    }

    /** Positive Tests for all 12 cases of fiscal month rotation */
    public static testMethod void testRotation() {
        System.assertEquals(MonthUtil.rotatedFrom(1), ROT_JAN);
        System.assertEquals(MonthUtil.rotatedFrom(2), ROT_FEB);
        System.assertEquals(MonthUtil.rotatedFrom(3), ROT_MAR);
        System.assertEquals(MonthUtil.rotatedFrom(4), ROT_APR);
        System.assertEquals(MonthUtil.rotatedFrom(5), ROT_MAY);
        System.assertEquals(MonthUtil.rotatedFrom(6), ROT_JUN);
        System.assertEquals(MonthUtil.rotatedFrom(7), ROT_JUL);
        System.assertEquals(MonthUtil.rotatedFrom(8), ROT_AUG);
        System.assertEquals(MonthUtil.rotatedFrom(9), ROT_SEP);
        System.assertEquals(MonthUtil.rotatedFrom(10), ROT_OCT);
        System.assertEquals(MonthUtil.rotatedFrom(11), ROT_NOV);
        System.assertEquals(MonthUtil.rotatedFrom(12), ROT_DEC);
    }

    /** Postive/Neg Tests of expected values for adjusted fiscal months */
    public static testMethod void testFiscalMonthNum() {
        // Thhis function isn't dependent on org settings.
        // Jan, FY starting in Jan is Jan
        System.assertEquals(MonthUtil.getFiscalMonthNum(1, 1), 1);
        System.assertEquals(MonthUtil.getFiscalMonthNum(5, 1), 5);
        System.assertEquals(MonthUtil.getFiscalMonthNum(1, 2), 12);
        System.assertEquals(MonthUtil.getFiscalMonthNum(10, 3), 8);
        System.assertEquals(MonthUtil.getFiscalMonthNum(11, 10), 2);
        System.assertEquals(MonthUtil.getFiscalMonthNum(7, 9), 11);
        System.assertEquals(MonthUtil.getFiscalMonthNum(6, 4), 3);
        // Few tests just to check off by one cases.
        System.assertNotEquals(MonthUtil.getFiscalMonthNum(1, 2), 1);
        System.assertNotEquals(MonthUtil.getFiscalMonthNum(7, 5), 4);
        System.assertNotEquals(MonthUtil.getFiscalMonthNum(12, 12), 0);
    }

    /** Test to make sure months are in the correct quarters */
    public static testMethod void testQtrNumber() {
        System.assertEquals(MonthUtil.getQtrOfMonth(1), 1);
        System.assertEquals(MonthUtil.getQtrOfMonth(3), 1);
        System.assertEquals(MonthUtil.getQtrOfMonth(6), 2);
        System.assertEquals(MonthUtil.getQtrOfMonth(9), 3);
        System.assertEquals(MonthUtil.getQtrOfMonth(11), 4);
    }

    /** Test the ability to get months in a quarter.*/
    public static testMethod void testMonthsInQtr() {
        String[] m1 = MonthUtil.getMonthsInQtr(1);
        // Don't error on weird values.
        String[] m2 = MonthUtil.getMonthsInQtr(42);
        // Not a robust test since months in list will vary.
        System.assert(m1.size() == 3);
        System.assert(m2.size() == 3);
    }

    /** Test to get the correct fiscal year */
    public static testMethod void testFY() {
        // setup basic properites for testing to mirror SFDC
        // FY starts in Feb and is based on the ending month year
        MonthUtil.reset(2, false);
        System.assertEquals(MonthUtil.getFY(2015, 7), 16);
        System.assertEquals(MonthUtil.getFY(1896, 3), 97);
        System.assertEquals(MonthUtil.getFY(1896, 2), 97);
        System.assertEquals(MonthUtil.getFY(2000, 1), 00);
        System.assertEquals(MonthUtil.getFY(1776, 2), 77);
        // FY starts in July and is based on starting month year
        MonthUtil.reset(7, true);
        System.assertEquals(MonthUtil.getFY(2015, 7), 15);
        System.assertEquals(MonthUtil.getFY(1896, 3), 95);
        System.assertEquals(MonthUtil.getFY(1896, 6), 95);
        System.assertEquals(MonthUtil.getFY(2000, 1), 99);
        System.assertEquals(MonthUtil.getFY(1776, 2), 75);
        try {
            MonthUtil.reset(27, false);
            System.assert(false); // error expected
        } catch (Exception ex) {
            System.assert(true);
        }
        MonthUtil.resetFromOrg(); // precaution for future tests.
    }

    /** The the normalize method for 2 digit years. */
    public static testMethod void testNorm() {
        System.assertEquals(MonthUtil.normalizeYr(2000), 0);
        System.assertEquals(MonthUtil.normalizeYr(0000), 0);
        System.assertEquals(MonthUtil.normalizeYr(1999), 99);
        System.assertEquals(MonthUtil.normalizeYr(2013), 13);
        System.assertEquals(MonthUtil.normalizeYr(-001), 99);
    }

    /** Make sure the proper year strings are displayed. */
    public static testMethod void testNromString() {
        System.assertEquals(MonthUtil.normalizeYrToStr(2000), '00');
        System.assertEquals(MonthUtil.normalizeYrToStr(1999), '99');
        System.assertEquals(MonthUtil.normalizeYrToStr(0000), '00');
        System.assertEquals(MonthUtil.normalizeYrToStr(2013), '13');
        System.assertEquals(MonthUtil.normalizeYrToStr(-001), '99');
    }

    /** Test the ability to reset Month Settings */
    public static testMethod void testReset() {
        try {
            MonthUtil.reset(31, false);
            System.assert(false, 'Invalid months should cause exceptions.');
        } catch (SpecialistForecastException ex) {
            System.assert(true);
        }
        // Run a couple times to make sure there aren't errors.
        MonthUtil.reset(3, true);
        MonthUtil.reset(2, false); // SFDC fiscal settings.
    }
}

