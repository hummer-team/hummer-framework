package com.hummer.rest.message.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.hummer.common.exceptions.ErrorRequestException;
import com.hummer.common.exceptions.SysException;
import com.hummer.common.utils.ZipUtil;
import com.hummer.rest.message.CompressHandler;
import com.hummer.rest.message.handle.RequestBodyHandle;
import com.hummer.rest.message.handle.ResponseBodyHandle;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * this class wrapper fast json message service.
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/24 13:39
 **/
public class FastJsonHttpMessageConverterService extends FastJsonHttpMessageConverter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FastJsonHttpMessageConverterService.class);
    private FastJsonConfig fastJsonConfig;
    private final RequestBodyHandle requestBodyHandle;
    private final ResponseBodyHandle responseBodyHandle;
    private static final String HEADER_GZIP_STR = "gzip";
    private static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public FastJsonHttpMessageConverterService(FastJsonConfig fastJsonConfig
            , RequestBodyHandle requestBodyHandle
            , ResponseBodyHandle responseBodyHandle) {
        super();
        this.fastJsonConfig = fastJsonConfig;
        this.requestBodyHandle = requestBodyHandle;
        this.responseBodyHandle = responseBodyHandle;
    }


    @Override
    public FastJsonConfig getFastJsonConfig() {
        return fastJsonConfig;
    }

    @Override
    public void setFastJsonConfig(final FastJsonConfig config) {
        fastJsonConfig = config;
    }

    /**
     * read request body
     *
     * @param clazz        target class type
     * @param inputMessage input message
     * @return java.lang.Object
     * @author liguo
     * @date 2019/6/24 15:47
     * @version 1.0.0
     **/
    @Override
    public Object read(Type type, Class<? extends Object> clazz,
                       HttpInputMessage inputMessage) throws IOException {
        return inRead(type, inputMessage);
    }

    @Override
    protected Object readInternal(Class<? extends Object> clazz,
                                  HttpInputMessage inputMessage) throws IOException {
        return inRead(clazz, inputMessage);
    }

    /**
     * response body handle
     *
     * @param obj           business logic return
     * @param outputMessage output
     * @return void
     * @author liguo
     * @date 2019/6/24 15:50
     * @version 1.0.0
     **/
    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException {
        write(obj, outputMessage);
    }

    private void write(Object obj, HttpOutputMessage outputMessage) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            long start = System.currentTimeMillis();

            HttpHeaders httpHeaders = outputMessage.getHeaders();
            long serialStart = System.currentTimeMillis();
            //implement business logic return result serial
            String result;
            if (responseBodyHandle != null) {
                result = responseBodyHandle.handle(obj, httpHeaders);
            } else {
                FastJsonConfig jsonConfig = getFastJsonConfig();
                SerializeConfig serializeConfig = jsonConfig.getSerializeConfig();
                //check serial configuration
                if (serializeConfig == null) {
                    LOGGER.debug("no settings serial configuration,use default serial configuration");
                    serializeConfig = new SerializeConfig();
                }

                result = JSON.toJSONString(obj
                        , serializeConfig
                        , jsonConfig.getSerializeFilters()
                        , jsonConfig.getDateFormat()
                        , JSON.DEFAULT_GENERATE_FEATURE
                        , jsonConfig.getSerializerFeatures());
            }

            //if client accept gzip then handle compress
            byte[] content = gzipIfNecessary(result, httpHeaders);
            long serialCostTime = System.currentTimeMillis() - serialStart;

            httpHeaders.setContentLength(content.length);
            outputStream.write(content);
            OutputStream out = outputMessage.getBody();
            outputStream.writeTo(out);
            LOGGER.debug("business logic execute done , serial total cost {} millis" +
                            ", return result serial cost {} millis,serial result {} bytes"
                    , System.currentTimeMillis() - start
                    , serialCostTime
                    , content.length);
        }
    }

    private Object inRead(Type type, HttpInputMessage inputMessage) throws IOException {
        //head
        HttpHeaders header = inputMessage.getHeaders();
        //body stream
        InputStream inputStream = inputMessage.getBody();
        //execute customer request body handle
        String contentStr;
        if (requestBodyHandle != null) {
            contentStr = requestBodyHandle.handle(inputStream, header);
            LOGGER.debug("customer parse request body done,body string {}"
                    , contentStr);
            return contentStr;
        }

        String contentEncoding = CompressHandler.getContentEncoding();
        //if this gzip body then execute zip stream handle
        if (contentEncoding != null && contentEncoding.contains(HEADER_GZIP_STR)) {
            inputStream = new GZIPInputStream(inputStream);
        }

        //handle `application/x-www-form-urlencoded` body
        MediaType contentType = header.getContentType();
        if (contentType == null) {
            throw new SysException("request content type is null");
        }
        Charset charset = (contentType.getCharset() != null ? contentType.getCharset() : DEFAULT_CHARSET);
        contentStr = CharStreams.toString(new InputStreamReader(inputStream, charset));
        if (contentType.isCompatibleWith(MediaType.APPLICATION_FORM_URLENCODED)) {
            try {
                contentStr = convertFormDataToJsonString(contentStr, charset);
            } catch (Exception e) {
                LOGGER.error("request body parse json failed,media type application/x-www-form-urlencoded", e);
                throw new ErrorRequestException("request body parse json failed.", contentStr);
            }
        }

        LOGGER.debug("request body content size {} byte"
                , contentStr.getBytes(DEFAULT_CHARSET).length);
        //parse content to domain entity
        try {
            return JSON.parseObject(contentStr, type, fastJsonConfig.getParserConfig());
        } catch (Exception e) {
            LOGGER.error("request body parse json failed,content string {},exception ", contentStr, e);
            throw new ErrorRequestException(e.getMessage(), contentStr);
        }
    }

    private String convertFormDataToJsonString(String body, Charset charset)
            throws UnsupportedEncodingException {
        //split to map
        Map<String, String> bodyMap = Splitter
                .on("&").withKeyValueSeparator("=")
                .split(body);
        if (MapUtils.isEmpty(bodyMap)) {
            return "{}";
        }
        //decoding
        Map<String, Object> decodingMap = Maps.newHashMapWithExpectedSize(bodyMap.size());
        for (Map.Entry<String, String> entry : bodyMap.entrySet()) {
            decodingMap.put(URLDecoder.decode(entry.getKey(), charset.name())
                    , (Object) URLDecoder.decode(entry.getValue(), charset.name()));
        }
        return JSON.toJSONString(decodingMap);
    }

    private byte[] gzipIfNecessary(String result, HttpHeaders headers) throws IOException {
        byte[] originBytes = result.getBytes(DEFAULT_CHARSET);
        if (isGzipResponseAccepted()) {
            byte[] gzipped = ZipUtil.gzip(originBytes);
            headers.set(HEADER_CONTENT_ENCODING, HEADER_GZIP_STR);
            return gzipped;
        } else {
            return originBytes;
        }
    }

    private boolean isGzipResponseAccepted() {
        String acceptEncoding = CompressHandler.getAcceptEncoding();
        return (acceptEncoding != null && acceptEncoding.contains(HEADER_GZIP_STR));
    }
}
