package com.hummer.rest.bean;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.message.handle.MessageSerialConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
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
        if (StringUtils
                .isBlank(PropertiesContainer.valueOfString("fastJson.deserializer.dateFormat.custom.type"))) {
            fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            fastJsonConfig.setDateFormat(PropertiesContainer.valueOfString("fastJson.deserializer.dateFormat.custom.type"));
        }
        String fieldNameStyle = PropertiesContainer.valueOfString("hummer.message.serializer.field.name.style");
        if (Strings.isNotEmpty(fieldNameStyle)) {
            fastJsonConfig.getSerializeConfig().setPropertyNamingStrategy(PropertyNamingStrategy.valueOf(fieldNameStyle));
        }
        fastJsonConfig.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect);

        if (PropertiesContainer.valueOf("fastJson.deserializer.null.enable", Boolean.class, false)) {
            fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
        }

    }
}
