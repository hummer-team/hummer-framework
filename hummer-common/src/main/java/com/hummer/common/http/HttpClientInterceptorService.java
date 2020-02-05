package com.hummer.common.http;

import com.hummer.common.http.context.RequestContext;
import com.hummer.common.http.context.ResponseContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/12/16 18:16
 **/
@Slf4j
public class HttpClientInterceptorService implements HttpClientInterceptor {
    /**
     * request service before callback
     *
     * @param httpRequest
     * @author liguo
     */
    @Override
    public void before(RequestContext httpRequest) {

    }

    /**
     * request service end of callback
     *
     * @param httpRequest
     * @param httpResponse
     * @author liguo
     */
    @Override
    public void after(RequestContext httpRequest, ResponseContext httpResponse) {

    }

    /**
     * request service exception callback
     *
     * @param httpRequest
     * @param ex
     * @author liguo
     */
    @Override
    public void throwing(RequestContext httpRequest, Throwable ex) {

    }

    @Override
    public int order() {
        return 0;
    }
}
