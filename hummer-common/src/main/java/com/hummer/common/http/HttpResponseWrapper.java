package com.hummer.common.http;

import org.apache.http.Header;
import org.springframework.util.MultiValueMap;

public interface HttpResponseWrapper {

    String getCharacterEncoding();

    byte[] toByteArray();

    String getId();

    int getStatus();

    MultiValueMap<String, String> getHeaders();

    boolean getHasGizp();

    Header[] getAllHeaders();

}
