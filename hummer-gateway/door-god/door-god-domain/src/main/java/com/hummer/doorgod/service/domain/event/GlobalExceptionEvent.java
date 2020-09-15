package com.hummer.doorgod.service.domain.event;

import org.springframework.web.server.ServerWebExchange;

public class GlobalExceptionEvent extends BaseEvent {
    private Throwable throwable;

    public GlobalExceptionEvent(Object source, String traceId
            , String routeId
            , ServerWebExchange exchange
            , Throwable throwable) {
        super(source, traceId, routeId, exchange);
        this.throwable = throwable;
    }

    public GlobalExceptionEvent(Object source, Throwable throwable) {
        super(source);
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
