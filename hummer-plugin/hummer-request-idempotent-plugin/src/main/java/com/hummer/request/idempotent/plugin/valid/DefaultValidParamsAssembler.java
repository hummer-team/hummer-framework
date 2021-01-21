package com.hummer.request.idempotent.plugin.valid;

import com.google.common.collect.Maps;
import com.hummer.core.PropertiesContainer;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

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


    @Override
    public Map<String, String> assemble(Object... objs) {
        if (ArrayUtils.isEmpty(objs)) {
            return null;
        }
        Map<String, String> map = Maps.newConcurrentMap();
        String key = String.valueOf(objs[0]);
        map.put(key, PropertiesContainer.valueOfString(key));
        return map;
    }
}
