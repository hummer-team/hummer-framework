package com.hummer.common.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Strings;

import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.common.SysConsts;
import com.hummer.common.utils.DateUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.RequestMethod;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

import static com.hummer.common.http.HttpConstant.DEFAULT_GROUP_HTTP_ASYNC;
import static com.hummer.common.http.HttpConstant.HTTP_CONN_SOCKET_TIMEOUT;
import static com.hummer.common.http.HttpConstant.HTTP_CONN_TIMEOUT;
import static com.hummer.common.http.HttpConstant.HTTP_MAX_TOTAL;
import static com.hummer.common.http.HttpConstant.HTTP_PER_MAX_TOTAL;

/**
 * http async wrapper
 *
 * @author liguo.
 * @date 2018/11/5.
 */
public final class HttpAsyncClient {
    private static final String USER_AGENT = "user_agent";
    private static final String HJ_IBJ = "hj_ibj";
    private static final int SUCCESS = 200;
    private static final int SUCCESS_201 = 201;
    private static final int SUCCESS_204 = 204;
    private static final int NO_MODIFY = 304;
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpAsyncClient.class);
    private static final Map<String, HttpAsyncClient> INSTANCE_MAP = new HashMap<>();
    private static final Object LOCK_OBJ = new Object();
    private String groupName;
    /**
     * http客户端
     */
    private CloseableHttpAsyncClient httpClient;
    private DefaultConnectingIOReactor ioReactor;

    /**
     * return http async instance
     *
     * @param []
     * @return com.hummer.support.http.HttpAsyncClient
     * @author liguo
     * @date 2019/6/20 15:00
     * @version 1.0.0
     **/
    public static HttpAsyncClient instance() {
        return instance(null);
    }

    /**
     * return http async instance
     *
     * @param groupName instance group name,same group http async instance shard.
     * @return com.hummer.support.http.HttpAsyncClient
     * @author liguo
     * @date 2019/6/20 15:00
     * @version 1.0.0
     **/
    public static HttpAsyncClient instance(final String groupName) {
        String key = Strings.isNullOrEmpty(groupName) ? DEFAULT_GROUP_HTTP_ASYNC : groupName;

        HttpAsyncClient asyncClient = INSTANCE_MAP.get(key);
        if (asyncClient == null) {
            key = DEFAULT_GROUP_HTTP_ASYNC;
            LOGGER.debug("`{}` http async instance not exists,use default instance.", groupName);
            asyncClient = INSTANCE_MAP.get(DEFAULT_GROUP_HTTP_ASYNC);
        }

        if (asyncClient == null) {
            synchronized (LOCK_OBJ) {
                if (asyncClient == null) {
                    HttpAsyncClient supplier1 = createInstance(groupName);
                    INSTANCE_MAP.remove(key);
                    INSTANCE_MAP.put(key, supplier1);
                    LOGGER.warn("`{}` http async instance not factory,lazy instance done.", groupName);
                    return supplier1;
                }
            }
        }

        return ensureValid(key, asyncClient);
    }

    private static HttpAsyncClient ensureValid(final String groupName, final HttpAsyncClient asyncClient) {
        //check this instance is valid.
        if (asyncClient.isValid()) {
            LOGGER.debug("`{}` http async instance valid ", groupName);
            return asyncClient;
        }

        if (!asyncClient.isValid()) {
            synchronized (LOCK_OBJ) {
                if (!asyncClient.isValid()) {
                    HttpAsyncClient tempAsyncClient = createInstance(groupName);
                    INSTANCE_MAP.remove(groupName);
                    INSTANCE_MAP.put(groupName, tempAsyncClient);
                    LOGGER.warn("`{}` http async instance invalid,re new instance", groupName);
                    return tempAsyncClient;
                }
            }
        }
        return asyncClient;
    }


    /**
     * new http async instance
     *
     * @param connTimeOutMillisecond   connection timeout
     * @param socketTimeOutMillisecond request service wait time
     * @param ioThreadCount            io thread count, default logic cpu count
     * @param maxTotal                 rule total count
     * @param maxPerRoute              per rule count,can't more than the total count
     * @return com.hjapi.classs.learning.monitor.httpasync.HttpAsyncClient
     * @author lee
     * @Date 2018/11/5 18:29
     **/
    private static HttpAsyncClient createInstance(final int connTimeOutMillisecond
            , final int socketTimeOutMillisecond
            , final int ioThreadCount
            , final int maxTotal
            , final int maxPerRoute
            , final String groupName) {
        return new HttpAsyncClient(connTimeOutMillisecond
                , socketTimeOutMillisecond
                , ioThreadCount
                , maxTotal
                , maxPerRoute
                , groupName);
    }

    /**
     * new http async instance，use config strategy.
     *
     * @return com.hjapi.classs.learning.monitor.httpasync.HttpAsyncClient
     * @author lee
     * @Date 2018/11/10 20:19
     **/
    private static HttpAsyncClient createInstance(final String groupName) {
        return HttpAsyncClient.createInstance(PropertiesContainer.get(
                String.format("%s-http-conn-timeout", groupName)
                , Integer.class
                , HTTP_CONN_TIMEOUT)
                , PropertiesContainer.get(String.format("%s-http-socket-timeout", groupName)
                        , Integer.class
                        , HTTP_CONN_SOCKET_TIMEOUT)
                , PropertiesContainer.get(String.format("%s-http-io-thread", groupName)
                        , Integer.class
                        , Runtime.getRuntime().availableProcessors())
                , PropertiesContainer.get(String.format("%s-http-max-total", groupName)
                        , Integer.class
                        , HTTP_MAX_TOTAL)
                , PropertiesContainer.get(String.format("%s-http-max-per-route", groupName)
                        , Integer.class
                        , HTTP_PER_MAX_TOTAL)
                , groupName);
    }

    /**
     * send request to service
     * <pre>
     *     1.with post,put,delete，not support get
     *     2.use async callback
     * </pre>
     *
     * @param customConfig
     * @param typeReference
     * @param handle
     * @return void
     * @implNote with post,put,delete，not support get
     * @author lee
     * @Date 2018/11/5 17:54
     **/
    public <INPUT, OUT> void send(final RequestCustomConfig<INPUT> customConfig
            , final TypeReference<OUT> typeReference
            , final SendMessageHandle<OUT, INPUT> handle) {

        HttpEntityEnclosingRequestBase requestBase =
                HttpInternalUtil.getHttpRequest(customConfig.getUrl(), customConfig.getMethod());

        retryExecute(requestBase, customConfig, typeReference, handle);
    }

    /**
     * send request to service
     * <pre>
     *     1.with post,put,delete，not support get
     *     2.use async callback
     * </pre>
     *
     * @param customConfig
     * @param typeReference
     * @return void
     * @implNote with post,put,delete，not support get
     * @author lee
     * @Date 2018/11/5 17:54
     **/
    public <INPUT, OUT> OUT send(final RequestCustomConfig<INPUT> customConfig
            , final TypeReference<OUT> typeReference) {

        long start2 = System.currentTimeMillis();

        HttpEntityEnclosingRequestBase requestBase =
                HttpInternalUtil.getHttpRequest(customConfig.getUrl(), customConfig.getMethod());

        long bodyCostTime = initRequest(requestBase, customConfig);

        long requestStartTime2 = System.currentTimeMillis();

        HttpResponse response = retryExecute(() ->
                        httpClient.execute(requestBase,
                                new FutureCallbackHandle<>(customConfig
                                        , requestBase))
                , customConfig);
        long requestCostTime2 = System.currentTimeMillis() - requestStartTime2;

        long parseResultTime2 = System.currentTimeMillis();
        OUT out = parseResponse(customConfig, typeReference, response);
        LOGGER.info("call url {},total cost time {} ms,bodyCostTime {} ms,requestCostTime {} ms,parseResultCostTime {} ms"
                , customConfig.getUrl()
                , System.currentTimeMillis() - start2
                , bodyCostTime
                , requestCostTime2
                , System.currentTimeMillis() - parseResultTime2);
        return out;
    }

    /**
     * send request to service
     * <pre>
     *     1.with post,put,delete，not support get
     *     2.use async callback
     * </pre>
     *
     * @param customConfig
     * @param handle
     * @return void
     * @implNote with post,put,delete，not support get
     * @author lee
     * @Date 2018/11/5 17:54
     **/
    public <INPUT> void send(final RequestCustomConfig<INPUT> customConfig
            , final SendMessageHandle<String, INPUT> handle) {

        HttpEntityEnclosingRequestBase requestBase =
                HttpInternalUtil.getHttpRequest(customConfig.getUrl(), customConfig.getMethod());

        retryExecute(requestBase, customConfig, null, handle);
    }


    /**
     * send request to service
     * <pre>
     *     1.with post,put,delete，not support get
     *     2.use async callback
     * </pre>
     *
     * @param customConfig
     * @param handle
     * @return void
     * @implNote with post,put,delete，not support get
     * @author lee
     * @Date 2018/11/5 17:54
     **/
    public <INPUT> String send(final RequestCustomConfig<INPUT> customConfig) {

        long start = System.currentTimeMillis();
        HttpEntityEnclosingRequestBase requestBase =
                HttpInternalUtil.getHttpRequest(customConfig.getUrl(), customConfig.getMethod());

        long bodyCostTime = initRequest(requestBase, customConfig);

        long requestStartTime = System.currentTimeMillis();

        HttpResponse response = retryExecute(() ->
                        httpClient.execute(requestBase,
                                new FutureCallbackHandle<>(customConfig, requestBase))
                , customConfig);
        long requestCostTime = System.currentTimeMillis() - requestStartTime;

        long parseResultTime = System.currentTimeMillis();
        String out = parseResponse(customConfig, response);

        LOGGER.info("call url {},total cost time {} ms,bodyCostTime {} ms,requestCostTime {} ms,parseResultCostTime {} ms"
                , customConfig.getUrl()
                , System.currentTimeMillis() - start
                , bodyCostTime
                , requestCostTime
                , System.currentTimeMillis() - parseResultTime);
        return out;
    }


    /**
     * send http get request to service,  ignore RequestCustomConfig.RequestMethod
     * <pre>
     *     use async callback
     * </pre>
     *
     * @param typeReference target type
     * @return OUT convert service response content to target class type
     * @author liguo
     * @Date 2018/11/29 18:06
     * @version 1.0.0
     * @Param customConfig
     **/
    @SuppressWarnings("unchecked")
    public <OUT> OUT sendGet(final RequestCustomConfig customConfig
            , final TypeReference<OUT> typeReference) {

        customConfig.setMethod(RequestMethod.GET);

        HttpRequestBase requestBase = HttpInternalUtil.getHttpRequestV2(customConfig.getUrl()
                , customConfig.getMethod());

        long start = System.currentTimeMillis();

        long bodyCostTime = initRequest(requestBase, customConfig);

        long requestStartTime = System.currentTimeMillis();

        HttpResponse response = retryExecute(() ->
                        httpClient.execute(requestBase,
                                new FutureCallbackHandle<>(customConfig, requestBase))
                , customConfig);
        long requestCostTime = System.currentTimeMillis() - requestStartTime;

        long parseResultTime = System.currentTimeMillis();
        OUT out = (OUT) parseResponse(customConfig
                , typeReference
                , response);

        LOGGER.info("call {},total cost time {} ms,bodyCostTime {} ms,requestCostTime {} ms,parseResultCostTime {} ms"
                , customConfig.getUrl()
                , System.currentTimeMillis() - start
                , bodyCostTime
                , requestCostTime
                , System.currentTimeMillis() - parseResultTime);

        return out;
    }

    /**
     * send http get request to service,  ignore RequestCustomConfig.RequestMethod,return service original message
     *
     * @param customConfig request config settings
     * @return java.lang.String
     * @author liguo
     * @date 2019/6/20 15:14
     * @version 1.0.0
     **/
    @SuppressWarnings("unchecked")
    public String sendGet(final RequestCustomConfig customConfig) {
        customConfig.setMethod(RequestMethod.GET);

        HttpRequestBase requestBase = HttpInternalUtil.getHttpRequestV2(customConfig.getUrl()
                , customConfig.getMethod());

        long start = System.currentTimeMillis();

        long bodyCostTime = initRequest(requestBase, customConfig);

        long requestStartTime = System.currentTimeMillis();
        HttpResponse response = retryExecute(() ->
                        httpClient.execute(requestBase
                                , new FutureCallbackHandle<>(customConfig, requestBase))
                , customConfig);
        long requestCostTime = System.currentTimeMillis() - requestStartTime;

        long parseResultTime = System.currentTimeMillis();
        String result = parseResponse(customConfig, response);

        LOGGER.info("call {},total cost time {} ms,bodyCostTime {} ms,requestCostTime {} ms,parseResultCostTime {} ms"
                , customConfig.getUrl()
                , System.currentTimeMillis() - start
                , bodyCostTime
                , requestCostTime
                , System.currentTimeMillis() - parseResultTime);

        return result;
    }

    /**
     * send http get request to service,ignore RequestCustomConfig.RequestMethod
     * <pre>
     *     use @link {#SendMessageHandle} callback
     * </pre>
     *
     * @param customConfig
     * @param typeReference
     * @param handle
     * @return void
     * @author liguo
     * @date 2018/12/21 10:27
     * @version 1.0.0
     **/
    public <INPUT, OUT> void sendGet(final RequestCustomConfig<INPUT> customConfig
            , final TypeReference<OUT> typeReference
            , final SendMessageHandle<OUT, INPUT> handle) {

        customConfig.setMethod(RequestMethod.GET);

        HttpRequestBase requestBase = HttpInternalUtil.getHttpRequestV2(customConfig.getUrl()
                , customConfig.getMethod());

        initRequest(requestBase, customConfig);

        retryExecute(requestBase, customConfig, typeReference, handle);
    }

    /**
     * send http get request to service,ignore RequestCustomConfig.RequestMethod
     * <pre>
     *     1，use @link {#SendMessageHandle} callback
     *     2，callback input parameter service response origin string
     * </pre>
     *
     * @param customConfig
     * @param handle
     * @return void
     * @author liguo
     * @date 2018/12/21 10:27
     * @version 1.0.0
     **/
    public <INPUT> void sendGet(final RequestCustomConfig<INPUT> customConfig
            , final SendMessageHandle<String, INPUT> handle) {

        customConfig.setMethod(RequestMethod.GET);

        HttpRequestBase requestBase = HttpInternalUtil.getHttpRequestV2(customConfig.getUrl()
                , customConfig.getMethod());

        initRequest(requestBase, customConfig);

        retryExecute(requestBase, customConfig, null, handle);
    }

    /**
     * check http instance is valid,if true representation valid,false unValid
     *
     * @param []
     * @return boolean
     * @author liguo
     * @date 2019/1/5 20:44
     * @version 1.0.0
     **/
    public boolean isValid() {
        return httpClient.isRunning();
    }

    private <INPUT, OUT> OUT retryExecute(Supplier<Future<OUT>> function
            , RequestCustomConfig<INPUT> customConfig) {
        int i = 0;
        while (i++ <= customConfig.getRetryCount()) {
            boolean hasException = false;
            Future<OUT> future = null;
            try {
                if (isValid()) {
                    future = function
                            .get();
                    return future.get(customConfig.getSocketTimeOutMillisecond()
                            , TimeUnit.MILLISECONDS);
                }
            } catch (TimeoutException e) {
                future.cancel(true);
            } catch (Throwable e) {
                hasException = true;
                logRequestFail(customConfig
                        , i
                        , e);
            }
            if (hasException) {
                sleep(hasException, customConfig.getRetrySleepMillisecond());
            }
        }
        return null;
    }


    private <INPUT, OUT> void retryExecute(
            final HttpRequestBase httpRequestBase
            , final RequestCustomConfig<INPUT> customConfig
            , final TypeReference<OUT> typeReference
            , final SendMessageHandle<OUT, INPUT> handle) {

        int i = 0;
        while (i++ <= customConfig.getRetryCount()) {
            boolean hasException;
            try {
                if (isValid()) {
                    execute(httpRequestBase, customConfig, typeReference, handle);
                }
                return;
            } catch (Exception e) {
                hasException = true;
                logRequestFail(customConfig, i, e);
            }
            if (hasException) {
                try {
                    Thread.sleep(customConfig.getRetrySleepMillisecond());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private <INPUT> String parseResponse(RequestCustomConfig<INPUT> customConfig
            , HttpResponse response) {

        if (response == null) {
            throw new NullPointerException("response is null.");
        }
        try {

            HttpEntity entity = HttpInternalUtil.getHttpEntity(response);
            final String gzip = "gzip";
            if (entity.getContentEncoding() != null && gzip
                    .equalsIgnoreCase(entity.getContentEncoding().getValue())) {
                entity = new GzipDecompressingEntity(entity);
            }

            return Strings.isNullOrEmpty(customConfig.getRespEncoding())
                    ? EntityUtils.toString(entity)
                    : EntityUtils.toString(entity, Charset.forName(customConfig.getRespEncoding()));

        } catch (Exception e) {
            LOGGER.error("parse service resp exception, req config:{},ex ", customConfig, e);
            return null;
        }
    }


    private <INPUT, OUT> OUT parseResponse(RequestCustomConfig<INPUT> customConfig
            , TypeReference<OUT> typeReference
            , HttpResponse response) {

        String val = parseResponse(customConfig, response);
        if (Strings.isNullOrEmpty(val)) {
            return null;
        }

        try {
            return JSON.parseObject(val
                    , typeReference);
        } catch (Exception e) {
            LOGGER.error("parse service resp exception, req config:{},ex ", customConfig, e);
            return null;
        }
    }

    /**
     * execute http request
     *
     * @param requestBase
     * @param customConfig
     * @param typeReference
     * @param handle
     * @return void
     * @author lee
     * @Date 2018/11/5 17:46
     **/
    private <INPUT, OUT> void execute(HttpRequestBase requestBase
            , RequestCustomConfig<INPUT> customConfig
            , final TypeReference<OUT> typeReference
            , final SendMessageHandle<OUT, INPUT> handle) {

        long totalStartTime = System.currentTimeMillis();

        long toJsonCostTime = initRequest(requestBase, customConfig);

        long start = System.currentTimeMillis();

        httpClient.execute(requestBase, new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse result) {
                long completedConstTime = System.currentTimeMillis() - start;
                //release conn
                requestBase.releaseConnection();
                //
                int statusCode = result.getStatusLine().getStatusCode();
                if (statusCode != SUCCESS
                        && statusCode != SUCCESS_201
                        && statusCode != SUCCESS_204
                        && statusCode != NO_MODIFY) {
                    LOGGER.error("$=>call service api failed,total cost {} ms,request body toJson Cost {} ms" +
                                    " call service cost {} ms,http status {},request Detail:{},ex:{}"
                            , System.currentTimeMillis() - totalStartTime
                            , toJsonCostTime
                            , completedConstTime
                            , statusCode
                            , customConfig
                            , result.toString());

                    handle.fail(customConfig.getRequestBody()
                            , new RuntimeException(String.format("call service %s fail,error %s"
                                    , customConfig.getUrl()
                                    , result.toString())));
                    return;
                }

                //
                final String gzip = "gzip";
                HttpEntity entity = result.getEntity();
                if (entity.getContentEncoding() != null && gzip
                        .equalsIgnoreCase(entity.getContentEncoding().getValue())) {
                    entity = new GzipDecompressingEntity(entity);
                }

                Charset charset = Charset.forName(/*entity.getContentEncoding() != null
                        ? entity.getContentEncoding().getValue()
                        : */"UTF-8");
                //
                long parseStartTime = System.currentTimeMillis();
                OUT out = null;
                Exception localEx = null;
                String respString = null;
                try {
                    respString = EntityUtils.toString(
                            entity, charset);
                    //typeReference不为空则解析为领域对象
                    if (typeReference != null) {
                        out = JSON.parseObject(respString, typeReference);
                    } else {
                        out = (OUT) respString;
                    }
                } catch (Exception e) {
                    localEx = e;
                    LOGGER.error("$=>call service api Exception,total cost {} ms,request body toJson Cost {} ms" +
                                    " call service cost {} ms,parse response const {} ms,request Detail:{},resp:{}"
                            , System.currentTimeMillis() - totalStartTime
                            , toJsonCostTime
                            , completedConstTime
                            , System.currentTimeMillis() - parseStartTime
                            , customConfig
                            , respString);
                }
                long parseConstTime = System.currentTimeMillis() - parseStartTime;
                //
                long callHandleStartTime = System.currentTimeMillis();
                if (localEx == null) {
                    handle.complete(out);
                } else {
                    handle.fail(customConfig.getRequestBody(), localEx);
                }
                long callHandleCostTime = System.currentTimeMillis() - callHandleStartTime;
                //
                if (!customConfig.isCallSuccessOutPutBody()) {
                    respString = "ignore";
                }
                LOGGER.debug(">call service api success total cost {} ms,request body toJson Cost {} ms" +
                                "call service cost {} ms,parse response const {} ms" +
                                "call handle cost {} ms;request Detail:{},resp:{}"
                        , System.currentTimeMillis() - totalStartTime
                        , toJsonCostTime
                        , completedConstTime
                        , parseConstTime
                        , callHandleCostTime
                        , customConfig
                        , respString);
            }

            @Override
            public void failed(Exception ex) {
                long callFailCostTime = System.currentTimeMillis() - start;
                requestBase.releaseConnection();
                LOGGER.error("#=>call service api failed,total cost time {} ms,request body toJson Cost {} ms" +
                                " call service cost {} ms request Detail:{},ex"
                        , System.currentTimeMillis() - totalStartTime
                        , toJsonCostTime
                        , callFailCostTime
                        , customConfig
                        , ex);
                //
                handle.fail(customConfig.getRequestBody(), ex);
            }

            @Override
            public void cancelled() {
                long cancelledCostTime = System.currentTimeMillis() - start;
                requestBase.releaseConnection();
                LOGGER.warn("#=>call service api cancelled,total cost time {} ms,request body toJson Cost {} ms" +
                                "call service cost {} ms request Detail:{}"
                        , System.currentTimeMillis() - totalStartTime
                        , toJsonCostTime
                        , cancelledCostTime
                        , customConfig);
            }
        });
    }

    /**
     * setting request body .return parse request body cost time ms
     *
     * @param requestBase
     * @param customConfig
     * @return long
     * @author lee
     * @Date 2018/11/5 17:44
     **/
    private <TINPUT> long initRequest(final HttpRequestBase requestBase
            ,final RequestCustomConfig<TINPUT> customConfig) {
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setSocketTimeout(customConfig.getSocketTimeOutMillisecond())
                .setConnectTimeout(customConfig.getConnectionRequestTimeout())
                .setConnectionRequestTimeout(customConfig.getConnectionRequestTimeout())
                .build();

        long startTime = System.currentTimeMillis();

        requestBase.setConfig(requestConfig);
        if (!CollectionUtils.isEmpty(customConfig.getHeaders())) {
            requestBase.setHeaders(customConfig.getHeaders().toArray(new Header[0]));
        }

        setRequestHead(requestBase);

        if (customConfig.getRequestBody() != null) {

            long jsonCostStartTime = System.currentTimeMillis();
            String jsonString;
            if (customConfig.getRequestBody() instanceof String) {
                jsonString = customConfig.getRequestBody().toString();
            } else {
                jsonString = JSON.toJSONString(customConfig.getRequestBody());
            }
            long toJsonCostTime = System.currentTimeMillis() - jsonCostStartTime;

            StringEntity stringEntity = new StringEntity(jsonString, "utf-8");
            if (requestBase instanceof HttpEntityEnclosingRequestBase) {
                ((HttpEntityEnclosingRequestBase) requestBase).setEntity(stringEntity);
            }
            return toJsonCostTime;
        }

        return System.currentTimeMillis() - startTime;
    }

    private HttpAsyncClient() {
        this(HTTP_CONN_TIMEOUT
                , HTTP_CONN_SOCKET_TIMEOUT
                , Runtime.getRuntime().availableProcessors()
                , HTTP_MAX_TOTAL
                , HTTP_PER_MAX_TOTAL
                , DEFAULT_GROUP_HTTP_ASYNC);
    }

    private HttpAsyncClient(int connectTimeOut
            , int socketTimeOut
            , int ioThreadCount
            , int maxTotal
            , int maxPerRoute
            , String groupName) {

        this.groupName = groupName;
        int localIoThreadCount = ioThreadCount <= 0 ? Runtime.getRuntime().availableProcessors() : ioThreadCount;
        int localConnTimeOut = connectTimeOut <= 0 ? HTTP_CONN_TIMEOUT : connectTimeOut;
        int localSocketTimeOut = socketTimeOut <= 0 ? HTTP_CONN_SOCKET_TIMEOUT : socketTimeOut;
        int localMaxTotal = maxTotal <= 0 ? HTTP_MAX_TOTAL : maxTotal;
        int localMaxPerRoute = maxPerRoute <= 0 ? HTTP_PER_MAX_TOTAL : maxPerRoute;
        IOReactorConfig ioReactorCfg = IOReactorConfig.custom()
                .setTcpNoDelay(true)
                .setConnectTimeout(localConnTimeOut)
                .setSoKeepAlive(true)
                .setSoTimeout(localSocketTimeOut)
                .setIoThreadCount(localIoThreadCount)
                .build();

        try {

            ioReactor = new DefaultConnectingIOReactor(ioReactorCfg);
            ioReactor.setExceptionHandler(new IOExceptionHandler());

            PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor);
            cm.setDefaultMaxPerRoute(localMaxPerRoute);
            cm.setMaxTotal(localMaxTotal);

            httpClient = HttpAsyncClients.custom().setConnectionManager(cm).build();
            httpClient.start();
        } catch (IOReactorException e) {
            LOGGER.error("HttpAsyncClient http util start fail,", e);
        }

        LOGGER.info("http async init done,connTimeOut-{},socketTimeOut-{}" +
                        ",ioThreadCount-{},maxTotal-{},maxPerRoute-{}"
                , localConnTimeOut
                , localSocketTimeOut
                , localIoThreadCount
                , localMaxTotal
                , localMaxPerRoute);

        assert httpClient != null;
    }


    private static void setRequestHead(HttpMessage httpMessage) {
        httpMessage.addHeader(SysConsts.REQUEST_ID, MDC.get(SysConsts.REQUEST_ID));
        httpMessage.addHeader(USER_AGENT, HJ_IBJ);
        httpMessage.addHeader(SysConsts.HEADER_REQ_TIME, String.valueOf(DateUtil.getTimestampInMillis()));
    }

    private <INPUT> void logRequestFail(RequestCustomConfig<INPUT> customConfig
            , int retryIndex
            , Throwable e) {
        if (!(e.getCause() instanceof NoHttpResponseException)
                || retryIndex == customConfig.getRetryCount()) {
            LOGGER.error("$$>call service api exception,retryTotalCount:{},retryIndex:{},request:{},AuditLog:{}"
                    , customConfig.getRetryCount()
                    , retryIndex
                    , customConfig
                    , ioReactor.getAuditLog()
                    , e);
        }
    }

    private void sleep(final boolean hasException, final int retrySleepMillisecond) {
        if (hasException) {
            try {
                Thread.sleep(retrySleepMillisecond);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }


    class FutureCallbackHandle<T> implements FutureCallback<T> {
        private RequestCustomConfig config;
        private HttpRequestBase requestBase;

        public FutureCallbackHandle(final RequestCustomConfig config
                , final HttpRequestBase requestBase) {
            this.config = config;
            this.requestBase = requestBase;
        }

        @Override
        public void completed(T result) {
            requestBase.releaseConnection();
            LOGGER.debug("request {} completed,", config);
        }

        @Override
        public void failed(Exception ex) {
            requestBase.releaseConnection();
            LOGGER.error("request {} failed,", config, ex);
        }

        @Override
        public void cancelled() {
            requestBase.releaseConnection();
            LOGGER.warn("request {} cancelled", config);
        }
    }
}
