package com.hummer.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

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

    /**
     * 字符串转化为数字格式
     *
     * @param resource 字符串
     * @param cla      目标类型
     * @return T
     * @author chen wei
     * @date 2020/8/5
     */
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

    /**
     * 获取uuid字符串
     *
     * @return java.lang.String
     * @author chen wei
     * @date 2020/8/5
     */
    public static String getUuid() {
        return UUID.randomUUID().toString();
    }


    /**
     * 获取uuid字符串，并移除-
     *
     * @return java.lang.String
     * @author chen wei
     * @date 2020/8/5
     */
    public static String getUuidShort() {
        return getUuid().replace("-", "");
    }

    public static String subStringLength(String s, int len) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        if (len <= 0 || s.length() <= len) {
            return s;
        }
        return s.substring(0, len);
    }

    public static <T> T getFirstIfSingleElseNull(List<T> list) {
        if (list != null && list.size() == 1) {
            return list.get(0);
        }
        return null;
    }
}
