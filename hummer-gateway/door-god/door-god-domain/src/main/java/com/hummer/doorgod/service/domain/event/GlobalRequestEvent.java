package com.hummer.doorgod.service.domain.event;

import org.springframework.web.server.ServerWebExchange;

public class GlobalRequestEvent extends BaseEvent {
    public GlobalRequestEvent(Object source, String traceId, String routeId, ServerWebExchange exchange) {
        super(source, traceId, routeId, exchange);
    }

    public GlobalRequestEvent(Object source) {
        super(source);
    }

    public int getResponseSize() {
        return responseSize;
    }

    public void setResponseSize(int responseSize) {
        this.responseSize = responseSize;
    }

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(HttpStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public long getRequestCostMillis() {
        return requestCostMillis;
    }

    public void setRequestCostMillis(long requestCostMillis) {
        this.requestCostMillis = requestCostMillis;
    }

    @Override
    public String toString() {
        return "GlobalRequestEvent{" +
                "responseStatus=" + responseStatus +
                ", responseSize=" + responseSize +
                ", requestCostMillis=" + requestCostMillis +
                '}';
    }
}
