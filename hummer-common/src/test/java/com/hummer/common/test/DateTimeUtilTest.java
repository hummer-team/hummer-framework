package com.hummer.common.test;

import com.hummer.common.utils.DateUtil;
import org.junit.Test;

public class DateTimeUtilTest {
    @Test
    public void dateTimeFormatAsyyyyMMdd() {
        System.out.println(DateUtil.addDayAndFormatAsyyyyMMdd(DateUtil.now(), 2).toString());
        System.out.println(DateUtil.addDay(DateUtil.now(), 90) + "---:"
                + DateUtil.startDateSubtractEndDateOfDay(DateUtil.addDay(DateUtil.now(), 90)
                , DateUtil.now()));

        System.out.println(DateUtil.addDayAndFormatAsyyyyMMddStr(DateUtil.now(),1));

        System.out.println(DateUtil.addMinuteAndFormatAsyyyyMMdd(DateUtil.now(),1));

        System.out.println(DateUtil.addHour(DateUtil.now(),1));
        System.out.println(DateUtil.addHourAndFormatAsYYYYMMddHHmmss(DateUtil.now(),1));
    }
}
