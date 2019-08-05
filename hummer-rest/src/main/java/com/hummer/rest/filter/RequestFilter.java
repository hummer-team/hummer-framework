package com.hummer.rest.filter;

import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.common.SysConstant;
import com.hummer.common.utils.HttpServletRequestUtil;
import com.hummer.common.utils.IpUtil;
import org.apache.commons.lang3.StringUtils;
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
        try {
            // TODO: 2019/6/24 insert response logic 
            chain.doFilter(request, response);
        } catch (Throwable throwable) {
            LOGGER.error("request {} handle failed,cost {} millis"
                    , HttpServletRequestUtil.getCurrentUrl(httpRequest)
                    , System.currentTimeMillis() - start);
        } finally {
            MDC.clear();
            outputLog((HttpServletResponse) response, httpRequest, System.currentTimeMillis() - start);
        }
    }

    private void outputLog(final HttpServletResponse response
            , final HttpServletRequest httpRequest
            , final long costTime) {
        final int successCode = 200;
        final int defaultSlowCostTimeMills = 10;
        if (response.getStatus() != successCode
                || costTime >= PropertiesContainer.valueOf("request.cost.time.slow.value"
                , Integer.class, defaultSlowCostTimeMills)) {
            LOGGER.warn("request {} handle done,response status {},total cost {} millis"
                    , HttpServletRequestUtil.getCurrentUrl(httpRequest)
                    , response.getStatus()
                    , costTime);
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
