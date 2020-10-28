package com.hummer.request.idempotent.plugin;

import com.google.common.collect.Maps;
import com.hummer.common.utils.CacheKeyFormatUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.Map;

@Slf4j
public class KeyUtil {

    private KeyUtil() {

    }

    public static Map<String, Object> getFieldNameValueMap(ProceedingJoinPoint joinPoint
            , String keyName, String keyValue) {

        //
        Parameter[] parameters = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameters();
        if (parameters == null) {
            return Collections.emptyMap();
        }
        Map<String, Object> clsMap = Maps.newHashMapWithExpectedSize(16);
        for (int p = 0; p < parameters.length; p++) {
            clsMap.put(parameters[p].getName(), parameters[p].getType().getSimpleName());
        }
        clsMap.put(keyName, keyValue);
        return clsMap;
    }

    public static String formatKey(String applicationName, String businessCode, Map<String, Object> parameterMap) {

        return CacheKeyFormatUtil.formatKey(applicationName, businessCode, parameterMap);
    }
}
