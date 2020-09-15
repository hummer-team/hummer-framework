package com.hummer.doorgod.service.domain.event;

import org.springframework.web.server.ServerWebExchange;

public class ProvideServiceTimeEvent extends BaseEvent {
    private long costMillis;

    public ProvideServiceTimeEvent(Object source
            , String traceId
            , String routeId
            , ServerWebExchange exchange
            , long costMillis) {
        super(source, traceId, routeId, exchange);
        setCostMillis(costMillis);
    }

    public ProvideServiceTimeEvent(Object source) {
        super(source);
    }

    public long getCostMillis() {
        return costMillis;
    }

    public void setCostMillis(long costMillis) {
        this.costMillis = costMillis;
    }

    @Override
    public String toString() {
        return "ProvideServiceTimeEvent{" +
                "costMillis=" + costMillis +
                "baseEvent=" + super.toString() +
                ", source=" + source +
                '}';
    }
}
