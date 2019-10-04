package com.hummer.seata.spring.boot.plugin;

import com.google.common.base.Strings;
import com.hummer.core.PropertiesContainer;
import io.seata.core.context.RootContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * this class implement seata xid with spring boot context
 */
public class XidTransferFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(XidTransferFilter.class);

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain filterChain) throws ServletException, IOException {
        //if distracted transcription disabled then execute next filter
        if (!PropertiesContainer.valueOf("distributed.transaction.enable", Boolean.class, Boolean.FALSE)) {
            filterChain.doFilter(request, response);
        }
        final String reqXid = request.getHeader("FesCar-XId");
        final String xid = RootContext.getXID();
        LOGGER.debug("distracted transaction request id is {} and seata context xid is {}", reqXid, xid);
        if (Strings.isNullOrEmpty(xid) && !Strings.isNullOrEmpty(reqXid)) {
            RootContext.bind(reqXid);
            LOGGER.info("distributed transaction xid {} bind to seats context", reqXid);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            String unBindXid = RootContext.unbind();
            if (StringUtils.compareIgnoreCase(reqXid, unBindXid) != 0) {
                LOGGER.error("current request distribute transaction request xid {} no match context xid {}" +
                                ",please check"
                        , reqXid
                        , unBindXid);
            }
        }
    }
}
