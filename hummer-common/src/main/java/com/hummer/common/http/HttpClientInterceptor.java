package com.hummer.common.http;


import com.hummer.common.http.context.RequestContext;
import com.hummer.common.http.context.ResponseContext;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/12/16 17:01
 **/
public interface HttpClientInterceptor extends Comparable<HttpClientInterceptor>{
    /**
     * request service before callback
     *
     * @author liguo
     **/
    void before(RequestContext httpRequest);

    /**
     * request service end of callback
     *
     * @author liguo
     **/
    void after(RequestContext httpRequest, ResponseContext httpResponse);

    /**
     * request service exception callback
     *
     * @author liguo
     **/
    void throwing(RequestContext httpRequest, Throwable ex);

    int order();
}
