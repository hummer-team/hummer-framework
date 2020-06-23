package com.hummer.user.plugin.filter;

import com.google.common.base.Splitter;
import com.hummer.core.PropertiesContainer;
import com.hummer.user.plugin.holder.RequestContextHolder;
import com.hummer.user.plugin.holder.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@Slf4j
public class RequestContextFilter implements Filter {

    private void readRequestContextHolder(HttpServletRequest request) {
        String keyCfg = PropertiesContainer.valueOfString("read.request.key", "*");
        if ("*".equalsIgnoreCase(keyCfg)) {
            if (ArrayUtils.isNotEmpty(request.getCookies())) {
                for (Cookie cookie : request.getCookies()) {
                    RequestContextHolder.set(cookie.getName(), cookie.getValue());
                }
            }
            Enumeration<String> headerKeys = request.getHeaderNames();
            while (headerKeys.hasMoreElements()) {
                String headKey = headerKeys.nextElement();
                RequestContextHolder.set(headKey, request.getHeader(headKey));
            }
        } else {
            Iterable<String> tokenKeys = Splitter
                    .on(",")
                    .split(keyCfg);
            for (String readKey : tokenKeys) {
                String value = request.getHeader(readKey);
                if (StringUtils.isEmpty(value) && ArrayUtils.isNotEmpty(request.getCookies())) {
                    for (Cookie cookie : request.getCookies()) {
                        if (readKey.equalsIgnoreCase(cookie.getName())) {
                            value = cookie.getValue();
                            RequestContextHolder.set(readKey, value);
                        }
                    }
                }
            }
        }

        log.debug("reader request context item count {},add to this holder"
                , RequestContextHolder.size());
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.debug("hummer request context filter register done,`RequestContextFilter`");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        readRequestContextHolder((HttpServletRequest) servletRequest);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        RequestContextHolder.clearHolder();
        UserHolder.clean();
        log.debug("clean hummer request context hold,clean of user.");
    }
}
