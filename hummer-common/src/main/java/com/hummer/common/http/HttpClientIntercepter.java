package com.hummer.common.http;

public interface HttpClientIntercepter extends Comparable<HttpClientIntercepter>{

    void before(HttpRequestWrapper httpRequest);

    void after(HttpRequestWrapper httpRequest, HttpResponseWrapper httpResponse);

    void afterThrowing(HttpRequestWrapper httpRequest, Exception ex);

    Integer getOrder();
}
