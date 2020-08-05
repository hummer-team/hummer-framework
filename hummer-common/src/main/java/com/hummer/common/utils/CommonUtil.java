package com.hummer.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * CommonUtil
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/6/2 13:32
 */
public class CommonUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonUtil.class);

    private CommonUtil() {
    }

    /**
     * ifNull return deft , else return resource
     *
     * @param resource
     * @param deft
     * @return T
     * @author chen wei
     * @date 2020/6/2
     */
    public static <T> T ifNullDefault(T resource, T deft) {
        if (resource == null) {
            return deft;
        }
        return resource;
    }

    public static <T> T typeChange(String resource, Class<? extends Number> cla) {
        if (StringUtils.isBlank(resource)) {
            return null;
        }
        try {
            Method method = ReflectionUtils.findMethod(cla, "valueOf", String.class);
            if (method == null) {
                return null;
            }
            return (T) ReflectionUtils.invokeMethod(method, cla, resource);
        } catch (Exception e) {
            LOGGER.warn("String convert number fail", e);
        }
        return null;
    }
}
