package com.hummer.common.http;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 14:32
 **/
public class HttpConstant {
    public static final String DEFAULT_GROUP_HTTP_ASYNC = "defaultGroupHttpAsyncClient";
    public static final String SEND_MESSAGE_HTTP_ASYNC_CLIENT = "sendMessageHttpAsyncClient";
    public static final int HTTP_MAX_TOTAL = 200;
    public static final int HTTP_PER_MAX_TOTAL = 20;
    public static final int HTTP_CONN_TIMEOUT = 3000;
    public static final int HTTP_CONN_SOCKET_TIMEOUT = 3000;
    public static final int HEAD_LOG_DATA_LENGTH = 1990;
    public static final int EXCEPTION_MESSAGE_MAX_LENGTH = 50000;
    public static final int TRACKED_LOG_HTTP_HEAD_MAX_LENGTH = 495;
    public static final String HTTPCLIENT_CONNTIMEOUTRETRY_ENABLE = "httpclient.connTimeoutRetry.enable";
    /**
     * HttpClientUtil Connection reset retry 重试开关 默认:true
     */
    public static final String HTTPCLIENT_CONNRESETRETRY_ENABLE = "httpclient.connResetRetry.enable";
    /**
     * 当前http请求参数缓存key
     **/
    public static final String REQUEST_OBJECT_CONTEXT_KEY = "cls_request_o";
    public static final String ENABLE_DEPENDENT_SOA_MONITOR_KEY = "enable-dependent-soa-monitor";
    public static final String ENABLE_DEPENDENT_CACHE_MONITOR_KEY = "enable-dependent-cache-monitor";

    private HttpConstant() {

    }
}
