package com.hummer.doorgod.service.domain.filter.factory;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.hummer.common.exceptions.AppException;
import com.hummer.doorgod.service.domain.event.RequestBlacklistEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

/**
 * @author edz
 */
@Slf4j
public class RequestBlacklistGatewayFilterFactory
        extends AbstractGatewayFilterFactory<RequestBlacklistGatewayFilterFactory.Config> {
    public RequestBlacklistGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new RequestBlacklistGatewayFilter(config);
    }

    public static class RequestBlacklistGatewayFilter implements GatewayFilter, Ordered {
        private final Config config;

        public RequestBlacklistGatewayFilter(Config config) {
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
            if (CollectionUtils.isEmpty(config.blackHead)
                    && CollectionUtils.isEmpty(config.blackIp)
                    && CollectionUtils.isEmpty(config.blackUserAgent)) {
                log.debug("route id {} no settings black", config.getRouteId());
                return chain.filter(exchange);
            }

            HttpHeaders headers = exchange.getRequest().getHeaders();

            return Mono.zip(Mono.just(Optional.ofNullable(config.blackIp))
                    , Mono.just(Optional.ofNullable(config.blackHead))
                    , Mono.just(Optional.ofNullable(config.blackUserAgent)))
                    .doOnNext(nx -> {
                        checkHead(headers);
                        checkRequestIp(headers);
                        checkUserAgent(headers);
                    })
                    .doOnError(throwable -> {
                        exchange.getApplicationContext()
                                .publishEvent(new RequestBlacklistEvent(this
                                        , null
                                        , config.routeId
                                        , exchange
                                        , throwable));
                        //
                        throw new AppException(50008, "request forbid -> " + throwable.getMessage());
                    })
                    .then(chain.filter(exchange));
        }

        private void checkHead(HttpHeaders headers) {
            if (CollectionUtils.isEmpty(config.getBlackHead())) {
                return;
            }

            for (String entry
                    : config.getBlackHead()) {
                Iterable<String> iterables = Splitter.on("=").split(entry);
                String v = headers.getFirst(Iterables.get(iterables, 0));
                if (Strings.isEmpty(v) || !entry.equals(v)) {
                    throw new AppException(40000, String.format("%s not find", Iterables.get(iterables, 0)));
                }
            }
        }

        private void checkRequestIp(HttpHeaders headers) {
            if (CollectionUtils.isEmpty(config.blackIp)) {
                return;
            }
            //todo
        }

        private void checkUserAgent(HttpHeaders headers) {
            if (CollectionUtils.isEmpty(config.getBlackUserAgent())) {
                return;
            }
            //todo
        }

        @Override
        public int getOrder() {
            return config.getOrder() == null ? 0 : config.getOrder();
        }
    }

    public static class Config implements HasRouteId {

        private List<String> blackIp;
        private List<String> blackUserAgent;
        private List<String> blackHead;
        private String routeId;
        private Integer order;

        public Integer getOrder() {
            return order;
        }

        public void setOrder(Integer order) {
            this.order = order;
        }

        public List<String> getBlackIp() {
            return blackIp;
        }

        public void setBlackIp(List<String> blackIp) {
            this.blackIp = blackIp;
        }

        public List<String> getBlackUserAgent() {
            return blackUserAgent;
        }

        public void setBlackUserAgent(List<String> blackUserAgent) {
            this.blackUserAgent = blackUserAgent;
        }

        public List<String> getBlackHead() {
            return blackHead;
        }

        public void setBlackHead(List<String> blackHead) {
            this.blackHead = blackHead;
        }

        @Override
        public String getRouteId() {
            return routeId;
        }

        @Override
        public void setRouteId(String routeId) {
            this.routeId = routeId;
        }
    }
}
