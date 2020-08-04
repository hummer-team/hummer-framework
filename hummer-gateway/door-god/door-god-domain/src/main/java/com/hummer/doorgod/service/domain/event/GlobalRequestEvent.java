package com.hummer.doorgod.service.domain.event;

import org.springframework.web.server.ServerWebExchange;

public class GlobalRequestEvent extends BaseEvent {
    public GlobalRequestEvent(Object source, String traceId, String routeId, ServerWebExchange exchange) {
        super(source, traceId);
        this.routeId = routeId;
        this.exchange = exchange;
    }

    private String routeId;
    private ServerWebExchange exchange;

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public ServerWebExchange getExchange() {
        return exchange;
    }

    public void setExchange(ServerWebExchange exchange) {
        this.exchange = exchange;
    }
}
