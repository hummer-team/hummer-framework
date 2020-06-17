package com.hummer.common.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;
import java.util.Locale;

/**
 * date util,if customer imp date feature recommend use {@link org.joda.time.DateTime}
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 14:45
 **/
public class DateUtil {
    private DateUtil() {

    }

    /**
     * return time millis with UTC
     *
     * @param []
     * @return long
     * @author liguo
     * @date 2019/6/20 14:58
     * @version 1.0.0
     **/
    public static long getTimestampInMillis() {
        return new DateTime(DateTimeZone.UTC).getMillis();
    }

    /**
     * format current time by format string
     *
     * @param format
     * @return java.lang.String
     * @author liguo
     * @date 2019/6/20 16:28
     * @version 1.0.0
     **/
    public static String formatNowData(final String format) {
        return new DateTime().toString(format, Locale.CHINESE);
    }

    /**
     * add day to date
     *
     * @param date
     * @param day
     * @return java.util.Date
     * @author liguo
     * @date 2019/6/20 16:31
     * @version 1.0.0
     **/
    public static Date addDay(final Date date, final int day) {
        return new DateTime(date).plusDays(day).toDate();
    }

    /**
     * add minute to data
     *
     * @param date   date
     * @param minute minute
     * @return java.util.Date
     * @author liguo
     * @date 2019/7/9 10:42
     * @since 1.0.0
     **/
    public static Date addMinute(final Date date, final int minute) {
        return new DateTime(date).plusMinutes(minute).toDate();
    }

    /**
     * return now time
     *
     * @return java.util.Date
     * @author liguo
     * @date 2019/7/9 10:44
     * @since 1.0.0
     **/
    public static Date now() {
        return DateTime.now(DateTimeZone.UTC).toDate();
    }

    /**
     * targetDate subtract now
     *
     * @param targetDate date
     * @return int
     * @author liguo
     * @date 2019/7/11 13:38
     * @since 1.0.0
     **/
    public static long subtractNowDate(final Date targetDate) {

        long end = new DateTime(targetDate).toDateTime(DateTimeZone.UTC).getMillis();
        long start = DateTime.now(DateTimeZone.UTC).getMillis();

        return (end - start);
    }

    public static Date addHour(final Date date, final int increment) {
        if (date == null) {
            return null;
        }
        return new DateTime(date).plusHours(increment).toDate();
    }
}
