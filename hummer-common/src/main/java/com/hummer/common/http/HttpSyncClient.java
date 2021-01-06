package com.hummer.common.http;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import com.hummer.common.coder.CoderEnum;
import com.hummer.common.exceptions.AppException;
import com.hummer.common.exceptions.SysException;
import com.hummer.common.http.context.MessageTypeContext;
import com.hummer.common.http.context.RequestContext;
import com.hummer.common.http.context.RequestContextWrapper;
import com.hummer.common.http.context.ResponseContext;
import com.hummer.common.http.context.ResponseContextWrapper;
import com.hummer.core.PropertiesContainer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.hummer.common.SysConstant.DEFAULT_CHARSET_NAME;
import static com.hummer.common.SysConstant.HEADER_REQ_TIME;
import static com.hummer.common.SysConstant.REQUEST_ID;
import static com.hummer.common.SysConstant.SYS_ERROR_CODE;
import static com.hummer.common.http.HttpConstant.HTTPCLIENT_CONNRESETRETRY_ENABLE;
import static com.hummer.common.http.HttpConstant.HTTPCLIENT_CONNTIMEOUTRETRY_ENABLE;
import static com.hummer.common.http.HttpConstant.HTTP_CONN_SOCKET_TIMEOUT;
import static com.hummer.common.http.HttpConstant.HTTP_CONN_TIMEOUT;

/**
 * http client sync wrapper
 */
@Slf4j
public class HttpSyncClient {
    private static final String USER_AGENT = "user_agent";
    private static final String PANLI_IBJ = "panli";

    private static volatile CloseableHttpClient httpClient;

    private static List<HttpClientHandler> httpClientHandlers = new ArrayList<>();
    private static List<HttpClientInterceptor> HttpClientInterceptors = new ArrayList<>();
    private static RequestConfig requestConfig = null;

    static {

        ServiceLoader<HttpClientHandler> slHttpClientHandler = ServiceLoader.load(HttpClientHandler.class);
        for (HttpClientHandler filter : slHttpClientHandler) {
            httpClientHandlers.add(filter);
        }

        ServiceLoader<HttpClientInterceptor> slHttpClientLogHandler = ServiceLoader.load(HttpClientInterceptor.class);
        for (HttpClientInterceptor filter : slHttpClientLogHandler) {
            HttpClientInterceptors.add(filter);
        }

        Collections.sort(HttpClientInterceptors);
    }

    private HttpSyncClient() {
    }

    /**
     * this is single instance and setting properties
     *
     * @return
     */
    public static CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (CloseableHttpClient.class) {
                if (httpClient == null) {
                    ConnectionSocketFactory plainsf = PlainConnectionSocketFactory.getSocketFactory();
                    Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                            .<ConnectionSocketFactory>create()
                            .register("http", plainsf)
                            .register("https", SSLConnectionSocketFactory.getSystemSocketFactory())
                            .build();
                    HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connFactory
                            = new ManagedHttpClientConnectionFactory(
                            DefaultHttpRequestWriterFactory.INSTANCE, DefaultHttpResponseParserFactory.INSTANCE);
                    DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
                    PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(
                            socketFactoryRegistry, connFactory, dnsResolver);

                    SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
                    connManager.setDefaultSocketConfig(defaultSocketConfig);

                    connManager
                            .setMaxTotal(PropertiesContainer.valueOf("httpclient.conn.maxTotal"
                                    , Integer.class, 1000));
                    connManager.setDefaultMaxPerRoute(
                            PropertiesContainer.valueOf("httpclient.conn.maxPerRoute"
                                    , Integer.class, 500));
                    connManager.setValidateAfterInactivity(1000);

                    requestConfig = RequestConfig.custom()
                            .setSocketTimeout(PropertiesContainer.valueOf("httpclient.socketTimeout"
                                    , Integer.class
                                    , HTTP_CONN_SOCKET_TIMEOUT))
                            .setConnectTimeout(
                                    PropertiesContainer.valueOf("httpclient.connectTimeout"
                                            , Integer.class
                                            , HTTP_CONN_TIMEOUT))
                            .setConnectionRequestTimeout(PropertiesContainer.valueOf("httpclient.connectionRequestTimeout"
                                    , Integer.class
                                    , HTTP_CONN_TIMEOUT))
                            .build();

                    ExceptionRetryHandler retryHandler = new ExceptionRetryHandler(10,
                            PropertiesContainer.valueOf(HTTPCLIENT_CONNRESETRETRY_ENABLE,
                                    Boolean.class, true),
                            PropertiesContainer.valueOf(HTTPCLIENT_CONNTIMEOUTRETRY_ENABLE,
                                    Boolean.class, false));

                    HttpClientBuilder httpClientBuilder = HttpClients
                            .custom()
                            .setConnectionManager(connManager)
                            .setConnectionManagerShared(false)
                            .evictExpiredConnections()
                            .evictIdleConnections(10, TimeUnit.SECONDS)
                            .setDefaultRequestConfig(requestConfig)
                            .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                            .setKeepAliveStrategy(new CustomConnectionKeepAliveStrategy())
                            .setRetryHandler(retryHandler);

                    httpClient = httpClientBuilder.build();

                    Thread closeThread = new IdleConnectionMonitorThread(connManager);
                    closeThread.setDaemon(true);
                    closeThread.start();
                }
            }
        }

        return httpClient;
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param jsonStr json string
     */
    public static String sendHttpPost(String httpUrl, String jsonStr, Header... headers) {
        return sendHttpPost(httpUrl, jsonStr, 0L, null, headers);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param jsonStr json string
     */
    public static String sendHttpPostByRetry(String httpUrl, String jsonStr, int retryCount, Header... headers) {
        return sendHttpPostByRetry(httpUrl, jsonStr, 0L, null, retryCount, headers);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     */
    public static String sendHttpPost(String httpUrl, long timeout, TimeUnit timeUnit, Header... header) {
        HttpPost httpPost = new HttpPost(httpUrl);
        if (header != null) {
            httpPost.setHeaders(header);
        }
        return sendHttpPost(httpPost, timeout, timeUnit);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     */
    public static String sendHttpPostByRetry(String httpUrl, long timeout, TimeUnit timeUnit, int retryCount,
                                             Header... header) {
        HttpPost httpPost = new HttpPost(httpUrl);
        if (header != null) {
            httpPost.setHeaders(header);
        }
        return sendHttpPostByRetry(httpPost, timeout, timeUnit, retryCount);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     */
    public static String sendHttpPost(String httpUrl, Header... headers) {
        return sendHttpPost(httpUrl, 0L, null, headers);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     */
    public static String sendHttpPostByRetry(String httpUrl, int retyCount, Header... headers) {
        return sendHttpPostByRetry(httpUrl, 0L, null, retyCount, headers);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param jsonStr json string
     */
    public static String sendHttpPost(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit,
                                      Header... header) {
        return sendHttpPostByRetry(httpUrl, jsonStr, timeout, timeUnit, 0, header);
    }

    public static <T> void sendByRetry(@NotNull String httpUrl
            , @NotNull HttpMethod method
            , @NotNull long timeoutMs
            , @NotNull int retryCount
            , Header... header) {
        sendByRetryOf(httpUrl, null, null, method, timeoutMs, retryCount, header);
    }

    public static <T> void sendByRetry(@NotNull String httpUrl
            , @NotNull T body
            , @NotNull HttpMethod method
            , @NotNull long timeoutMs
            , @NotNull int retryCount
            , Header... header) {
        sendByRetryOf(httpUrl, body, null, method, timeoutMs, retryCount, header);
    }

    public static <T, R> R sendByRetry(@NotNull String httpUrl
            , @NotNull T body
            , @NotNull MessageTypeContext<R> type
            , @NotNull HttpMethod method
            , @NotNull long timeoutMs
            , @NotNull int retryCount
            , Header... header) {

        return sendByRetryOf(httpUrl, body, type, method, timeoutMs, retryCount, header);
    }

    @SneakyThrows
    private static <T, R> R sendByRetryOf(@NotNull String httpUrl
            , T body
            , MessageTypeContext<R> type
            , @NotNull HttpMethod method
            , @NotNull long timeoutMs
            , @NotNull int retryCount
            , Header... header) {
        CoderEnum coder = CoderEnum.getCoderByName(PropertiesContainer.valueOfString("hummer.http.message.coder"
                , "fast_json"));
        HttpRequestBase httpReqAction = HttpMethodFactory.get(method);
        Assert.notNull(httpReqAction, "ony support " + HttpMethodFactory.allKeys());
        httpReqAction.setURI(URI.create(httpUrl));
        if (body != null && httpReqAction instanceof HttpEntityEnclosingRequestBase) {
            switch (coder) {
                case FAST_JSON:
                    StringEntity fastJsonEntry = new StringEntity(JSON.toJSONString(body));
                    ((HttpEntityEnclosingRequestBase) httpReqAction).setEntity(fastJsonEntry);
                    fastJsonEntry.setContentType(coder.getMediaType().toString());
                    break;
                case MSG_PACK_BINARY:
                    ByteArrayEntity packBinaryEntity = new ByteArrayEntity(coder.encodeWithBinary(body));
                    ((HttpEntityEnclosingRequestBase) httpReqAction).setEntity(packBinaryEntity);
                    packBinaryEntity.setContentType(coder.getMediaType().toString());
                    break;
                case MSG_PACK_JSON:
                    ByteArrayEntity packJsonEntity = new ByteArrayEntity(coder.encodeWithJson(body));
                    ((HttpEntityEnclosingRequestBase) httpReqAction).setEntity(packJsonEntity);
                    packJsonEntity.setContentType(coder.getMediaType().toString());
                    break;
                case PROTOSTUFF_BINARY:
                    ByteArrayEntity protostuffBinaryEntity = new ByteArrayEntity(coder.encodeWithBinary(body));
                    ((HttpEntityEnclosingRequestBase) httpReqAction).setEntity(protostuffBinaryEntity);
                    protostuffBinaryEntity.setContentType(coder.getMediaType().toString());
                    break;
                case PROTOSTUFF_JSON:
                    ByteArrayEntity protostuffJsonEntity = new ByteArrayEntity(coder.encodeWithJson(body));
                    ((HttpEntityEnclosingRequestBase) httpReqAction).setEntity(protostuffJsonEntity);
                    protostuffJsonEntity.setContentType(coder.getMediaType().toString());
                    break;
                default:
                    throw new SysException(SYS_ERROR_CODE, "not support " + coder);
            }
        }
        if (header != null) {
            httpReqAction.setHeaders(header);
        }
        httpReqAction.setHeader("Accept", coder.getMediaType().toString());
        HttpResult result = execute2ResultByRetry(httpReqAction
                , timeoutMs
                , TimeUnit.MILLISECONDS
                , retryCount
                , true);
        if (type == null) {
            closeResources(null, httpReqAction);
            return null;
        }
        R r = parseResp(httpUrl, type, coder, result);
        closeResources(null, httpReqAction);
        return r;
    }

    @SuppressWarnings("unchecked")
    private static <R> R parseResp(String httpUrl
            , MessageTypeContext<R> type
            , CoderEnum coder
            , HttpResult result) {
        if (result == null || result.getHttpResponse() == null) {
            throw new SysException("no http response.");
        }
        try {
            switch (coder) {
                case FAST_JSON:
                    return JSON.parseObject(result.getHttpResponse().getEntity().getContent()
                            , type.getType());
                case MSG_PACK_BINARY:
                    return (R) coder.decodeWithBinary(
                            ByteStreams.toByteArray(result.getHttpResponse().getEntity().getContent())
                            , type.getTypeRef());
                case MSG_PACK_JSON:
                    return (R) coder.decodeWithJson(ByteStreams.toByteArray(
                            result.getHttpResponse().getEntity().getContent())
                            , type.getTypeRef());
                case PROTOSTUFF_BINARY:
                    return (R) coder.decodeWithBinary(ByteStreams.toByteArray(
                            result.getHttpResponse().getEntity().getContent())
                            , type.getClassType());
                case PROTOSTUFF_JSON:
                    return (R) coder.decodeWithJson(ByteStreams.toByteArray(
                            result.getHttpResponse().getEntity().getContent())
                            , type.getClassType());
                default:
                    throw new IllegalArgumentException("invalid coder " + coder);
            }
        } catch (IOException e) {
            String msg = String.format("read %s resp body stream error ", httpUrl);
            log.warn(msg, e);
            throw new SysException(SYS_ERROR_CODE, msg, e);
        } finally {
            try {
                result.getHttpResponse().getEntity().getContent().close();
            } catch (IOException e) {
                //ignore
            }
        }
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param jsonStr json string
     */
    public static String sendHttpPostByRetry(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit,
                                             int retryCount, Header... header) {
        HttpPost httpPost = new HttpPost(httpUrl);
        try {
            StringEntity stringEntity = new StringEntity(jsonStr, "UTF-8");
            stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            httpPost.setEntity(stringEntity);
            if (header != null) {
                httpPost.setHeaders(header);
            }
        } catch (Exception e) {
            throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
        }
        return sendHttpPostByRetry(httpPost, timeout, timeUnit, retryCount);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param maps    param map
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> maps, Header... headers) {
        return sendHttpPost(httpUrl, maps, 0L, null, headers);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param maps    param map
     */
    public static String sendHttpPostByRetry(String httpUrl, Map<String, String> maps, int retryCount,
                                             Header... headers) {
        return sendHttpPostByRetry(httpUrl, maps, 0L, null, retryCount, headers);
    }

    /**
     * send post
     *
     * @param httpUrl    target url
     * @param parameters param map
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> parameters, long timeout, TimeUnit timeUnit,
                                      Header... header) {
        return sendHttpPostByRetry(httpUrl, parameters, timeout, timeUnit, 0, header);
    }

    /**
     * send post with from data
     *
     * @param httpUrl
     * @param nameValuePairs
     * @param timeout
     * @param timeUnit
     * @param retryCount
     * @param header
     * @return
     */
    public static String sendHttpPostByRetry(String httpUrl, List<NameValuePair> nameValuePairs
            , long timeout
            , TimeUnit timeUnit
            , int retryCount
            , Header... header) {
        HttpPost httpPost = new HttpPost(httpUrl);
        try {
            if (header != null) {
                httpPost.setHeaders(header);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
        }
        return sendHttpPostByRetry(httpPost, timeout, timeUnit, retryCount);
    }

    /**
     * send post
     *
     * @param httpUrl    target url
     * @param parameters param map
     */
    public static String sendHttpPostByRetry(String httpUrl, Map<String, String> parameters, long timeout,
                                             TimeUnit timeUnit, int retryCount, Header... header) {
        //
        HttpPost httpPost = new HttpPost(httpUrl);
        //
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), parameters.get(entry.getKey())));
        }
        try {
            if (header != null) {
                httpPost.setHeaders(header);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
        } catch (Exception e) {
            throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
        }
        return sendHttpPostByRetry(httpPost, timeout, timeUnit, retryCount);
    }

    /**
     * send post
     *
     * @param httpUrl    target url
     * @param parameters param map
     */
    public static String sendHttpPost(String httpUrl, Map<String, String> parameters) {
        return sendHttpPost(httpUrl, parameters, 0L, null);
    }

    /**
     * send post
     *
     * @param httpUrl    target url
     * @param parameters param map
     */
    public static String sendHttpPostByRetry(String httpUrl, Map<String, String> parameters, int retryCount) {
        return sendHttpPostByRetry(httpUrl, parameters, 0L, null, retryCount);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param maps    param map
     */
    public static String sendHttpPut(String httpUrl, Map<String, String> maps, Header... headers) {
        return sendHttpPut(httpUrl, maps, 0L, null, headers);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param maps    param map
     */
    public static String sendHttpPutByRetry(String httpUrl, Map<String, String> maps, int retryCount,
                                            Header... headers) {
        return sendHttpPutByRetry(httpUrl, maps, 0L, null, retryCount, headers);
    }

    /**
     * send post
     *
     * @param httpUrl    target url
     * @param parameters param map
     */
    public static String sendHttpPut(String httpUrl, Map<String, String> parameters, long timeout, TimeUnit timeUnit,
                                     Header... headers) {
        return sendHttpPutByRetry(httpUrl, parameters, timeout, timeUnit, 0, headers);
    }

    /**
     * send post
     *
     * @param httpUrl    target url
     * @param parameters param map
     */
    public static String sendHttpPutByRetry(String httpUrl, Map<String, String> parameters, long timeout,
                                            TimeUnit timeUnit, int retryCount, Header... headers) {
        HttpPut httpPut = new HttpPut(httpUrl);
        if (headers != null) {
            httpPut.setHeaders(headers);
        }

        List<NameValuePair> nameValuePairs = new ArrayList<>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            nameValuePairs.add(new BasicNameValuePair(entry.getKey(), parameters.get(entry.getKey())));
        }
        try {
            httpPut.setEntity(new UrlEncodedFormEntity(nameValuePairs, DEFAULT_CHARSET_NAME));
        } catch (Exception e) {
            throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
        }
        return sendHttpRequestByRetry(httpPut, timeout, timeUnit, retryCount);
    }

    /**
     * send put
     *
     * @param httpUrl target url
     * @param jsonStr json string
     */
    public static String sendHttpPut(String httpUrl, String jsonStr, Header... header) {
        return sendHttpPut(httpUrl, jsonStr, 0L, null, header);
    }

    /**
     * send put request
     *
     * @param httpUrl target url
     * @param jsonStr json string
     */
    public static String sendHttpPutByRetry(String httpUrl, String jsonStr, int retryCount, Header... header) {
        return sendHttpPutByRetry(httpUrl, jsonStr, 0L, null, retryCount, header);
    }

    /**
     * send put request
     *
     * @param httpUrl target url
     * @param jsonStr json String
     */
    public static String sendHttpPut(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit,
                                     Header... header) {
        return sendHttpPutByRetry(httpUrl, jsonStr, timeout, timeUnit, 0, header);
    }

    /**
     * send put request
     *
     * @param httpUrl target url
     * @param jsonStr json String
     */
    public static String sendHttpPutByRetry(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit,
                                            int retryCount, Header... header) {
        HttpPut httpPut = new HttpPut(httpUrl);
        try {
            StringEntity stringEntity = new StringEntity(jsonStr, DEFAULT_CHARSET_NAME);
            stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            httpPut.setEntity(stringEntity);
            if (header != null) {
                httpPut.setHeaders(header);
            }
        } catch (Exception e) {
            throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
        }
        return sendHttpRequestByRetry(httpPut, timeout, timeUnit, retryCount);
    }

    /**
     * send delete request
     *
     * @param jsonStr json
     */
    public static String sendHttpDelete(String httpUrl, String jsonStr, Header... headers) {
        return sendHttpDelete(httpUrl, jsonStr, 0L, null, headers);
    }

    /**
     * send delete request
     *
     * @param jsonStr json
     */
    public static String sendHttpDeleteByRetry(String httpUrl, String jsonStr, int retryCount, Header... headers) {
        return sendHttpDeleteByRetry(httpUrl, jsonStr, 0L, null, retryCount, headers);
    }

    /**
     * send delete request
     *
     * @param jsonStr json
     */
    public static String sendHttpDelete(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit,
                                        Header... headers) {
        return sendHttpDeleteByRetry(httpUrl, jsonStr, timeout, timeUnit, 0, headers);
    }

    /**
     * send delete request
     *
     * @param jsonStr json
     */
    public static String sendHttpDeleteByRetry(String httpUrl, String jsonStr, long timeout, TimeUnit timeUnit,
                                               int retryCount, Header... headers) {
        HttpDelete httpDelete = new HttpDelete(httpUrl);
        try {
            StringEntity stringEntity = new StringEntity(jsonStr, DEFAULT_CHARSET_NAME);
            stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            httpDelete.setEntity(stringEntity);
            if (headers != null) {
                httpDelete.setHeaders(headers);
            }
        } catch (Exception e) {
            throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
        }
        return sendHttpRequestByRetry(httpDelete, timeout, timeUnit, retryCount);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param headers param
     */
    public static String sendHttpDelete(String httpUrl, Header... headers) {
        return sendHttpDeleteByRetry(httpUrl, 0, headers);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param headers param
     */
    public static String sendHttpDeleteByRetry(String httpUrl, int retryCount, Header... headers) {
        HttpDelete httpDelete = new HttpDelete(httpUrl);
        if (headers != null) {
            httpDelete.setHeaders(headers);
        }
        return sendHttpRequestByRetry(httpDelete, 0L, null, retryCount);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param timeout timeout
     * @param headers param
     */
    public static String sendHttpDelete(String httpUrl, long timeout, TimeUnit timeUnit, Header... headers) {
        return sendHttpDeleteByRetry(httpUrl, timeout, timeUnit, 0, headers);
    }

    /**
     * send post
     *
     * @param httpUrl target url
     * @param timeout timeout
     * @param headers param
     */
    public static String sendHttpDeleteByRetry(String httpUrl, long timeout, TimeUnit timeUnit, int retryCount,
                                               Header... headers) {
        HttpDelete httpDelete = new HttpDelete(httpUrl);
        if (headers != null) {
            httpDelete.setHeaders(headers);
        }
        return sendHttpRequestByRetry(httpDelete, timeout, timeUnit, retryCount);
    }

    /**
     * send Post request
     */
    private static String sendHttpPost(HttpPost httpPost, long timeout, TimeUnit timeUnit) {
        return HttpSyncClient.sendHttpRequest(httpPost, timeout, timeUnit);
    }

    /**
     * send Post request
     */
    private static String sendHttpPostByRetry(HttpPost httpPost, long timeout, TimeUnit timeUnit, int retryCount) {
        return HttpSyncClient.sendHttpRequestByRetry(httpPost, timeout, timeUnit, retryCount);
    }

    /**
     * send get request
     */
    public static String sendHttpGet(String httpUrl) {
        HttpGet httpGet = new HttpGet(httpUrl);
        return sendHttpGet(httpGet, 0L, null);
    }

    /**
     * send get request
     */
    public static String sendHttpGet(String httpUrl, long timeout, TimeUnit timeUnit) {
        HttpGet httpGet = new HttpGet(httpUrl);
        return sendHttpGet(httpGet, timeout, timeUnit);
    }

    /**
     * send get request
     */
    public static String sendHttpGet(String httpUrl, Header... headers) {
        return sendHttpGet(httpUrl, 0L, null, headers);
    }

    /**
     * send get request
     */
    public static String sendHttpGet(String httpUrl, long timeout, TimeUnit timeUnit, Header... headers) {
        return sendHttpsGetByRetry(httpUrl, timeout, timeUnit, 0, headers);
    }

    /**
     * send get requestHttps
     */
    public static String sendHttpsGet(String httpUrl, Header... headers) {
        return sendHttpsGet(httpUrl, 0L, null, headers);
    }

    /**
     * send get requestHttps
     */
    public static String sendHttpsGet(String httpUrl, long timeout, TimeUnit timeUnit, Header... header) {
        return sendHttpsGetByRetry(httpUrl, timeout, timeUnit, 0, header);
    }

    /**
     * send get requestHttps
     */
    public static String sendHttpsGetByRetry(String httpUrl, long timeout, TimeUnit timeUnit, int retryCount,
                                             Header... header) {
        HttpGet httpGet = new HttpGet(httpUrl);
        if (header != null) {
            httpGet.setHeaders(header);
        }
        return sendHttpRequestByRetry(httpGet, timeout, timeUnit, retryCount);
    }

    /**
     * send get request
     */
    private static String sendHttpGet(HttpGet httpGet, long timeout, TimeUnit timeUnit) {
        return sendHttpRequestByRetry(httpGet, timeout, timeUnit, 3);
    }

    public static String sendHttpRequest(HttpRequestBase httpRequestBase, long timeout, TimeUnit timeUnit) {
        return sendHttpRequestByRetry(httpRequestBase, timeout, timeUnit, 0);
    }

    public static String sendHttpRequestByRetry(HttpRequestBase httpRequestBase, long timeout, TimeUnit timeUnit,
                                                final int retryCount) {
        HttpResult result = execute2ResultByRetry(httpRequestBase
                , timeout
                , timeUnit
                , retryCount
                , false);
        if (result != null) {
            return result.getResult();
        }
        return null;
    }

    public static HttpResult execute2ResultByRetry(HttpRequestBase httpRequestBase
            , long timeout
            , TimeUnit timeUnit
            , int retryCount
            , boolean isReturnHttpResponse) {
        int i = 0;
        if (httpRequestBase == null) {
            throw new SysException(SYS_ERROR_CODE, "HttpRequestBase is null!");
        }
        while (i <= retryCount) {
            try {
                return execute2Result(httpRequestBase, timeout, timeUnit, isReturnHttpResponse);
            } catch (NoHttpResponseException e) {
                if (i == retryCount) {
                    throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
                }
            } catch (Exception e) {
                if (i == retryCount) {
                    log.error("Still failed after retrying, retry count {} error {}: url :{}, "
                            , retryCount
                            , e.getMessage()
                            , httpRequestBase.getURI().toString()
                            , e);
                }
                if (e instanceof AppException) {
                    throw (AppException) e;
                } else if (e instanceof SysException) {
                    throw (SysException) e;
                } else {
                    throw new SysException(SYS_ERROR_CODE, e.getMessage(), e);
                }
            }
            i++;
            log.warn("HttpClient retry count:{},target url {}", i, httpRequestBase.getURI());
        }
        return null;
    }

    public static String execute(HttpRequestBase httpRequestBase, long timeout, TimeUnit timeUnit) throws Exception {
        HttpResult result = execute2Result(httpRequestBase, timeout, timeUnit, false);

        if (result != null) {
            if (result.getStatus() >= 400) {
                throw new SysException(SYS_ERROR_CODE, "httpUrl:" + httpRequestBase.getURI().toString()
                        + " httpStatus:" + result.getStatus() + ", result:" + result.getResult());
            } else {
                return result.getResult();
            }
        }

        return null;
    }

    public static void execute(HttpRequestBase httpRequestBase, long timeout, TimeUnit timeUnit,
                               Consumer<HttpEntity> consumer) throws Exception {
        HttpResult hr = execute2Result(httpRequestBase, timeout, timeUnit, true);
        consumer.accept(hr.getHttpResponse().getEntity());
    }


    public static HttpResult execute2Result(HttpRequestBase httpRequestBase
            , long timeout
            , TimeUnit timeUnit
            , boolean isReturnHttpResponse) throws Exception {
        if (httpRequestBase == null) {
            throw new SysException(SYS_ERROR_CODE, "HttpRequestBase is null!");
        }
        HttpResult result = null;
        RequestContext httpRequest = null;
        CloseableHttpResponse response = null;
        String responseContent = null;

        preHandle(httpRequestBase);

        try {
            setConfig(httpRequestBase, timeout, timeUnit);
            addGlobalHeader(httpRequestBase);
            httpRequest = beforeLog(httpRequestBase);

            long startTime = System.currentTimeMillis();
            response = getHttpClient().execute(httpRequestBase);
            log.info(">>request {}, cost {} ms {} bytes"
                    , httpRequestBase.getURI()
                    , System.currentTimeMillis() - startTime
                    , response.getFirstHeader("Content-Length"));

            afterLog(httpRequest, response);
            if (isReturnHttpResponse) {
                result = new HttpResult(response);
            } else {
                HttpEntity entity = response.getEntity();
                responseContent = EntityUtils.toString(entity);
                int statusCode = response.getStatusLine().getStatusCode();
                result = new HttpResult(statusCode, responseContent);
            }

            afterCompletion(response.getAllHeaders());

            assertResponseStatusCode(httpRequestBase
                    , result
                    , response.getStatusLine() == null
                            ? 500
                            : response.getStatusLine().getStatusCode());

            return result;
        } catch (Exception e) {
            afterThrowingLog(httpRequest, e);
            throwException(e);
            throw new SysException(SYS_ERROR_CODE
                    , String.format("%s -> %s"
                    , e.getMessage()
                    , httpRequestBase.getURI())
                    , e);
        } finally {
            if (!isReturnHttpResponse) {
                closeResources(response, httpRequestBase);
            }
            /**tryMetricsMark(httpRequestBase.getMethod().toLowerCase()
             , httpRequestBase.getURI().getPath()
             , begin.stop().elapsed(TimeUnit.NANOSECONDS));**/
        }
    }

    private static void assertResponseStatusCode(HttpRequestBase httpRequestBase
            , HttpResult result
            , int statusCode) {
        final List<Integer> okCode = ImmutableList.of(200, 201, 204);
        if (!okCode.contains(statusCode)) {

            throw new SysException(SYS_ERROR_CODE, String.format("call %s failed.code %s"
                    , httpRequestBase.getURI().toString()
                    , statusCode)
                    , result);
        }
    }


    private static RequestContext beforeLog(HttpRequestBase httpRequestBase) {
        RequestContext wrapper = null;
        if (HttpClientInterceptors.size() > 0) {
            wrapper = new RequestContextWrapper(httpRequestBase, MDC.get(REQUEST_ID));
            for (HttpClientInterceptor interceptor : HttpClientInterceptors) {
                try {
                    interceptor.before(wrapper);
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
        return wrapper;
    }

    private static void afterLog(RequestContext requestWrapper, CloseableHttpResponse response) {
        ResponseContext wrapper = null;
        if (HttpClientInterceptors.size() > 0) {
            wrapper = new ResponseContextWrapper(response, MDC.get(REQUEST_ID));
            for (HttpClientInterceptor interceptor : HttpClientInterceptors) {
                try {
                    interceptor.after(requestWrapper, wrapper);
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }

            }
        }
    }

    private static void afterThrowingLog(RequestContext wrapper, Exception ex) {
        for (HttpClientInterceptor interceptor : HttpClientInterceptors) {
            try {
                interceptor.throwing(wrapper, ex);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private static void afterCompletion(Header[] headers) {
        for (HttpClientHandler handler : httpClientHandlers) {
            try {
                handler.afterCompletion(headers);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private static void throwException(Exception ex) {
        for (HttpClientHandler handler : httpClientHandlers) {
            try {
                handler.throwException(ex);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    private static void preHandle(HttpRequestBase httpRequestBase) {
        for (HttpClientHandler handler : httpClientHandlers) {
            try {
                handler.preHandle(httpRequestBase);
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }


    private static void setConfig(HttpRequestBase httpRequestBase, long timeout, TimeUnit timeUnit) {
        RequestConfig config = null;
        if (timeout != 10000L && timeout > 0L) {
            int timeoutInMS = Math.toIntExact(TimeUnit.MILLISECONDS.convert(timeout, timeUnit));
            config = RequestConfig.custom().setSocketTimeout(timeoutInMS).setConnectTimeout(timeoutInMS)
                    .setConnectionRequestTimeout(timeoutInMS).build();
        } else {
            config = requestConfig;
        }
        httpRequestBase.setConfig(config);
    }


    private static void closeResources(CloseableHttpResponse httpResponse, HttpRequestBase httpRequestBase) {
        try {
            if (httpResponse != null) {
                httpResponse.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        try {
            if (httpRequestBase != null) {
                httpRequestBase.releaseConnection();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private static void addGlobalHeader(HttpMessage httpMessage) {
        httpMessage.addHeader(REQUEST_ID, Strings.isNullOrEmpty(MDC.get(REQUEST_ID))
                ? UUID.randomUUID().toString().replaceAll("-", "").toLowerCase()
                : MDC.get(REQUEST_ID));
        httpMessage.addHeader(USER_AGENT, PANLI_IBJ);
        httpMessage.addHeader(HEADER_REQ_TIME, String.valueOf(System.currentTimeMillis()));
    }
}
