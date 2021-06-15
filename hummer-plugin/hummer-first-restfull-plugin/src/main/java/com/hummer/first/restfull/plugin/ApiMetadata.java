package com.hummer.first.restfull.plugin;

import lombok.Builder;
import lombok.Data;
import org.apache.http.Header;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author lee
 */
@Builder
@Data
public class ApiMetadata {
    private String apiPath;
    private String host;
    private String httpMethod;
    private Integer timeOutMills;
    private Integer retryCount;
    private Boolean async;
    private List<Header> headerList;
    private String businessDescribe;
    private CustomParseRespProvider parse;
    private AfterHandlerProvider afterHandler;
    private String apiName;
    private Boolean enable;
    private Integer cacheTimeOutMills;
    private String body;
    private Method method;
    private String url;
    private RequestMethod requestMethod;
    private Type returnType;
}
