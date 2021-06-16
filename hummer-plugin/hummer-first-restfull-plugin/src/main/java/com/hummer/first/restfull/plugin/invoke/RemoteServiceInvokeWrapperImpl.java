package com.hummer.first.restfull.plugin.invoke;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hummer.common.http.HttpInternalUtil;
import com.hummer.common.http.HttpSyncClient;
import com.hummer.common.utils.CollectionUtil;
import com.hummer.core.PropertiesContainer;
import com.hummer.first.restfull.plugin.ApiMetadata;
import com.hummer.first.restfull.plugin.annotation.HummerRestApiDeclare;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author edz
 */
@Slf4j
public class RemoteServiceInvokeWrapperImpl implements RemoteServiceInvokeWrapper {

    public static final RemoteServiceInvokeWrapper INSTANCE = new RemoteServiceInvokeWrapperImpl();
    private static final ParameterNameDiscoverer DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * call service by declare parameter
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     */
    @Override
    @SuppressWarnings({"unchecked", "raw"})
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        HummerRestApiDeclare declare = method.getAnnotation(HummerRestApiDeclare.class);
        if (declare == null) {
            log.error("method {} not use annotation HummerRestApiDeclare", method.getName());
            throw new IllegalArgumentException("not found annotation HummerRestApiDeclare");
        }
        long start = System.currentTimeMillis();
        ApiMetadata apiMetadata = builderApiMetadata(declare, method, args);
        long costMills = System.currentTimeMillis() - start;
        if (costMills > 0) {
            log.debug("method {} builder api metadata done,cost {} mills", method.getName(), costMills);
        }
        //todo cache

        //call remote api
        Object o = apiMetadata.getAsync() ? invokeOfAsync(apiMetadata) : invokeOfSync(apiMetadata);
        //after handler
        if (apiMetadata.getAfterHandler() != null) {
            apiMetadata.getAfterHandler().handler(o);
        }
        return o;
    }

    private Object invokeOfSync(ApiMetadata apiMetadata) {
        //requestBase
        HttpRequestBase requestBase = builderRequestBase(apiMetadata);
        //send call
        String result = HttpSyncClient.sendHttpRequestByRetry(requestBase, apiMetadata.getTimeOutMills()
                , TimeUnit.MILLISECONDS, apiMetadata.getRetryCount());
        //parse
        return apiMetadata.getParse() != null
                ? apiMetadata.getParse().parse(result)
                : JSON.parseObject(result, apiMetadata.getReturnType());
    }

    private HttpRequestBase builderRequestBase(ApiMetadata apiMetadata) {
        HttpRequestBase requestBase = HttpInternalUtil.getHttpRequestV2(apiMetadata.getUrl()
                , apiMetadata.getRequestMethod());

        if (StringUtils.isNotEmpty(apiMetadata.getBody())) {
            StringEntity stringEntity = new StringEntity(apiMetadata.getBody(), "utf-8");
            if (requestBase instanceof HttpEntityEnclosingRequestBase) {
                ((HttpEntityEnclosingRequestBase) requestBase).setEntity(stringEntity);
            }
        }

        if (!apiMetadata.getHeaderList().isEmpty()) {
            requestBase.setHeaders(apiMetadata.getHeaderList().toArray(new Header[0]));
        }
        return requestBase;
    }

    private Object invokeOfAsync(ApiMetadata apiMetadata) {
        return invokeOfSync(apiMetadata);
    }

    private ApiMetadata builderApiMetadata(HummerRestApiDeclare declare, Method method, Object[] args) throws Exception {
        ApiMetadata metadata;
        //Configuration first
        metadata = builderApiDeclare(declare);

        metadata.setUrl(String.format("%s%s", metadata.getHost(), metadata.getApiPath()));
        metadata.setMethod(method);
        if (!declare.parse().isInterface()) {
            metadata.setParse(declare.parse().newInstance());
        }
        if (!declare.afterHandler().isInterface()) {
            metadata.setAfterHandler(declare.afterHandler().newInstance());
        }
        metadata.setBusinessDescribe(declare.businessDescribe());
        metadata.setReturnType(method.getGenericReturnType());

        builderParameter(method, args, metadata);
        return metadata;
    }

    private void builderParameter(Method method, Object[] args, ApiMetadata metadata) throws Exception {
        StringBuilder param = new StringBuilder();
        List<Header> headerList = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            MethodParameter param2 = new SynthesizingMethodParameter(method, i);
            param2.initParameterNameDiscovery(DISCOVERER);
            PathVariable pathVariable = param2.getParameterAnnotation(PathVariable.class);
            if (pathVariable != null) {
                metadata.setUrl(metadata.getUrl().replaceAll(String.format("\\{%s\\}", pathVariable.name()), args[i].toString()));
            }

            RequestBody requestBody = param2.getParameterAnnotation(RequestBody.class);
            if (requestBody != null) {
                if (Strings.isNotEmpty(metadata.getBody())) {
                    throw new IllegalArgumentException(String.format("method %s RequestBody duplicate", method.getName()));
                }
                metadata.setBody(JSON.toJSONString(args[i]));
            }

            //a=1&b=23
            RequestParam requestParam = param2.getParameterAnnotation(RequestParam.class);
            if (requestParam != null) {
                param.append(String.format("%s=%s&", requestParam.name(), args[i]));
            }

            RequestHeader header = param2.getParameterAnnotation(RequestHeader.class);
            if (header != null) {
                if (Map.class.isAssignableFrom(param2.getParameterType())) {
                    Map<String, Object> paramMap = CollectionUtil.toMap2(args[i]);
                    for (Map.Entry<String, Object> hObj : paramMap.entrySet()) {
                        headerList.add(new BasicHeader(hObj.getKey(), String.valueOf(hObj.getValue())));
                    }
                } else if (Collection.class.isAssignableFrom(param2.getParameterType())) {
                    if (args[i] instanceof Header[]) {
                        headerList.addAll(Lists.newArrayList((Header[]) args[i]));
                    }
                } else {
                    headerList.add(new BasicHeader(header.name(), args[i].toString()));
                }
            }
        }
        if (!headerList.isEmpty()) {
            metadata.setHeaderList(headerList);
        }
        if (param.length() > 0) {
            metadata.setUrl(String.format("%s?%s", metadata.getUrl()
                    , StringUtils.removeEnd(param.toString(), "&")));
        }
    }

    private ApiMetadata builderApiDeclare(HummerRestApiDeclare declare) {
        ApiMetadata metadata;
        if (StringUtils.isNotEmpty(declare.apiName())) {
            metadata = ApiMetadata.builder()
                    .apiPath(PropertiesContainer.valueOfString(apiKeyFormat(declare.apiName()
                            , "api.path"), declare.apiPath()))
                    .host(PropertiesContainer.valueOfString(apiKeyFormat(declare.apiName()
                            , "api.host"), declare.apiPath()))
                    .timeOutMills(PropertiesContainer.valueOfInteger(apiKeyFormat(declare.apiName()
                            , "api.timeout.millis"), declare.timeOutMills()))
                    .retryCount(PropertiesContainer.valueOfInteger(apiKeyFormat(declare.apiName()
                            , "api.retry.count"), declare.retryCount()))
                    .enable(PropertiesContainer.valueOf(apiKeyFormat(declare.apiName()
                            , "api.enable"), Boolean.class, declare.enable()))
                    .async(PropertiesContainer.valueOf(apiKeyFormat(declare.apiName()
                            , "api.async"), Boolean.class, declare.async()))
                    .cacheTimeOutMills(PropertiesContainer.valueOfInteger(apiKeyFormat(declare.apiName()
                            , "api.timeout.millis"), declare.timeOutMills()))
                    .requestMethod(RequestMethod.valueOf(PropertiesContainer.valueOfString(apiKeyFormat(declare.apiName()
                            , "api.method"), declare.httpMethod()).toUpperCase()))
                    .build();
        } else {
            metadata = ApiMetadata.builder()
                    .apiPath(declare.apiPath())
                    .host(declare.host())
                    .timeOutMills(declare.timeOutMills())
                    .retryCount(declare.retryCount())
                    .enable(declare.enable())
                    .async(declare.async())
                    .requestMethod(RequestMethod.valueOf(declare.httpMethod().toUpperCase()))
                    .cacheTimeOutMills(declare.cacheTimeOutMills())
                    .build();
        }
        return metadata;
    }

    private String apiKeyFormat(String prefix, String key) {
        return String.format("%s.%s", prefix, key);
    }
}
