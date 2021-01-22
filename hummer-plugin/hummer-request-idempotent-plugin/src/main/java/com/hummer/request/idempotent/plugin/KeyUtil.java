package com.hummer.request.idempotent.plugin;

import com.google.common.collect.Maps;
import com.hummer.common.security.Md5;
import com.hummer.common.utils.CacheKeyFormatUtil;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.request.idempotent.plugin.annotation.RequestIdempotentAnnotation;
import com.hummer.request.idempotent.plugin.valid.DefaultValidParamsAssembler;
import com.hummer.request.idempotent.plugin.valid.ValidParamsAssembler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
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

    public static String formatKey(String applicationName, String businessCode, ProceedingJoinPoint joinPoint
            , RequestIdempotentAnnotation requestIdempotent) {
        if (Strings.isEmpty(businessCode) || Strings.isEmpty(applicationName)) {
            throw new IllegalArgumentException("this business code or application name can't null");
        }
        ValidParamsAssembler assembler = SpringApplicationContext.getBean(requestIdempotent.validParamsAssembler());
        Map<String, String> validParams;
        if (assembler instanceof DefaultValidParamsAssembler) {
            validParams = assembler.assemble(requestIdempotent.key());
        } else {
            validParams = assembler.assemble(joinPoint.getArgs());
        }
        if (MapUtils.isEmpty(validParams)) {
            return null;
        }
        validParams.putAll(getFieldNameTypeMap(joinPoint));
        String validKey = formatParamsMd5(validParams);

        StringBuilder key = new StringBuilder();
        key.append(applicationName)
                .append(":")
                .append(businessCode).append(":")
                .append(PropertiesContainer.valueOfString("spring.profiles.active"))
                .append(":")
                .append(joinPoint.getSignature().getName())
                .append(":")
                .append(validKey);
        return key.toString();
    }

    public static String formatLockKey(String key) {
        return key + ":" + "lock";
    }

    private static Map<String, String> getFieldNameTypeMap(ProceedingJoinPoint point) {
        Parameter[] parameters = ((MethodSignature) point.getSignature()).getMethod().getParameters();
        if (parameters == null) {
            return Collections.emptyMap();
        }
        Map<String, String> clsMap = Maps.newHashMapWithExpectedSize(16);
        for (int p = 0; p < parameters.length; p++) {
            clsMap.put(parameters[p].getName(), parameters[p].getType().getSimpleName());
        }
        return clsMap;
    }

    private static String formatParamsMd5(Map<String, String> map) {
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        StringBuilder key = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            key.append(entry.getKey())
                    .append(":")
                    .append(entry.getValue())
                    .append(":");
        }
        String formatKey = StringUtils.removeEnd(key.toString(), ":");
        return Md5.encryptMd5(formatKey);
    }
}
