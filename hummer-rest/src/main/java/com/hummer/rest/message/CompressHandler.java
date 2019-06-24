package com.hummer.rest.message;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


public class CompressHandler extends HandlerInterceptorAdapter {

    private static ThreadLocal<Map<String, String>> headers = new ThreadLocal<>();
    public static final String CONTENT_ENCODING_STRING = "Content-Encoding";
    public static final String ACCEPT_ENCODING_STRING = "Accept-Encoding";

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
            throws Exception {
        Map<String, String> headerValue = headers.get();

        if (headerValue == null) {
            headerValue = new HashMap<>(4);
        }

        headerValue.put(CONTENT_ENCODING_STRING, httpServletRequest.getHeader(CONTENT_ENCODING_STRING));
        headerValue.put(ACCEPT_ENCODING_STRING, httpServletRequest.getHeader(ACCEPT_ENCODING_STRING));

        headers.set(headerValue);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception ex) {
        headers.get().clear();
    }

    public static String getContentEncoding() {
        Map<String, String> headerValue = headers.get();
        if (headerValue != null) {
            return headerValue.get(CONTENT_ENCODING_STRING);
        } else {
            return null;
        }

    }

    public static String getAcceptEncoding() {
        Map<String, String> headerValue = headers.get();
        if (headerValue != null) {
            return headerValue.get(ACCEPT_ENCODING_STRING);
        } else {
            return null;
        }
    }
}
