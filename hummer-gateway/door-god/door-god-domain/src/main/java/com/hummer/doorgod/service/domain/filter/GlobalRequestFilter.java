package com.hummer.doorgod.service.domain.filter;

import com.hummer.common.SysConstant;
import com.hummer.common.utils.HttpServletRequestUtil;
import com.hummer.common.utils.IpUtil;
import com.hummer.doorgod.service.domain.event.GlobalRequestEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @author edz
 */
@Component
@Slf4j
public class GlobalRequestFilter implements GlobalFilter, Ordered {
    /**
     * Process the Web request and (optionally) delegate to the next {@code WebFilter}
     * through the given {@link GatewayFilterChain}.
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.getAttributes().put("globalRequestTime", System.currentTimeMillis());

        String requestId = getRequestId(exchange);
        MDC.put(SysConstant.REQUEST_ID, requestId);
        MDC.put(SysConstant.RestConstant.SERVER_IP, IpUtil.getLocalIp());

        //modified request header
        exchange.getRequest().mutate().header(SysConstant.REQUEST_ID, requestId);

        //per filter
        return chain.filter(exchange)
                .then(Mono.fromRunnable(() -> {
                    //this impl post filter
                    long startTime = (Long) exchange.getAttributes().get("globalRequestTime");
                    log.debug("this request total cost {} millis,uri {} -> {} response status code {}"
                            , System.currentTimeMillis() - startTime
                            , exchange.getRequest().getURI()
                            , (URI) exchange.getAttributes().get(GATEWAY_REQUEST_URL_ATTR)
                            , exchange.getResponse().getStatusCode());
                    exchange.getApplicationContext()
                            .publishEvent(new GlobalRequestEvent(this
                                    , requestId
                                    , null
                                    , exchange));
                }));
    }

    private String getRequestId(ServerWebExchange exchange) {
        return HttpServletRequestUtil.getHeaderFirstByKey(exchange.getRequest()
                    , SysConstant.REQUEST_ID
                    , () -> UUID.randomUUID().toString().replaceAll("-", "").toLowerCase());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
