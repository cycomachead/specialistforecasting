/**
 * Test class for MonthUtil class in SpecialistForecasting Package
 *
 * @author Michael Ball
 * @since
 */
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


    /**    METHOD List **/
    /*
    static String abbrev(String mon)
    static LIST getAll()
    static Integer getFiscalMonthNum(Integer month, Integer start)
    static String getMonthName(Integer i)
    Integer getMonthNumber(String mon)
    static LIST rotatedFrom(Integer start)
    */

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
        System.assertEquals(JAN, MonthUtil.getMonthName(1));
        System.assertEquals(JUN, MonthUtil.getMonthName(6));
        System.assertEquals(DEC, MonthUtil.getMonthName(12));
        System.assertEquals(OCT, MonthUtil.getMonthName(10));
        // Expect Error Results
        System.assertEquals(-1, MonthUtil.getMonthNumber('WOOT!'));
        System.assertEquals(-1, MonthUtil.getMonthNumber(':D'));
        System.assertEquals('', MonthUtil.getMonthName(1337));
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
    }

    /** Positive Tests for all 12 cases of fiscal month rotation */
    public static testMethod void testRotation() {
        System.AssertEquals(MonthUtil.rotatedFrom(1), ROT_JAN);
        System.AssertEquals(MonthUtil.rotatedFrom(2), ROT_FEB);
        System.AssertEquals(MonthUtil.rotatedFrom(3), ROT_MAR);
        System.AssertEquals(MonthUtil.rotatedFrom(4), ROT_APR);
        System.AssertEquals(MonthUtil.rotatedFrom(5), ROT_MAY);
        System.AssertEquals(MonthUtil.rotatedFrom(6), ROT_JUN);
        System.AssertEquals(MonthUtil.rotatedFrom(7), ROT_JUL);
        System.AssertEquals(MonthUtil.rotatedFrom(8), ROT_AUG);
        System.AssertEquals(MonthUtil.rotatedFrom(9), ROT_SEP);
        System.AssertEquals(MonthUtil.rotatedFrom(10), ROT_OCT);
        System.AssertEquals(MonthUtil.rotatedFrom(11), ROT_NOV);
        System.AssertEquals(MonthUtil.rotatedFrom(12), ROT_DEC);
        // tests for invalid months should give a predicatable result
        // see the function in getFiscalMonthNum()

    }

    /** Postive/Neg Tests of expected values for adjusted fiscal months */
    public static testMethod void testAjdFiscalMonths() {
        // Jan, FY starting in Jan is Jan
        System.assertEquals(MonthUtil.getFiscalMonthNum(1, 1), 1);
        //
        System.assertEquals(MonthUtil.getFiscalMonthNum(5, 1), 5);
        //
        System.assertEquals(MonthUtil.getFiscalMonthNum(1, 2), 12);
        //
        System.assertEquals(MonthUtil.getFiscalMonthNum(10, 3), 8);
        //
        System.assertEquals(MonthUtil.getFiscalMonthNum(11, 10), 2);
        //
        System.assertEquals(MonthUtil.getFiscalMonthNum(7, 9), 11);
        //
        System.assertEquals(MonthUtil.getFiscalMonthNum(6, 4), 3);
        // NEGATIVE TESTS (Testing +/- error cases)
        //
        System.assertNotEquals(MonthUtil.getFiscalMonthNum(1, 2), 1);
        //
        System.assertNotEquals(MonthUtil.getFiscalMonthNum(7, 5), 4);
        //
        System.assertNotEquals(MonthUtil.getFiscalMonthNum(12, 12), 0);
    }


}

