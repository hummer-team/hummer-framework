package com.hummer.common.holder;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

public class HummerHttpContextHolder {
    public static ServletRequestAttributes getRequestAttributes() {
        ServletRequestAttributes attributes = null;
        try {
            attributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        } catch (Exception e) {
            //LOGGER.debug("HttpServletRequest context is null.");
        }
        if (attributes == null) {
            throw new RuntimeException("HttpServletRequest context is null..");
        }
        return attributes;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getContextObject(String key, T defaultVal) {
        try {
            T t = (T) getRequestAttributes().getAttribute(key, SCOPE_REQUEST);
            return t == null ? defaultVal : t;
        } catch (Exception e) {
            //ignore
            return defaultVal;
        }
    }

    public <T> void setContextObject(String key, T obj) {
        getRequestAttributes().setAttribute(key, obj, SCOPE_REQUEST);
    }
}
