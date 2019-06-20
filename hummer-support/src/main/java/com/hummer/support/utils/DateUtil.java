package com.hummer.support.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;
import java.util.Locale;

/**
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
    public static Date addTo(final Date date, final int day) {
        return new DateTime(date).plusDays(day).toDate();
    }
}
