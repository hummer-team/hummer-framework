package com.hummer.common.http.context;

import org.apache.http.client.config.RequestConfig;
import org.springframework.util.MultiValueMap;

import java.net.URI;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/12/16 17:01
 **/

public interface RequestContext {
    String getRequestId();
    String getMethod();
    String getQueryString();
    URI getURI();
    String getProtocol();
    RequestConfig getRequestConfig();
    String getCharacterEncoding();
    String getRequestURLString();
    MultiValueMap<String, String> getHeaders();
}
