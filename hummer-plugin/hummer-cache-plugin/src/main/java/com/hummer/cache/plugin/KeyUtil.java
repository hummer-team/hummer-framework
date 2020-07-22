package com.hummer.cache.plugin;

import com.google.common.collect.Maps;
import com.hummer.common.security.Md5;
import com.hummer.common.utils.CacheKeyFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class KeyUtil {

    private KeyUtil() {

    }

    public static Map<String, Object> getFieldNameValueMap(ProceedingJoinPoint joinPoint)
            throws IllegalAccessException {

        //
        Parameter[] parameters = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameters();
        if (parameters == null) {
            return Collections.emptyMap();
        }
        Object[] args = joinPoint.getArgs();
        Map<String, Object> clsMap = Maps.newHashMapWithExpectedSize(16);
        for (int p = 0; p < parameters.length; p++) {
            HummerSimpleObjectCacheKey key = parameters[p].getAnnotation(HummerSimpleObjectCacheKey.class);
            if (key != null) {
                clsMap.put(StringUtils.isEmpty(key.keyName()) ? parameters[p].getName() : key.keyName()
                        , formatValue(key, args[p]));
            }
        }

        for (Object arg : args) {
            Class<?> cls = arg.getClass();
            //class type
            if (!cls.isPrimitive()) {
                for (Field f : cls.getDeclaredFields()) {
                    HummerSimpleObjectCacheKey key = f.getAnnotation(HummerSimpleObjectCacheKey.class);
                    if (key != null) {
                        f.setAccessible(true);
                        clsMap.put(StringUtils.isEmpty(key.keyName()) ? f.getName() : key.keyName()
                                , formatValue(key, f.get(arg)));
                    }
                }
            } else {
                HummerSimpleObjectCacheKey key2 = cls.getAnnotation(HummerSimpleObjectCacheKey.class);
                if (key2 != null) {
                    clsMap.put(StringUtils.isEmpty(key2.keyName()) ? cls.getSimpleName() : key2.keyName()
                            , formatValue(key2, arg));
                }
            }
        }

        return clsMap;
    }

    private static Object formatValue(HummerSimpleObjectCacheKey key, Object value) {
        if (value == null) {
            return null;
        }

        return key.maxKeyMd5() ? Md5.encryptMd5(value) : value;
    }

    public static String formatKey(String applicationName, String businessCode, Map<String, Object> parameterMap) {
        return CacheKeyFormatUtil.formatKey(applicationName,businessCode,parameterMap);
    }
}
