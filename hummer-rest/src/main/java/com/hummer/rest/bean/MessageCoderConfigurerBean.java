package com.hummer.rest.bean;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.JSONPResponseBodyAdvice;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.hummer.common.coder.MsgPackCoder;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.message.coder.FastJsonMessageCoder;
import com.hummer.rest.message.coder.MsgPackMessageCoder;
import com.hummer.rest.message.coder.ProtostuffMessageCoder;
import com.hummer.rest.message.handle.MessageSerialConfig;
import com.hummer.rest.message.handle.RequestBodyHandle;
import com.hummer.rest.message.handle.ResponseBodyHandle;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static com.hummer.common.SysConstant.RestConstant.MVC_SERIALIZERFEATURE;


/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/24 13:33
 **/
@Configuration(proxyBeanMethods = false)
public class MessageCoderConfigurerBean extends WebMvcConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageCoderConfigurerBean.class);
    @Autowired(required = false)
    private RequestBodyHandle requestBodyHandle;
    @Autowired(required = false)
    private ResponseBodyHandle responseBodyHandle;
    @Autowired(required = false)
    private MessageSerialConfig serialConfig;

    @Bean
    public JSONPResponseBodyAdvice jsonpResponseBodyAdvice() {
        return new JSONPResponseBodyAdvice();
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, getMsgPackMessageCoder());
        converters.add(1, getProtostuffMessageConverterCoder());
        converters.add(2, getFastJsonMessageConverterCoder());
    }

    @PostConstruct()
    private void init() {
        MsgPackCoder.getSerializationConfigForJson()
                .getDefaultPropertyInclusion()
                .withContentInclusion(JsonInclude.Include.NON_NULL)
                .withContentInclusion(JsonInclude.Include.NON_EMPTY);
        MsgPackCoder.getSerializationConfigForJson().getDateFormat().setTimeZone(TimeZone.getTimeZone("GMT+08"));


        MsgPackCoder.getSerializationConfigForBinary().getDefaultPropertyInclusion()
                .withValueInclusion(JsonInclude.Include.NON_NULL)
                .withContentInclusion(JsonInclude.Include.NON_EMPTY);
        MsgPackCoder.getSerializationConfigForBinary().getDateFormat().setTimeZone(TimeZone.getTimeZone("GMT+08"));

        LOGGER.debug("msgpack coder serialization config init done.");
    }

    private HttpMessageConverter getProtostuffMessageConverterCoder() {
        return new ProtostuffMessageCoder();
    }

    private HttpMessageConverter getMsgPackMessageCoder() {
        return new MsgPackMessageCoder();
    }

    private HttpMessageConverter getFastJsonMessageConverterCoder() {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        FastJsonMessageCoder coder = new FastJsonMessageCoder(fastJsonConfig
                , requestBodyHandle
                , responseBodyHandle);

        if (serialConfig != null) {
            serialConfig.register(fastJsonConfig);
        }

        List<MediaType> mediaTypeList = new ArrayList<>();
        MediaType jsonType = new MediaType("application", "json", StandardCharsets.UTF_8);
        mediaTypeList.add(jsonType);

        MediaType wwwformUrlencodedType =
                new MediaType("application", "x-www-form-urlencoded", StandardCharsets.UTF_8);
        mediaTypeList.add(wwwformUrlencodedType);

        mediaTypeList.add(MediaType.TEXT_PLAIN);
        coder.setSupportedMediaTypes(mediaTypeList);


        List<SerializerFeature> listFeature = Lists.newArrayList();
        String serializerFeatureStr = PropertiesContainer.get(MVC_SERIALIZERFEATURE, String.class);
        if (StringUtils.isNotEmpty(serializerFeatureStr)) {
            Iterable<String> features = Splitter.on(",").split(serializerFeatureStr);
            for (String feature : features) {
                listFeature.add(SerializerFeature.valueOf(feature));
            }
            fastJsonConfig.setSerializerFeatures(listFeature.toArray(new SerializerFeature[0]));
        }
        //flush config
        coder.setFastJsonConfig(fastJsonConfig);

        LOGGER.debug("message service `FastJsonHttpMessageConverterService` register done");
        return coder;
    }
}
