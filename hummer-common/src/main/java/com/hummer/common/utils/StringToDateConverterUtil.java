package com.hummer.common.utils;

import com.hummer.common.exceptions.AppException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/6 11:15
 **/
public class StringToDateConverterUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(StringToDateConverterUtil.class);

    private StringToDateConverterUtil(){

    }

    private static final List<String> DEFAULT_DATE_FORMATS = Arrays.asList(
            "yyyy-MM-dd'T'HH:mm:ssX",
            "yyyy-MM-dd'T'HH:mm:ssZ",
            "MM/dd/yyyy hh:mm:ss a",
            "yyyy/MM/dd hh:mm:ss",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd'T'HH:mm",
            "yyyy-MM-dd+HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyyMMdd",
            "yyyyMM");

    public static Date tryParseDateWithMultiFormats(String dateString) {
        return tryParseDateWithMultiFormats(dateString, null);
    }


    public static Date tryParseDateWithMultiFormats(
            String dateString, List<String> additionalFormats) {
        if (StringUtils.isEmpty(dateString)){
            return null;
        }

        List<String> dateFormats;
        if (CollectionUtils.isEmpty(additionalFormats)){
            dateFormats = DEFAULT_DATE_FORMATS;
        } else {
            dateFormats = new ArrayList<>();
            dateFormats.addAll(additionalFormats);
            dateFormats.addAll(DEFAULT_DATE_FORMATS);
        }

        for(String format: dateFormats){
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.ENGLISH);
            try{
                return sdf.parse(dateString);
            } catch (ParseException e) {
                // intentionally empty
                // try next format
            }
        }
        LOGGER.error("Invalid input for date. Given {}, expecting format {}",
                dateString, dateFormats);

        throw new AppException(40000,String.format("request parameter type parse failed %s",dateString));
    }
}
