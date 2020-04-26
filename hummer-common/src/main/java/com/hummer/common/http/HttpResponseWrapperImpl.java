package com.hummer.common.http;

import com.google.common.base.Charsets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;

@Slf4j
public class HttpResponseWrapperImpl implements HttpResponseWrapper {

    private String id;

    private HttpResponse response;

    private MultiValueMap<String, String> headers;

    public HttpResponseWrapperImpl(String requestId, HttpResponse response) {
        this.id = requestId;
        this.response = response;
    }

    @Override
    public byte[] toByteArray() {
        byte[] result = null;
        try {
            BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
            response.setEntity(entity);
            result = IOUtils.toByteArray(entity.getContent());
        } catch (Exception e) {
            log.error("Client get response failed：toByteArray", e);
        }
        return result;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCharacterEncoding() {
        return response.getEntity().getContentEncoding() != null ? response.getEntity().getContentEncoding().getValue()
                : Charsets.UTF_8.name();
    }

    @Override
    public int getStatus() {
        return response.getStatusLine().getStatusCode();
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        if (headers == null) {
            headers = new LinkedMultiValueMap<>();
            Arrays.stream(response.getAllHeaders()).forEach(p -> {
                headers.add(p.getName(), p.getValue());
            });
        }
        return headers;
    }

    @Override
    public boolean getHasGizp() {
        try {
            MultiValueMap<String, String> headerMap = getHeaders();
            String key = "Content-Encoding";
            if (headerMap.containsKey(key)) {
                return headerMap.get(key).stream().anyMatch(p -> p.equalsIgnoreCase("gzip"));
            }
        } catch (Exception e) {
            log.error("Client Log getHasGizp error:", e);
        }
        return false;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Header[] getAllHeaders() {
        return response.getAllHeaders();
    }
}


