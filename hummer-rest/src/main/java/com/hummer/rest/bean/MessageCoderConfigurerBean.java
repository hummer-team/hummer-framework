package com.hummer.rest.bean;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.JSONPResponseBodyAdvice;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hummer.common.coder.MsgPackCoder;
import com.hummer.core.PropertiesContainer;
import com.hummer.rest.message.coder.FastJsonMessageCoder;
import com.hummer.rest.message.coder.MsgPackMessageCoder;
import com.hummer.rest.message.coder.ProtostuffMessageCoder;
import com.hummer.rest.message.handle.MessageSerialConfig;
import com.hummer.rest.message.handle.RequestBodyHandle;
import com.hummer.rest.message.handle.ResponseBodyHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.validation.constraints.Null;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;


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

    private SerializationConfig msgPackCoderConfig(SerializationConfig config, @Null String desc) {
        config = config.with(new SimpleDateFormat(PropertiesContainer.valueOfString("message.encoder.datetime.format"
                , "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")))
                .withPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL,
                        JsonInclude.Include.NON_EMPTY))
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        config.getDateFormat().setTimeZone(TimeZone.getTimeZone("GMT+08"));
        LOGGER.debug("msgpack coder serialization config init done,{}", desc);
        return config;
    }

    private DeserializationConfig msgPackDeCoderConfig(DeserializationConfig config, @Null String desc) {
        config = config.without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        LOGGER.debug("msgpack coder deserializationConfig config init done,{}", desc);
        return config;
    }

    private HttpMessageConverter getProtostuffMessageConverterCoder() {
        return new ProtostuffMessageCoder();
    }

    private HttpMessageConverter getMsgPackMessageCoder() {
        return new MsgPackMessageCoder(msgPackCoderConfig(MsgPackCoder.getSerializationConfigForJson()
                , "for json encoder")
                , msgPackCoderConfig(MsgPackCoder.getSerializationConfigForBinary(), "for binary encoder")
                , msgPackDeCoderConfig(MsgPackCoder.getDeserializationConfigForJson(), "for json decoder")
                , msgPackDeCoderConfig(MsgPackCoder.getDeserializationConfigForBinary(), "for binary decoder"));
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

        //flush config
        coder.setFastJsonConfig(fastJsonConfig);

        LOGGER.debug("message service `FastJsonHttpMessageConverterService` register done");
        return coder;
    }
}
