package com.hexi;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Minutes;
import org.junit.Test;

/**
 * Created by hexi on 15/8/4.
 */
public class JodaTimeTest {

    @Test
    public void test1() {
        DateTime now = DateTime.now().plusDays(-2);
        DateTime mon = now.withDayOfWeek(DateTimeConstants.MONDAY);
        System.out.println(now);
        System.out.println(mon);
    }

    @Test
    public void test2() {
        DateTime now = DateTime.now().plusDays(-36);
        DateTime dayOfMonth = now.withDayOfMonth(1);

        System.out.println(now);
        System.out.println(dayOfMonth);
    }

    @Test
    public void test3() {
        DateTime now = DateTime.now();
        System.out.println(now);
        System.out.println(now.withSecondOfMinute(0).plusMinutes(1));
    }

    @Test
    public void test4() {
        DateTime now = DateTime.now();
        DateTime start = now.withDayOfMonth(7).withHourOfDay(7).withMinuteOfHour(0).withSecondOfMinute(0);
        DateTime end = now.withDayOfMonth(8).withHourOfDay(17).withMinuteOfHour(0).withSecondOfMinute(0);

        int minutes = Minutes.minutesBetween(start, end).getMinutes();
        int count = minutes / 60 + 1;
        System.out.println(count);
    }

    @Test
    public void test5() {
        DateTime now = DateTime.now();
        DateTime start = now.withDayOfMonth(8).withHourOfDay(18).withMinuteOfHour(0).withSecondOfMinute(0);
        DateTime end = now.withDayOfMonth(9).withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0);

        int minutes = Minutes.minutesBetween(start, end).getMinutes();
        int count = minutes / 60 + 1;
        System.out.println(count);
    }

    @Test
    public void test6() {
        DateTime now = DateTime.now();
        DateTime start = now.withDayOfMonth(9).withHourOfDay(1).withMinuteOfHour(0).withSecondOfMinute(0);
        DateTime end = now.withDayOfMonth(10).withHourOfDay(3).withMinuteOfHour(0).withSecondOfMinute(0);

        int minutes = Minutes.minutesBetween(start, end).getMinutes();
        int count = minutes / 60 + 1;
        System.out.println(count);
    }
}
