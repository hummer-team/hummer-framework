package com.hummer.common.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/20 15:03
 **/
public class HttpInternalUtil {

    private HttpInternalUtil(){

    }


    /**
     * 获取http请求结果
     *
     * @return org.apache.http.HttpEntity
     * @throws ClientProtocolException
     * @throws HttpResponseException
     * @throws NullPointerException
     * @author liguo
     * @Date 2018/11/29 17:23
     * @version
     * @Param response
     **/
    static HttpEntity getHttpEntity(HttpResponse response)
            throws ClientProtocolException
            , HttpResponseException {

        return getHttpEntity(response, 200, 204, 201);
    }

    /**
     * 获取http请求结果。
     *
     * @param matchStatusCode 要匹配的状态码
     * @return org.apache.http.HttpEntity
     * @throws ClientProtocolException
     * @throws HttpResponseException
     * @throws NullPointerException
     * @implNote 如果状态码不匹配则抛出异常
     * @author liguo
     * @Date 2018/11/29 17:24
     * @version
     * @Param response http response 实体
     **/
    static HttpEntity getHttpEntity(HttpResponse response, Integer... matchStatusCode)
            throws ClientProtocolException, HttpResponseException {

        if (response == null) {
            throw new NullPointerException("response is null.");
        }

        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (matchStatusCode != null) {
            int code = statusLine.getStatusCode();
            if (Arrays.stream(matchStatusCode).noneMatch(c -> c != code)) {
                throw new HttpResponseException(
                        statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }
        }
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }
        return entity;
    }

    /**
     * @return org.apache.http.client.methods.HttpRequestBase
     * @author liguo
     * @Date 2018/11/29 17:47
     * @version
     * @Param [url, requestMethod]
     **/
    static HttpRequestBase getHttpRequestV2(final String url, final RequestMethod requestMethod) {
        HttpRequestBase httpRequest;
        switch (requestMethod) {
            case POST:
                httpRequest = new HttpPost(url);
                break;
            case PUT:
                httpRequest = new HttpPut(url);
                break;
            case DELETE:
                httpRequest = new HttpDelete(url);
                break;
            case GET:
                httpRequest = new HttpGet(url);
                break;
            default:
                httpRequest = new HttpPost(url);
        }
        return httpRequest;
    }

    /**
     * 获取http method name
     *
     * @param url
     * @param requestMethod
     * @return org.apache.http.client.methods.HttpEntityEnclosingRequestBase
     * @author lee
     * @Date 2018/11/5 17:52
     **/
    static HttpEntityEnclosingRequestBase getHttpRequest(String url, RequestMethod requestMethod) {
        HttpEntityEnclosingRequestBase httpRequest;
        switch (requestMethod) {
            case POST:
                httpRequest = new HttpPost(url);
                break;
            case PUT:
                httpRequest = new HttpPut(url);
                break;
            case DELETE:
                httpRequest = new HttpDelete(url);
                break;
            default:
                httpRequest = new HttpPost(url);
        }
        return httpRequest;
    }
}
