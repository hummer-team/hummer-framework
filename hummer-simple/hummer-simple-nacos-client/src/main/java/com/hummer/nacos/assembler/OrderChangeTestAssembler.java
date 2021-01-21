package com.hummer.nacos.assembler;

import com.google.common.collect.Maps;
import com.hummer.request.idempotent.plugin.valid.ValidParamsAssembler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OrderChangeTestAssembler
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/1/21 16:57
 */
@Component
public class OrderChangeTestAssembler implements ValidParamsAssembler {

    @Override
    public Map<String, String> assemble(Object... objs) {
        Map<String, String> map = Maps.newHashMapWithExpectedSize(16);
        map.put("businessCode", String.valueOf(objs[0]));
        map.put("businessType", String.valueOf(objs[1]));
        return map;
    }
}
