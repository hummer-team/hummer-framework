package com.hummer.common.http.context;

import org.springframework.util.MultiValueMap;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/12/16 17:03
 **/
public interface ResponseContext {
    String getCharacterEncoding();
    byte[] toByteArray();
    String getResponseId();
    int getHttpStatus();
    MultiValueMap<String, String> getHeaders();
    boolean getHasGizp();
}
