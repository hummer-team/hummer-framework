package com.hummer.doorgod.service.domain.event;

import org.springframework.web.server.ServerWebExchange;

public class ProvideServiceTimeEvent extends BaseEvent {
    public ProvideServiceTimeEvent(Object source, String traceId, String routeId, ServerWebExchange exchange) {
        super(source, traceId, routeId, exchange);
    }

    public ProvideServiceTimeEvent(Object source) {
        super(source);
    }

    private long costMillis;

    public long getCostMillis() {
        return costMillis;
    }

    public void setCostMillis(long costMillis) {
        this.costMillis = costMillis;
    }
}
