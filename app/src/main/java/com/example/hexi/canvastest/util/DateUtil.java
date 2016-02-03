package com.example.hexi.canvastest.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.TimeZone;

/**
 * Created by Bruce on 2/28/15.
 */
public class DateUtil {
    public static DateTime parse_yyyyddMM_hhmmss(String dateStr) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss");
        fmt.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("asia/shanghai")));
        return DateTime.parse(dateStr, fmt);
    }

    public static DateTime parse_yyyyddMM(String dateStr) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyyMMdd");
        fmt.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone("asia/shanghai")));
        return DateTime.parse(dateStr, fmt);
    }

    public static String format_hh_mm(DateTime dateTime) {
        return dateTime.toString("HH:mm");
    }

    public static String format_hh_mm(long value) {
        return format_hh_mm(new DateTime(value));
    }

    public static DateTime getTradeStartTimeFromBondCategory(String bondCategory) {
        String[] ranges = bondCategory.split(";");
        int startMinutes =  Integer.parseInt(ranges[0].split("-")[0]);

        int hour = startMinutes / 60;
        int minute = startMinutes % 60;

        return DateTime.now().withTimeAtStartOfDay().withHourOfDay(hour).withMinuteOfHour(minute);
    }

    public static DateTime getTradeEndTimeFromBondCategory(String bondCategory) {
        String[] ranges = bondCategory.split(";");
        int endMinutes = Integer.parseInt(ranges[ranges.length - 1].split("-")[1]);

        int hour = endMinutes / 60;
        int minute = endMinutes % 60;

        return DateTime.now().plusDays(1).withTimeAtStartOfDay().withHourOfDay(hour).withMinuteOfHour(minute);
    }

    public static int getPointsOfOneTradeDayFrom(String bondCategory) {
        String[] ranges = bondCategory.split(";");
        int value = 0;
        for (String range : ranges) {
            String[] temp = range.split("-");
            value += (Integer.parseInt(temp[1]) - Integer.parseInt(temp[0]));
        }

        return value;
    }

    public static String format_yyyyMMddHHmm(DateTime time) {
        if (time == null) {
            return null;
        }
        return time.toString("yyyyMMddHHmm");
    }
}
