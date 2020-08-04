package com.hummer.doorgod.service.domain.event;

import org.springframework.web.server.ServerWebExchange;

public class GlobalExceptionEvent extends BaseEvent {
    private Throwable throwable;
    private ServerWebExchange exchange;

    public GlobalExceptionEvent(Object source, String traceId, Throwable throwable, ServerWebExchange exchange) {
        super(source, traceId);
        this.throwable = throwable;
        this.exchange = exchange;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public ServerWebExchange getExchange() {
        return exchange;
    }

    public void setExchange(ServerWebExchange exchange) {
        this.exchange = exchange;
    }
}
