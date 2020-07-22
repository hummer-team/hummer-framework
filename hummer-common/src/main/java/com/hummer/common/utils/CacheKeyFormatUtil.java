package com.hummer.common.utils;

import com.hummer.common.security.Md5;
import com.hummer.core.PropertiesContainer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.Collections;
import java.util.Map;

@Slf4j
public class CacheKeyFormatUtil {
    private CacheKeyFormatUtil() {

    }

    public static String formatKey(String applicationName
            , String businessCode
            , Map<String, Object> parameterMap) {
        if (Strings.isEmpty(businessCode) || Strings.isEmpty(applicationName)) {
            throw new IllegalArgumentException("this business code or application name can't null");
        }

        StringBuilder key = new StringBuilder();
        key.append(applicationName)
                .append(":")
                .append(businessCode).append(":")
                .append(PropertiesContainer.valueOfString("spring.profiles.active"))
                .append(":");
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            key.append(entry.getKey())
                    .append(":")
                    .append(entry.getValue())
                    .append(":");
        }
        String formatKey = StringUtils.removeEnd(key.toString(), ":");
        log.debug("hummer simple cache format key is -> {}", formatKey);
        return formatKey;
    }

    public static String formatKey(String businessCode
            , Map<String, Object> parameterMap) {
        return formatKey(PropertiesContainer.valueOfString("spring.application.name")
                , businessCode
                , parameterMap);
    }

    public static String formatKey(String businessCode
            , Map<String, Object> parameterMap
            , boolean md5Parameter) {
        if (!md5Parameter) {
            return formatKey(PropertiesContainer.valueOfString("spring.application.name")
                    , businessCode
                    , parameterMap);
        } else {
            if (MapUtils.isEmpty(parameterMap)) {
                return formatKey(PropertiesContainer.valueOfString("spring.application.name")
                        , businessCode
                        , Collections.emptyMap());
            }

            return new StringBuilder()
                    .append(PropertiesContainer.valueOfString("spring.application.name"))
                    .append(":")
                    .append(businessCode).append(":")
                    .append(PropertiesContainer.valueOfString("spring.profiles.active"))
                    .append(":")
                    .append(Md5.encryptMd5(parameterMap.toString()))
                    .toString();
        }
    }
}
