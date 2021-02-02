package com.hummer.rest.message.handle;

import org.springframework.http.HttpHeaders;


/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/24 14:22
 **/
public interface ResponseBodyHandle {
    /**
     * customer handle response logic
     *
     * @param responseBody body
     * @param httpHeaders  head
     * @return void
     * @author liguo
     * @date 2019/6/24 14:29
     * @version 1.0.0
     **/
    String handle(Object responseBody, HttpHeaders httpHeaders);
}
