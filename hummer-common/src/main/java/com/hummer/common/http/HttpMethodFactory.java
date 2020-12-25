package com.hummer.common.http;

import com.google.common.collect.Maps;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.http.HttpMethod;

import java.util.Collection;
import java.util.Map;

public class HttpMethodFactory {
    private static final Map<HttpMethod, HttpRequestBase> methodMap = Maps.newHashMapWithExpectedSize(4);

    static {
        methodMap.put(HttpMethod.GET, new HttpGet());
        methodMap.put(HttpMethod.POST, new HttpPost());
        methodMap.put(HttpMethod.PUT, new HttpPut());
        methodMap.put(HttpMethod.DELETE, new HttpDelete());
    }

    private HttpMethodFactory() {

    }

    public static HttpRequestBase get(HttpMethod method) {
        return methodMap.get(method);
    }

    public static Collection<HttpMethod> allKeys() {
        return methodMap.keySet();
    }
}
