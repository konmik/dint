package dint;

import org.intelligentsia.hirondelle.date4j.DateTime;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import static dint.Dint.*;
import static java.lang.String.format;
import static org.intelligentsia.hirondelle.date4j.DateTime.DayOverflow.LastDay;
import static org.junit.Assert.assertEquals;

public class DintTest {

    private static final int RUNS = 100000;
    private static final int SEED = 1;

    @Test
    public void random_data_test() {
        Random r = new Random(SEED);
        for (int iteration = 0; iteration < RUNS; iteration++) {
            testAddDays(r);
            testAddMonths(r);
            testAddYears(r);
            testDiff(r);
            testFirstDayOfMonth(r);
            testLastDayOfMonth(r);
            testDaysInAMonth(r);
            testIsLeapYear(r);
        }
    }

    private void testIsLeapYear(Random r) {
        DateTime d1 = randomDateTime(r);
        int dint1 = dateTimeToDint(d1);
        assertEquals("" + dint1, d1.isLeapYear(), isLeapYear(year(dint1)));
    }

    private void testDaysInAMonth(Random r) {
        DateTime d1 = randomDateTime(r);
        int dint1 = dateTimeToDint(d1);
        assertEquals(d1.getNumDaysInMonth(), daysInAMonth(year(dint1), month(dint1)));
    }

    private void testLastDayOfMonth(Random r) {
        DateTime d1 = randomDateTime(r);
        assertEquals(dateTimeToDint(d1.getEndOfMonth()), lastDayOfMonth(dateTimeToDint(d1)));
    }

    private void testFirstDayOfMonth(Random r) {
        DateTime d1 = randomDateTime(r);
        assertEquals(dateTimeToDint(d1.getStartOfMonth()), firstDayOfMonth(dateTimeToDint(d1)));
    }

    private void testDiff(Random r) {
        DateTime d1 = randomDateTime(r);
        DateTime d2 = randomDateTime(r);
        int dint1 = dateTimeToDint(d1);
        int dint2 = dateTimeToDint(d2);
        assertEquals(d2.numDaysFrom(d1), diff(dint1, dint2));
    }

    private void testAddYears(Random r) {
        DateTime d1 = randomDateTime(r);
        int dint1 = dateTimeToDint(d1);

        int yearsAdd = r.nextInt(4999);
        DateTime added = d1.plus(yearsAdd, 0, 0, 0, 0, 0, LastDay);
        int dint2 = dateTimeToDint(added);
        assertEquals(dint2, Dint.addYears(dint1, yearsAdd));

        boolean isEndOfMonth = isLastDayOfMonth(d1);
        DateTime extendedExpected = isEndOfMonth ? added.getEndOfMonth() : added;
        int dint3 = dateTimeToDint(extendedExpected);
        int result = Dint.addYearsExtend(dint1, yearsAdd);
        assertEquals(format("from: %d, add: %d, result: %d, expected: %d", dint1, yearsAdd, result, dint3), dint3, result);
    }

    private void testAddMonths(Random r) {
        DateTime d1 = randomDateTime(r);
        int dint1 = dateTimeToDint(d1);

        int monthsAdd = r.nextInt(10000);
        DateTime added = d1.plus(0, monthsAdd, 0, 0, 0, 0, LastDay);

        int dint2 = dateTimeToDint(added);
        assertEquals(dint2, Dint.addMonths(dint1, monthsAdd));

        boolean isEndOfMonth = isLastDayOfMonth(d1);
        DateTime extendedExpected = isEndOfMonth ? added.getEndOfMonth() : added;
        int dint3 = dateTimeToDint(extendedExpected);
        int result = Dint.addMonthsExtend(dint1, monthsAdd);
        assertEquals(format("from: %d, add: %d, result: %d, expected: %d", dint1, monthsAdd, result, dint3), dint3, result);
    }

    private void testAddDays(Random r) {
        DateTime d1 = randomDateTime(r);
        int dint1 = dateTimeToDint(d1);

        int daysToAdd = r.nextInt(10000);
        DateTime d2 = d1.plusDays(daysToAdd);
        int dint2 = dateTimeToDint(d2);
        assertEquals(dint2, Dint.addDays(dint1, daysToAdd));
    }

    @Test
    public void test_create_from_ymd() throws Exception {
        assertEquals(20140912, create(2014, 9, 12));
        assertEquals(0, create(0, 0, 0));
        assertEquals(11332, create(1, 13, 32));
    }

    @Test
    public void test_create_from_Calendar() throws Exception {
        assertEquals(20140912, create(create20140912Calendar()));
    }

    @Test
    public void test_create_from_Date() throws Exception {
        assertEquals(20140912, create(new Date(2014 - 1900, 8, 12)));
    }

    @Test
    public void test_compose() throws Exception {
        assertEquals(20140912, compose(2014, 9, 12));
        assertEquals(20141212, compose(2014, 12, 12));
        assertEquals(20150112, compose(2014, 13, 12));
        assertEquals(20140912, compose(2013, 9, 12 + 365));
        assertEquals(20140912, compose(2015, 9, 12 - 365));
        assertEquals(20150912, compose(2016, 9, 12 - 366));
        assertEquals(20141201, compose(2014, 12, 1));
        assertEquals(20151102, compose(2015, 11, 2));
    }

    @Test
    public void test_year() throws Exception {
        assertEquals(2014, Dint.year(20140912));
    }

    @Test
    public void test_month() throws Exception {
        assertEquals(9, Dint.month(20140912));
    }

    @Test
    public void test_day() throws Exception {
        assertEquals(12, Dint.day(20140912));
    }

    @Test
    public void test_toCalendar() throws Exception {
        assertEquals(create20140912Calendar(), Dint.toCalendar(20140912));
    }

    @Test
    public void test_toDate() throws Exception {
        assertEquals(create20140912Calendar().getTime(), Dint.toDate(20140912));
    }

    @Test
    public void test_today() throws Exception {
        assertEquals(createTodayCalendar(), Dint.toCalendar(Dint.today()));
    }

    @Test
    public void test_firstDayOfMonth() throws Exception {
        assertEquals(20140901, firstDayOfMonth(20140912));
    }

    @Test
    public void test_lastDayOfMonth() throws Exception {
        assertEquals(20140930, lastDayOfMonth(20140912));
    }

    @Test
    public void test_daysInAMonth() throws Exception {
        assertEquals(31, daysInAMonth(2014, 1));
        assertEquals(28, daysInAMonth(2014, 2));
        assertEquals(31, daysInAMonth(2014, 3));
        assertEquals(30, daysInAMonth(2014, 4));
        assertEquals(31, daysInAMonth(2014, 5));
        assertEquals(30, daysInAMonth(2014, 6));
        assertEquals(31, daysInAMonth(2014, 7));
        assertEquals(31, daysInAMonth(2014, 8));
        assertEquals(30, daysInAMonth(2014, 9));
        assertEquals(31, daysInAMonth(2014, 10));
        assertEquals(30, daysInAMonth(2014, 11));
        assertEquals(31, daysInAMonth(2014, 12));
        assertEquals(29, daysInAMonth(2016, 2));
    }

    @Test
    public void test_isLeapYear() throws Exception {
        assertEquals(false, isLeapYear(2014));
        assertEquals(false, isLeapYear(2015));
        assertEquals(true, isLeapYear(2016));
        assertEquals(false, isLeapYear(2017));
    }

    @Test
    public void test_diff() throws Exception {
        assertEquals(365, diff(20140912, 20130912));
        assertEquals(365, diff(20150912, 20140912));
        assertEquals(366, diff(20160912, 20150912));

        assertEquals(1, diff(20140912, 20140911));
        assertEquals(-1, diff(20140912, 20140913));

        assertEquals(31, diff(20140912, 20140812));
    }

    @Test
    public void test_addYears() throws Exception {
        assertEquals(20140912, addYears(20140912, 0));
        assertEquals(20140912, addYears(20130912, 1));
        assertEquals(20140912, addYears(20150912, -1));
        assertEquals(20140912, addYears(10140912, 1000));

        assertEquals(20160228, addYears(20140228, 2));
    }

    @Test
    public void test_addYearsExtend() throws Exception {
        assertEquals(20160229, addYearsExtend(20140228, 2));
    }

    @Test
    public void test_addMonths() throws Exception {
        assertEquals(20140912, addMonths(20140912, 0));
        assertEquals(20140912, addMonths(20140812, 1));
        assertEquals(20140912, addMonths(20141012, -1));
        assertEquals(20140912, addMonths(20040912, 120));

        assertEquals(20140912, addMonths(20130912, 12));
        assertEquals(20140912, addMonths(20150912, -12));
        assertEquals(20140912, addMonths(20240912, -120));

        assertEquals(20140930, addMonths(20140831, 1));
    }

    @Test
    public void test_addMonthsExtend() throws Exception {
        assertEquals(20140912, addMonthsExtend(20140912, 0));
        assertEquals(20140912, addMonthsExtend(20140812, 1));
        assertEquals(20140912, addMonthsExtend(20140812, 1));
        assertEquals(20140912, addMonthsExtend(20140812, 1));
        assertEquals(20140912, addMonthsExtend(20140812, 1));
        assertEquals(20141031, addMonthsExtend(20140930, 1));

        assertEquals(20141030, addMonths(20140930, 1));
        assertEquals(20141031, addMonthsExtend(20140930, 1));
    }

    @Test
    public void test_days() throws Exception {
        assertEquals(20140912, addDays(20140911, 1));
        assertEquals(20140912, addDays(20140913, -1));
        assertEquals(20140912, addDays(20150912, -365));
        assertEquals(20140912, addDays(20141012, -30));
    }

    @Test
    public void coverage_instantiate_utility_classes() throws Exception {
        new Dint();
    }

    private Calendar create20140912Calendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, 2014);
        calendar.set(Calendar.MONTH, 8);
        calendar.set(Calendar.DAY_OF_MONTH, 12);
        return calendar;
    }

    private Calendar createTodayCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        Calendar temp = new GregorianCalendar();
        calendar.set(Calendar.YEAR, temp.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, temp.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, temp.get(Calendar.DAY_OF_MONTH));
        return calendar;
    }

    private static boolean isLastDayOfMonth(DateTime dateTime) {
        return dateTime.getDay().equals(dateTime.getNumDaysInMonth());
    }

    private static int dateTimeToDint(DateTime dateTime) {
        return Dint.create(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
    }

    private static DateTime randomDateTime(Random random) {
        return new DateTime(
            1 + random.nextInt(5000),
            1 + random.nextInt(11),
            1 + random.nextInt(28),
            0, 0, 0, 0);
    }
}
