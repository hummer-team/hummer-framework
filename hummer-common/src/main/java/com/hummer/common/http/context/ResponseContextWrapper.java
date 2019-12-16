package com.hummer.common.http.context;

import com.google.common.base.Charsets;
import org.apache.commons.collections.MapUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.validation.constraints.NotNull;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/12/16 17:47
 **/
public class ResponseContextWrapper implements ResponseContext {
    private HttpResponse response;
    private String requestId;

    public ResponseContextWrapper(final @NotNull HttpResponse response, final String requestId) {
        this.response = response;
        this.requestId = requestId;
    }

    @Override
    public String getCharacterEncoding() {
        return response.getEntity().getContentEncoding() != null ? response.getEntity().getContentEncoding().getValue()
                : Charsets.UTF_8.name();
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public String getResponseId() {
        return requestId;
    }

    @Override
    public int getHttpStatus() {
        return response.getStatusLine().getStatusCode();
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        for (Header header : response.getAllHeaders()) {
            headers.add(header.getName(), header.getValue());
        }
        return headers;
    }

    @Override
    public boolean getHasGizp() {
        MultiValueMap<String, String> headerMap = getHeaders();
        if(MapUtils.isEmpty(headerMap)){
            return false;
        }
        String key = "Content-Encoding";
        if (headerMap.containsKey(key)) {
            return headerMap.get(key).stream().anyMatch("gzip"::equalsIgnoreCase);
        }

        return false;
    }
}
