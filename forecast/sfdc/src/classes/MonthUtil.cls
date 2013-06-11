/**
 *
 * @author Michael Ball
 * @since
 */


public with sharing class MonthUtil {

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

    /** Number of months in a year. */
    public static Final Integer NUM = 12;

    /**
     *  DESCRIPTION
     *  @param
     *  @return
     */
    public static String getMonthName(Integer i) {
        if (i > 0 && i <= 12) {
            return ALL[i - 1];
        } else {
            return '';
        }
    }

    /**
     *  DESCRIPTION
     *  @param
     *  @return
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
     *  DESCRIPTION
     *  @param
     *  @return
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
     *  DESCRIPTION
     *  @param
     *  @return
     */
    public static String abbrev(String mon) {
        return mon.substring(0, 3).toUpperCase();
    }

    /**
     *  @return the list of the months of the year, as a String[]
     */
    public static String[] getAll() {
        return ALL;
    }

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
}

