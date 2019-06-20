package com.hummer.support.http;

import com.google.common.base.Strings;
import org.apache.http.Header;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collection;

/**
 * http request custom config settings
 *
 * @author liguo.
 * @date 2018/11/5.
 */
public class RequestCustomConfig<T> {
    private String url;
    private RequestMethod method;
    private Integer socketTimeOutMillisecond = 3000;
    private Integer connectTimeOutMillisecond = 5000;
    private Integer connectionRequestTimeout = 5000;
    private int retryCount = 1;
    private int retrySleepMillisecond = 1000;
    private Collection<Header> headers;
    private T requestBody;
    private boolean callSuccessOutPutBody = false;
    private String respEncoding;

    private RequestCustomConfig() {

    }

    public static <T> RequestCustomConfig<T> builder() {
        return new RequestCustomConfig<>();
    }

    public RequestCustomConfig setCallSuccessOutPutBody(boolean callSuccessOutPutBody) {
        this.callSuccessOutPutBody = callSuccessOutPutBody;
        return this;
    }

    public RequestCustomConfig setRequestBody(T requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    public RequestCustomConfig setRetryCount(int retryCount) {
        this.retryCount = retryCount;
        return this;
    }

    public String getRespEncoding() {
        return respEncoding;
    }

    public RequestCustomConfig setRespEncoding(String respEncoding) {
        this.respEncoding = respEncoding;
        return this;
    }

    public RequestCustomConfig setRetrySleepMillisecond(int retrySleepMillisecond) {
        this.retrySleepMillisecond = retrySleepMillisecond;
        return this;
    }

    public RequestCustomConfig setUrl(String url) {
        this.url = url;
        return this;
    }

    public RequestCustomConfig setMethod(RequestMethod method) {
        this.method = method;
        return this;
    }

    public RequestCustomConfig setSocketTimeOutMillisecond(Integer socketTimeOutMillisecond) {
        this.socketTimeOutMillisecond = socketTimeOutMillisecond;
        return this;
    }

    public RequestCustomConfig setConnectTimeOutMillisecond(Integer connectTimeOutMillisecond) {
        this.connectTimeOutMillisecond = connectTimeOutMillisecond;
        return this;
    }

    public RequestCustomConfig setConnectionRequestTimeout(Integer connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
        return this;
    }

    public RequestCustomConfig setHeaders(Collection<Header> headers) {
        this.headers = headers;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public Integer getSocketTimeOutMillisecond() {
        return socketTimeOutMillisecond;
    }

    public Integer getConnectTimeOutMillisecond() {
        return connectTimeOutMillisecond;
    }

    public Integer getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public Collection<Header> getHeaders() {
        return headers;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getRetrySleepMillisecond() {
        return retrySleepMillisecond;
    }

    public T getRequestBody() {
        return requestBody;
    }

    public boolean isCallSuccessOutPutBody() {
        return callSuccessOutPutBody;
    }

    public RequestCustomConfig<T> build() {
        if (Strings.isNullOrEmpty(getUrl())) {
            throw new NullPointerException("target url is null.");
        }
        return this;
    }

    @Override
    public String toString() {
        return "RequestCustomConfig{" +
                "url='" + url + '\'' +
                ", method=" + method +
                ", socketTimeOutMillisecond=" + socketTimeOutMillisecond +
                ", connectTimeOutMillisecond=" + connectTimeOutMillisecond +
                ", connectionRequestTimeout=" + connectionRequestTimeout +
                ", retryCount=" + retryCount +
                ", retrySleepMillisecond=" + retrySleepMillisecond +
                ", headers=" + headers +
                ", callSuccessOutPutBody=" + callSuccessOutPutBody +
                '}';
    }
}
