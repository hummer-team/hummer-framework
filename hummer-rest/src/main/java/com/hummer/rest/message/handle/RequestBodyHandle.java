package com.hummer.rest.message.handle;

import org.springframework.http.HttpHeaders;

import java.io.InputStream;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/24 14:19
 **/
public interface RequestBodyHandle {
    /**
     * customer handle request body.
     *
     * @param requestBody client request body.
     * @param httpHeaders http headers.
     * @return java.lang.String return parse json string
     * @author liguo
     * @date 2019/6/24 14:23
     * @version 1.0.0
     **/
    String handle(InputStream requestBody, HttpHeaders httpHeaders);
}
