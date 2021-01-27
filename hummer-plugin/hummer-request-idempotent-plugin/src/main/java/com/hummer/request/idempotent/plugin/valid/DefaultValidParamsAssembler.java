package com.hummer.request.idempotent.plugin.valid;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

/**
 * DefaultValidParamsAssembler
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/1/20 18:55
 */
@Component
public class DefaultValidParamsAssembler implements ValidParamsAssembler {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultValidParamsAssembler.class);

    @Override
    public Map<String, String> assemble(Object... objs) {
        if (ArrayUtils.isEmpty(objs)) {
            return null;
        }
        String key = String.valueOf(objs[0]);
        String value = getRequestHeader(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        Map<String, String> map = Maps.newHashMapWithExpectedSize(16);
        map.put(key, getRequestHeader(key));
        return map;
    }

    private String getRequestHeader(String key) {
        try {

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null && attributes.getRequest() != null) {
                return attributes.getRequest().getHeader(key);
            }
        } catch (Exception e) {
            LOGGER.warn("get request context fail, key== {} ", key);
        }
        return null;
    }
}
