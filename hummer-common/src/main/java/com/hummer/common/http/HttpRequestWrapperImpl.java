package com.hummer.common.http;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Map;

@Slf4j
public class HttpRequestWrapperImpl implements HttpRequestWrapper {

    private static final String CONTENT_TYPE = "Content-Type";

    private static final String[] BINARY_TYPES_PREFIX = {"image", "video", "audio"};

    private static final String MULTIPART_FORM_DATA_VALUE = "multipart/form-data";

    private String id;

    private HttpRequestBase request;

    private byte[] result;

    private Map<String, Object> paramData;

    private MultiValueMap<String, String> headers;


    public HttpRequestWrapperImpl(String requestId, HttpRequestBase request) {
        this.request = request;
        this.id = requestId;
        this.paramData = Maps.newHashMap();
    }


    @Override
    public RequestConfig getRequestConfig() {
        return request.getConfig();
    }


    public byte[] toByteArray() {
        try {
            if (request instanceof HttpEntityEnclosingRequestBase) {
                result = IOUtils.toByteArray(((HttpEntityEnclosingRequestBase) request).getEntity().getContent());
            }
        } catch (IOException e) {
            log.error("rest client log ClientRequestWrapper error:", e);
        }

        return result;
    }

    @Override
    public String getContentTypeString() {
        try {
            return request.getFirstHeader(CONTENT_TYPE).getValue();
        } catch (Exception e) {
            return "none";
        }
    }

    @Override
    public String getMethod() {
        try {
            return request.getMethod();
        } catch (Exception e) {
            return "none";
        }
    }


    @Override
    public String getQueryString() {
        return request.getURI().getQuery();
    }

    @Override
    public String getProtocol() {
        try {
            return request.getProtocolVersion().getProtocol();
        } catch (Exception e) {
            return "none";
        }
    }

    @Override
    public String getCharacterEncoding() {
        try {
            if (request instanceof HttpEntityEnclosingRequestBase) {
                Header header = ((HttpEntityEnclosingRequestBase) request).getEntity().getContentEncoding();
                return (header == null) ? null : header.getValue();
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    @Override
    public String getRequestURLString() {
        return request.getURI().toString();
    }

    @Override
    public MultiValueMap<String, String> getHeaders() {
        if (headers == null) {
            Header[] headerMap = request.getAllHeaders();
            headers = new LinkedMultiValueMap<>();
            Arrays.stream(headerMap).forEach(p -> {
                headers.add(p.getName(), p.getValue());
            });
        }
        return headers;
    }

    @Override
    public boolean isBinaryContent() {
        if (!(request instanceof HttpEntityEnclosingRequestBase)) {
            return true;
        }

        String contentType = getContentTypeString();

        if (contentType == null) {
            return true;
        }

        if (!contentType.toLowerCase().contains("json")) {
            return true;
        }

        for (String binaryTypePrefix : BINARY_TYPES_PREFIX) {
            if (contentType.startsWith(binaryTypePrefix)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isMultipart() {
        String contentType = getContentTypeString();
        return contentType != null && contentType.startsWith(MULTIPART_FORM_DATA_VALUE);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(toByteArray());
    }


    @Override
    public void setHeader(String name, String value) {
        request.setHeader(name, value);
    }

    @Override
    public URI getURI() {
        return request.getURI();
    }

    @Override
    public Map<String, Object> getAllParamData() {
        return paramData;
    }

    @Override
    public void addParamData(String key, Object data) {
        if(paramData == null) {
            paramData = Maps.newHashMap();
        }
        paramData.put(key, data);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HttpRequestBase getRequest() {
        return request;
    }
}

