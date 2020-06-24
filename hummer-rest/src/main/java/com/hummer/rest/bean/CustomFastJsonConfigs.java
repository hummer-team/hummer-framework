package com.hummer.rest.bean;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.message.handle.MessageSerialConfig;
import com.hummer.rest.serializer.CustomStringDeserializer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

/**
 * CustomFastJsonConfigs
 *
 * @author chen wei
 * @version 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 * </p>
 * @date 2020/6/10 14:27
 */
@Configuration
public class CustomFastJsonConfigs implements MessageSerialConfig {

    @Override
    public void register(SerializeConfig serializeConfig) {
    }

    @Override
    public void register(FastJsonConfig fastJsonConfig) {
        if (PropertiesContainer.valueOf("fastJson.deserializer.String.custom.enable", Boolean.class, true)) {

            ParserConfig parserConfig = fastJsonConfig.getParserConfig();
            if (parserConfig != null) {
                parserConfig.putDeserializer(String.class, CustomStringDeserializer.instance);
            }
        }
        if (StringUtils
                .isNotBlank(PropertiesContainer.valueOfString("fastJson.deserializer.dateFormat.custom.type"))) {
            fastJsonConfig.setDateFormat(PropertiesContainer.valueOfString("fastJson.deserializer.dateFormat.custom.type"));
        }
    }
}
