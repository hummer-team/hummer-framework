package com.hummer.doorgod.service.domain.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.server.ServerWebExchange;

public class BaseEvent extends ApplicationEvent {

    private String traceId;
    private String routeId;
    private ServerWebExchange exchange;

    public BaseEvent(Object source, String traceId, String routeId, ServerWebExchange exchange) {
        super(source);
        this.traceId = traceId;
        this.routeId = routeId;
        this.exchange = exchange;
    }

    public BaseEvent(Object source) {
        super(source);
    }

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

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }
}