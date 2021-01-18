package com.hummer.common.utils;

import com.hummer.common.exceptions.AppException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
    public static String formatNowDate(final String format) {
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
     * add day to date,return format 'yyyy-MM-dd 00:00:00'
     *
     * @param date
     * @param day
     * @return
     */
    public static Date addDayAndFormatAsyyyyMMdd(final Date date, final int day) {
        DateTime dateTime = new DateTime(date).plusDays(day);
        return DateTime.parse(dateTime.toString("yyyy-MM-dd 00:00:00")
                , org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
                .toDate();
    }

    /**
     * add day to date,return string value format 'yyyy-MM-dd 00:00:00'
     *
     * @param date
     * @param day
     * @return
     */
    public static String addDayAndFormatAsyyyyMMddStr(final Date date, final int day) {
        return new DateTime(date).plusDays(day)
                .toString("yyyy-MM-dd 00:00:00");
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
     * add minute to data,return format 'yyyy-MM-dd 00:00:00'
     *
     * @param date
     * @param minute
     * @return
     */
    public static Date addMinuteAndFormatAsyyyyMMdd(final Date date, final int minute) {
        String strDate = new DateTime(date).plusMinutes(minute).toString("yyyy-MM-dd 00:00:00");
        return DateTime.parse(strDate, org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
                .toDate();
    }

    /**
     * return now time,zoneId is :GMT+8
     *
     * @return java.util.Date
     * @author liguo
     * @date 2019/7/9 10:44
     * @since 1.0.0
     **/
    public static Date now() {
        return now("GMT+8");
    }

    public static Date now(String zoneId) {
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        TimeZone.setDefault(timeZone);
        return DateTime.now(DateTimeZone.forTimeZone(TimeZone.getDefault())).toDate();
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

    public static int startDateSubtractEndDateOfDay(final Date startDate
            , final Date endDate) {
        DateTime startDates = new DateTime(startDate);
        DateTime endDates = new DateTime(endDate);
        return Days.daysBetween(endDates, startDates).getDays();
    }

    public static Date addHour(final Date date, final int incrementHour) {
        if (date == null) {
            return null;
        }
        return parsingToDate(new DateTime(date).plusHours(incrementHour).toDate().toString()
                , DateTimeFormat.F2);
    }

    public static Date parsingToDate(final String dateStr, final DateTimeFormat format) {
        if (StringUtils.isEmpty(dateStr) || format == null) {
            return null;
        }
        try {
            return DateUtils.parseDate(dateStr, format.getValue());
        } catch (ParseException e) {
            throw new AppException(40000, String.format("String to date fail,s==%s,format==%s", dateStr, format.value));
        }
    }

    public static String dateFormat(final Date d, final DateTimeFormat format) {
        if (d == null || format == null) {
            return null;
        }
        return DateFormatUtils.format(d, format.value);
    }

    public static Date dateFormatAsDate(final Date d, final DateTimeFormat format) {
        if (d == null || format == null) {
            return null;
        }
        return parsingToDate(DateFormatUtils.format(d, format.value), format);
    }

    public enum DateTimeFormat {
        F1("yyyy-MM-dd HH:mm:ss"),
        F2("yyyy-MM-dd"),
        F3("HH:mm:ss"),
        F4("yyyyMMddHHmmss"),
        F5("yyyyMMddHHmmssSS"),
        F6("yyyyMMdd"),
        F7("yyyyMMddHHmmssSSS"),
        F8("yyyy-MM-dd HH:mm:ss.SSS"),
        ;
        private String value;

        DateTimeFormat(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
