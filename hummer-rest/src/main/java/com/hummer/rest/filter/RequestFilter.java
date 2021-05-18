package com.hummer.rest.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.hummer.common.SysConstant;
import com.hummer.common.utils.CommonUtil;
import com.hummer.common.utils.HttpServletRequestUtil;
import com.hummer.common.utils.IpUtil;
import com.hummer.core.PropertiesContainer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.hummer.common.SysConstant.HEADER_REQ_TIME;
import static com.hummer.common.SysConstant.RestConstant.SYSTEM_REMOTE_IP_SPLIT_CHAR;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/24 17:49
 **/
public class RequestFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestFilter.class);
    private static final String PARAM_NAME_EXCLUSIONS = "exclusions";
    private static final String UNKNOWN_STRING = "unknown";

    /**
     * Called by the web container to indicate to a filter that it is
     * being placed into service.
     *
     * <p>The servlet container calls the init
     * method exactly once after instantiating the filter. The init
     * method must complete successfully before the filter is asked to do any
     * filtering work.
     *
     * <p>The web container cannot place the filter into service if the init
     * method either
     * <ol>
     * <li>Throws a ServletException
     * <li>Does not return within a time period defined by the web container
     * </ol>
     *
     * @param filterConfig
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.debug("hummer request filter register done,`RequestFilter`");
    }

    /**
     * The <code>doFilter</code> method of the Filter is called by the
     * container each time a request/response pair is passed through the
     * chain due to a client request for a resource at the end of the chain.
     * The FilterChain passed in to this method allows the Filter to pass
     * on the request and response to the next entity in the chain.
     *
     * <p>A typical implementation of this method would follow the following
     * pattern:
     * <ol>
     * <li>Examine the request
     * <li>Optionally wrap the request object with a custom implementation to
     * filter content or headers for input filtering
     * <li>Optionally wrap the response object with a custom implementation to
     * filter content or headers for output filtering
     * <li>
     * <ul>
     * <li><strong>Either</strong> invoke the next entity in the chain
     * using the FilterChain object
     * (<code>chain.doFilter()</code>),
     * <li><strong>or</strong> not pass on the request/response pair to
     * the next entity in the filter chain to
     * block the request processing
     * </ul>
     * <li>Directly set headers on the response after invocation of the
     * next entity in the filter chain.
     * </ol>
     *
     * @param request
     * @param response
     * @param chain
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        buildRequestId(httpRequest);
        long start = System.currentTimeMillis();
        long ctime = HttpServletRequestUtil.getHeaderFirstByKey(httpRequest, HEADER_REQ_TIME, 0L, Long.class);
        long networkCostTime = ctime > 0L ? start - ctime : 0;
        try {
            // response
            composeResponse(httpResponse);
            // TODO: 2019/6/24 insert response logic
            chain.doFilter(request, response);
        } catch (Throwable throwable) {
            LOGGER.error("request {} handle failed,cost {} millis,user-agent {},error=={}"
                    , HttpServletRequestUtil.getCurrentUrl(httpRequest)
                    , System.currentTimeMillis() - start
                    , HttpServletRequestUtil.getUserAgent(httpRequest)
                    , ExceptionUtils.getStackTrace(throwable));
        } finally {
            long costTime = System.currentTimeMillis() - start;
            outputLog(httpResponse, httpRequest, costTime, networkCostTime);
            MDC.clear();
        }
    }

    private void composeResponse(HttpServletResponse response) {
        if (response == null) {
            return;
        }
        response.addHeader(SysConstant.REQUEST_ID, MDC.get(SysConstant.REQUEST_ID));
        response.addHeader(SysConstant.RestConstant.SERVER_IP, createResponseIp(MDC.get(SysConstant.RestConstant.SERVER_IP)));
        response.addHeader(SysConstant.RestConstant.CLIENT_IP, createResponseIp(MDC.get(SysConstant.RestConstant.CLIENT_IP)));
    }

    private String createResponseIp(String ip) {
        if (StringUtils.isBlank(ip)) {
            return null;
        }
        Iterable<String> iterable = Splitter.on(".").split(ip);
        if (iterable == null) {
            return null;
        }
        String first = Iterables.get(iterable, 2, null);
        String last = Iterables.get(iterable, 3, null);
        return String.format("%s@%s", first, last);
    }


    private void outputLog(final HttpServletResponse response
            , final HttpServletRequest httpRequest
            , final long businessCostTime
            , final long networkCostTime) {
        int successCode = 200;
        int defaultSlowCostTimeMills = PropertiesContainer.valueOf("request.cost.time.slow.value"
                , Integer.class, 10);
        int defaultSlowNetworkTimeMills = PropertiesContainer.valueOf("request.network.cost.time.slow.value"
                , Integer.class, 10);
        String headKey = PropertiesContainer.valueOfString("request.head.key.log","");

        if (response.getStatus() != successCode
                || businessCostTime >= defaultSlowCostTimeMills
                || networkCostTime >= defaultSlowNetworkTimeMills) {
            LOGGER.warn(">> {} - {} - {} ms - {} ms - {} bytes - ua: {} - {}"
                    , HttpServletRequestUtil.getCurrentUrl(httpRequest)
                    , response.getStatus()
                    , businessCostTime
                    , networkCostTime
                    , response.getHeader("Content-Length")
                    , HttpServletRequestUtil.getUserAgent(httpRequest)
                    , HttpServletRequestUtil.getHeaderByKeys(httpRequest,headKey)
            );
        }
    }

    /**
     * Called by the web container to indicate to a filter that it is being
     * taken out of service.
     *
     * <p>This method is only called once all threads within the filter's
     * doFilter method have exited or after a timeout period has passed.
     * After the web container calls this method, it will not call the
     * doFilter method again on this instance of the filter.
     *
     * <p>This method gives the filter an opportunity to clean up any
     * resources that are being held (for example, memory, file handles,
     * threads) and make sure that any persistent state is synchronized
     * with the filter's current state in memory.
     */
    @Override
    public void destroy() {
        LOGGER.info("hummer framework destroyed.");
    }


    private String buildRequestId(HttpServletRequest httpRequest) {
        String requestId = MDC.get(SysConstant.REQUEST_ID);
        if (StringUtils.isEmpty(requestId)) {
            requestId = httpRequest.getHeader(SysConstant.REQUEST_ID);
            if (StringUtils.isEmpty(requestId)) {
                requestId = UUID.randomUUID().toString().replaceAll("-", "");
            }
        }
        MDC.put(SysConstant.REQUEST_ID, requestId);
        MDC.put(SysConstant.RestConstant.SERVER_IP, IpUtil.getLocalIp());
        MDC.put(SysConstant.RestConstant.CLIENT_IP, getRemoteAddr(httpRequest));

        String pSpanId = httpRequest.getHeader(SysConstant.RestConstant.SPAN_ID);
        MDC.put(SysConstant.RestConstant.PARENT_SPAN_ID, StringUtils.isNotEmpty(pSpanId)
                ? pSpanId : CommonUtil.getUuidShort());
        return requestId;
    }

    public String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN_STRING.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isNotEmpty(ip)) {
            String[] newIp = ip.split(PropertiesContainer.get(SYSTEM_REMOTE_IP_SPLIT_CHAR, String.class, ","));
            return newIp[0].trim();
        }

        return ip;
    }

}
