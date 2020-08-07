package com.hummer.doorgod.service.domain.filter.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author lee
 */
@Slf4j
public class ProvideServiceTimeGatewayFilterFactory
        extends AbstractGatewayFilterFactory<ProvideServiceTimeGatewayFilterFactory.Config> {

    public ProvideServiceTimeGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new RequestTimeGatewayFilter(config);
    }

    public static class Config implements HasRouteId {
        private long slowTimeMills;

        private String routeId;
        private Integer order;

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public long getSlowTimeMills() {
            return slowTimeMills;
        }

        public void setSlowTimeMills(long slowTimeMills) {
            this.slowTimeMills = slowTimeMills;
        }

        @Override
        public String getRouteId() {
            return routeId;
        }

        @Override
        public void setRouteId(String routeId2) {
            routeId = routeId2;
        }
    }

    public static class RequestTimeGatewayFilter implements GatewayFilter, Ordered {

        private static final String REQUEST_TIME_KEY = "requestTime";
        private final Config config;

        public RequestTimeGatewayFilter(Config config) {
            this.config = config;
        }

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
            exchange.getAttributes().put(REQUEST_TIME_KEY, System.currentTimeMillis());
            //aop
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                Long startTime = (Long) exchange.getAttributes().get(REQUEST_TIME_KEY);
                log.debug("this route id {} url {} cost {} millis"
                        , config.getRouteId()
                        , exchange.getRequest().getURI()
                        , System.currentTimeMillis() - startTime);
            }));
        }

        @Override
        public int getOrder() {
            return config.getOrder() == null ? 1 : config.getOrder();
        }
    }
}
