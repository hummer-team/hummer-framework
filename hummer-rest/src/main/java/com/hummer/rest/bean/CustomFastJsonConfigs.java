package com.hummer.rest.bean;

import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.message.handle.MessageSerialConfig;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.hummer.common.SysConstant.RestConstant.MVC_SERIALIZERFEATURE;
import static com.hummer.common.SysConstant.RestConstant.MVC_SERIALIZER_FEATURE_DEFAULT;

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

        fastJsonConfig.setSerializerFeatures(getSerializerFeature().toArray(new SerializerFeature[0]));
    }

    public static List<SerializerFeature> getSerializerFeature() {

        List<SerializerFeature> listFeature = Lists.newArrayList();
        String serializerFeatureStr = PropertiesContainer.get(MVC_SERIALIZERFEATURE, String.class
                , MVC_SERIALIZER_FEATURE_DEFAULT);
        if (StringUtils.isNotEmpty(serializerFeatureStr)) {
            Iterable<String> features = Splitter.on(",").split(serializerFeatureStr);
            for (String feature : features) {
                listFeature.add(SerializerFeature.valueOf(feature));
            }
        }
        return listFeature;
    }

    public static void removeSerialize(FastJsonConfig fastJsonConfig, SerializerFeature serializerFeature) {
        if (fastJsonConfig == null || serializerFeature == null
                || ArrayUtils.isEmpty(fastJsonConfig.getSerializerFeatures())) {
            return;
        }
        SerializerFeature[] arr = fastJsonConfig.getSerializerFeatures();
        arr = ArrayUtils.removeElement(arr, serializerFeature);
        fastJsonConfig.setSerializerFeatures(arr);
    }

    public static void addSerialize(FastJsonConfig fastJsonConfig, SerializerFeature serializerFeature) {
        if (fastJsonConfig == null || serializerFeature == null) {
            return;
        }
        SerializerFeature[] arr = fastJsonConfig.getSerializerFeatures();
        if (arr == null) {
            arr = new SerializerFeature[]{serializerFeature};
        } else {
            arr = ArrayUtils.add(arr, serializerFeature);
        }
        fastJsonConfig.setSerializerFeatures(arr);
    }
}
