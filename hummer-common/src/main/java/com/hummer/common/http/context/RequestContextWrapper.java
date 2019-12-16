package com.hummer.common.http.context;

import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Optional;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/12/16 17:15
 **/
public class RequestContextWrapper implements RequestContext {
    private HttpRequestBase request;
    private String requestId;

    public RequestContextWrapper(final @NotNull HttpRequestBase request
            , String requestId) {
        this.request = request;
        this.requestId = requestId;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getQueryString() {
        String[] queryString = new String[0];
        Optional.ofNullable(request.getURI()).ifPresent(r -> queryString[0] = r.getQuery());
        return queryString[0];
    }

    @Override
    public URI getURI() {
        return request.getURI();
    }

    @Override
    public String getProtocol() {
        String[] protocol = new String[0];
        Optional.ofNullable(request
                .getProtocolVersion()).ifPresent(r -> protocol[0] = r.getProtocol());
        return protocol[0];
    }

    @Override
    public RequestConfig getRequestConfig() {
        return request.getConfig();
    }

    @Override
    public String getCharacterEncoding() {
        if (request instanceof HttpEntityEnclosingRequestBase) {
            Header header = ((HttpEntityEnclosingRequestBase) request).getEntity().getContentEncoding();
            return (header == null) ? null : header.getValue();
        }
        return null;
    }

    @Override
    public String getRequestURLString() {
        String[] urlString = new String[0];
        Optional.ofNullable(request.getURI()).ifPresent(r -> urlString[0] = r.toString());
        return urlString[0];
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        LinkedMultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        Header[] headers = request.getAllHeaders();
        if (headers == null || headers.length == 0) {
            return headerMap;
        }
        for (Header header : headers) {
            headerMap.add(header.getName(), header.getValue());
        }
        return headerMap;
    }
}
