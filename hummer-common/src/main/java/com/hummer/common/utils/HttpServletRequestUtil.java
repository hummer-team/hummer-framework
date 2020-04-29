package com.hummer.common.utils;

import com.google.common.base.Strings;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * this class wrapper http servlet sample feature
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/21 15:53
 **/
public class HttpServletRequestUtil {
    private HttpServletRequestUtil() {

    }

    public static String getCurrentUrl(HttpServletRequest request) {
        StringBuffer buffer = request.getRequestURL();
        if (buffer != null) {
            if (!Strings.isNullOrEmpty(request.getQueryString())) {
                return String.format("%s?%s", buffer.toString()
                        , request.getQueryString());
            } else {
                return buffer.toString();
            }
        }
        return null;
    }

    public static String getUserAgent(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return request.getHeader(HttpHeaders.USER_AGENT);
    }
}
