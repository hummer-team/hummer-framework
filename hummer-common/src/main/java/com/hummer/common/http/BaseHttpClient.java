package com.hummer.common.http;

import com.hummer.common.SysConstant;
import com.hummer.common.http.context.RequestContextWrapper;
import com.hummer.common.http.context.ResponseContextWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/12/16 18:19
 **/
public class BaseHttpClient {
    private static List<HttpClientInterceptor> httpClientIntercepters = new ArrayList<>();

    static {

        ServiceLoader<HttpClientInterceptor> slHttpClientLogHandler = ServiceLoader.load(HttpClientInterceptor.class);
        for (HttpClientInterceptor filter : slHttpClientLogHandler) {
            if(!filter.getClass().equals(HttpClientInterceptorService.class)) {
                httpClientIntercepters.add(filter);
            }
        }
        if(CollectionUtils.isNotEmpty(httpClientIntercepters)) {
            httpClientIntercepters.sort(Comparator.comparing(HttpClientInterceptor::order));
        }
    }

    protected List<HttpClientInterceptor> httpClientIntercepters() {
        return httpClientIntercepters;
    }

    protected void beforeHandle(HttpRequestBase httpRequestBase) {
        if (CollectionUtils.isEmpty(httpClientIntercepters())) {
            return;
        }
        httpClientIntercepters()
                .forEach(c -> c.before(new RequestContextWrapper(httpRequestBase
                        , httpRequestBase.getFirstHeader(SysConstant.REQUEST_ID).getValue())));
    }

    protected void afterHandle(HttpRequestBase httpRequestBase, HttpResponse reqponse) {
        if (CollectionUtils.isEmpty(httpClientIntercepters())) {
            return;
        }
        httpClientIntercepters()
                .forEach(c -> c.after(
                        new RequestContextWrapper(httpRequestBase
                                , httpRequestBase.getFirstHeader(SysConstant.REQUEST_ID).getValue())
                        , new ResponseContextWrapper(reqponse
                                , reqponse.getFirstHeader(SysConstant.REQUEST_ID).getValue())));
    }

    protected void throwHandle(HttpRequestBase httpRequestBase, Throwable e) {
        if (CollectionUtils.isEmpty(httpClientIntercepters())) {
            return;
        }
        httpClientIntercepters()
                .forEach(c -> c.throwing(new RequestContextWrapper(httpRequestBase
                                , httpRequestBase.getFirstHeader(SysConstant.REQUEST_ID).getValue())
                        , e));
    }
}
