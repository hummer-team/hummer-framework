package com.hummer.common.utils;

import com.hummer.common.SysConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * EncoderUtil
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/2/8 14:31
 */
public class EncoderUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(EncoderUtil.class);

    /**
     * urlEncode
     *
     * @param s       源字符串
     * @param charset 编码
     * @return java.lang.String
     * @author chen wei
     * @date 2021/2/8
     */
    public static String urlEncode(String s, String charset) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        try {
            return URLEncoder.encode(s, charset);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("urlEncode fail,s=={},charset=={}", s, charset);
        }
        return null;
    }

    /**
     * urlEncode utf-8编码
     *
     * @param s 源字符串
     * @return java.lang.String
     * @author chen wei
     * @date 2021/2/8
     */
    public static String urlEncode(String s) {
        return urlEncode(s, SysConstant.DEFAULT_CHARSET_NAME);
    }

    /**
     * urlDecode  utf-8编码
     *
     * @param s 源字符串
     * @return java.lang.String
     * @author chen wei
     * @date 2021/2/8
     */
    public static String urlDecode(String s) {
        return urlDecode(s, SysConstant.DEFAULT_CHARSET_NAME);
    }


    /**
     * urlDecode
     *
     * @param s       源字符串
     * @param charset 编码
     * @return java.lang.String
     * @author chen wei
     * @date 2021/2/8
     */
    public static String urlDecode(String s, String charset) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        try {
            return URLDecoder.decode(s, charset);
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("urlDecode fail,s=={},charset=={}", s, charset);
        }
        return null;
    }
}
