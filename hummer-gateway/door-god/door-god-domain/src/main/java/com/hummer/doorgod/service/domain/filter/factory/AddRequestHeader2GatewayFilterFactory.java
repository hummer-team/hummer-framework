package com.hummer.doorgod.service.domain.filter.factory;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;

public class AddRequestHeader2GatewayFilterFactory extends AbstractNameValueGatewayFilterFactory {
    @Override
    public GatewayFilter apply(NameValueConfig config) {
        return (exchange, chain) -> {

            ServerHttpRequest request = exchange
                    .getRequest()
                    .mutate()
                    .header("door-god-time", String.valueOf(System.currentTimeMillis()))
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        };
    }
}
