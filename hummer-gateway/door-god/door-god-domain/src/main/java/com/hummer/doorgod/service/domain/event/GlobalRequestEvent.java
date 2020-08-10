package com.hummer.doorgod.service.domain.event;

import org.springframework.web.server.ServerWebExchange;

public class GlobalRequestEvent extends BaseEvent {
    public GlobalRequestEvent(Object source, String traceId, String routeId, ServerWebExchange exchange) {
        super(source, traceId, routeId, exchange);
    }

    public GlobalRequestEvent(Object source) {
        super(source);
    }
}
