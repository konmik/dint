package dint;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.Math.min;

/**
 * Handling dates in a human-readable integer format (dint).
 * Example: 20140912 = September 12, 2014
 */
public class Dint {

    /**
     * Creates a dint using given year, month and day.
     */
    public static int create(int year, int month, int day) {
        return year * 10000 + month * 100 + day;
    }

    /**
     * Creates a dint from Calendar.
     */
    public static int create(Calendar calendar) {
        return compose(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Creates a dint from Date.
     */
    public static int create(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return create(calendar);
    }

    /**
     * Creates a dint using any number of years, months and days.
     */
    public static int compose(int year, int month, int day) {
        return addDays(addMonths(year * 10000, month), day);
    }

    /**
     * Returns a year of a given dint.
     */
    public static int year(int dint) {
        return dint / 10000;
    }

    /**
     * Returns a month of a given dint.
     */
    public static int month(int dint) {
        return dint / 100 % 100;
    }

    /**
     * Returns a day of month of in a given dint.
     */
    public static int day(int dint) {
        return dint % 100;
    }

    /**
     * Creates a Calendar instance from a given dint.
     */
    public static Calendar toCalendar(int dint) {
        return new GregorianCalendar(year(dint), month(dint) - 1, day(dint));
    }

    /**
     * Creates a Date instance from a given dint.
     */
    public static Date toDate(int dint) {
        return toCalendar(dint).getTime();
    }

    /**
     * Return the current date dint.
     */
    public static int today() {
        return create(Calendar.getInstance());
    }

    /**
     * Returns a dint at the first day of a month of a given dint.
     */
    public static int firstDayOfMonth(int dint) {
        return compose(year(dint), month(dint), 1);
    }

    /**
     * Returns a dint at the last day of a month of a given dint.
     */
    public static int lastDayOfMonth(int dint) {
        return compose(year(dint), month(dint), daysInAMonth(year(dint), month(dint)));
    }

    /**
     * Returns a number of days in a given month.
     */
    public static int daysInAMonth(int year, int month) {
        return 31 - ((month == 2) ? (3 - (isLeapYear(year) ? 1 : 0)) : ((month - 1) % 7 % 2));
    }

    /**
     * Returns if a given year is a leap year.
     */
    public static boolean isLeapYear(int year) {
        return !((year % 4 != 0) || ((year % 100 == 0) && (year % 400 != 0)));
    }

    /**
     * Returns amount of days between two given dints.
     */
    public static int diff(int dint1, int dint2) {
        return toJulianDay(dint1) - toJulianDay(dint2);
    }

    /**
     * Adds years to a dint. A negative number is allowed.
     * The resulting dint's number of days will be limited by a number of days in the resulting month.
     */
    public static int addYears(int dint, int years) {
        return composeLimit(dint, year(dint) + years, month(dint));
    }

    /**
     * Adds years to a dint. A negative number is allowed.
     * If the dint day is the last day in a month then the resulting day will be the last day in a month as well.
     */
    public static int addYearsExtend(int dint, int years) {
        return composeExtend(dint, year(dint) + years, month(dint));
    }

    /**
     * Adds months to a given dint. A negative number is allowed.
     * The resulting dint's number of days will be limited by a number of days in the resulting month.
     */
    public static int addMonths(int dint, int add) {
        int months = year(dint) * 12 + month(dint) - 1 + add;
        return composeLimit(dint, months / 12, months % 12 + 1);
    }

    /**
     * Adds months to a given dint. A negative number is allowed.
     * If the dint day is the last day in a month then the resulting day will be the last day in a month as well.
     */
    public static int addMonthsExtend(int dint, int add) {
        int months = year(dint) * 12 + month(dint) - 1 + add;
        return composeExtend(dint, months / 12, months % 12 + 1);
    }

    /**
     * Adds a given number of days to a given dint. A negative number is allowed.
     */
    public static int addDays(int dint, int days) {
        return fromJulianDay(toJulianDay(dint) + days);
    }

    private static int composeLimit(int dint, int y, int m) {
        return create(y, m, min(day(dint), daysInAMonth(y, m)));
    }

    private static int composeExtend(int dint, int y, int m) {
        return daysInAMonth(year(dint), month(dint)) == day(dint) ?
            create(y, m, daysInAMonth(y, m)) :
            composeLimit(dint, y, m);
    }

    private static int toJulianDay(int dint) {
        int year = year(dint);
        int month = month(dint);
        int day = day(dint);

        int a = (14 - month) / 12;
        int y = year + 4800 - a;
        int m = month + 12 * a - 3;

        return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
    }

    private static int fromJulianDay(int julianDay) {
        int p = julianDay + 68569;
        int q = 4 * p / 146097;
        int r = p - (146097 * q + 3) / 4;
        int s = 4000 * (r + 1) / 1461001;
        int t = r - 1461 * s / 4 + 31;
        int u = 80 * t / 2447;
        int v = u / 11;

        int Y = 100 * (q - 49) + s + v;
        int M = u + 2 - 12 * v;
        int D = t - 2447 * u / 80;

        return Y * 10000 + M * 100 + D;
    }
}
