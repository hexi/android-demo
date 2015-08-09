package com.hexi;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
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
}
