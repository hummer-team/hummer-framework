package com.hummer.rest.bean;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.message.handle.MessageSerialConfig;
import com.hummer.rest.message.handle.RequestBodyHandle;
import com.hummer.rest.message.handle.ResponseBodyHandle;
import com.hummer.rest.message.service.FastJsonHttpMessageConverterService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static com.hummer.common.SysConstant.RestConstant.MVC_SERIALIZERFEATURE;


/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/24 13:33
 **/
@Configuration
public class MessageConverterBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConverterBean.class);
    @Autowired(required = false)
    private RequestBodyHandle requestBodyHandle;
    @Autowired(required = false)
    private ResponseBodyHandle responseBodyHandle;
    @Autowired(required = false)
    private MessageSerialConfig serialConfig;

    /**
     * register message convert service.
     *
     * @param []
     * @return org.springframework.boot.autoconfigure.http.HttpMessageConverters
     * @author liguo
     * @date 2019/6/24 13:36
     * @version 1.0.0
     **/
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        // 指定时区为北京时区，东八区
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        FastJsonHttpMessageConverterService service = new FastJsonHttpMessageConverterService(fastJsonConfig
                , requestBodyHandle
                , responseBodyHandle);

        if (serialConfig != null) {
            serialConfig.register(fastJsonConfig);
        }

        List<MediaType> mediaTypeList = new ArrayList<>();
        MediaType jsonType = new MediaType("application", "json", Charset.forName("utf-8"));
        mediaTypeList.add(jsonType);

        MediaType wwwformUrlencodedType =
                new MediaType("application", "x-www-form-urlencoded", Charset.forName("utf-8"));
        mediaTypeList.add(wwwformUrlencodedType);

        mediaTypeList.add(MediaType.TEXT_PLAIN);
        service.setSupportedMediaTypes(mediaTypeList);


        List<SerializerFeature> listFeature = Lists.newArrayList();
        String serializerFeatureStr = PropertiesContainer.get(MVC_SERIALIZERFEATURE, String.class);
        if (StringUtils.isNotEmpty(serializerFeatureStr)) {
            Iterable<String> features = Splitter.on(",").split(serializerFeatureStr);
            for (String feature : features) {
                listFeature.add(SerializerFeature.valueOf(feature));
            }
            fastJsonConfig.setSerializerFeatures(listFeature.toArray(new SerializerFeature[0]));
        }

//        if (StringUtils
//                .isBlank(PropertiesContainer.valueOfString("fastJson.deserializer.dateFormat.custom.type"))) {
//            fastJsonConfig.setSerializerFeatures(SerializerFeature.UseISO8601DateFormat);
//        } else {
//            fastJsonConfig.setDateFormat(PropertiesContainer.valueOfString("fastJson.deserializer.dateFormat.custom.type"));
//        }
        //date time format
//        if (!listFeature.contains(SerializerFeature.WriteDateUseDateFormat)) {
//            fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat);
//        }
        //flush config
        service.setFastJsonConfig(fastJsonConfig);

        LOGGER.debug("message service `FastJsonHttpMessageConverterService` register done");
        return new HttpMessageConverters(service);
    }
}
