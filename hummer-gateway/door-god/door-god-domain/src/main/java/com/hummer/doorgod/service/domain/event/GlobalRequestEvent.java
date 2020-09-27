package com.hummer.doorgod.service.domain.event;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;

public class GlobalRequestEvent extends BaseEvent {
    private HttpStatus responseStatus;
    private int responseSize;
    private long requestCostMillis;

    public GlobalRequestEvent(Object source
            , String traceId
            , String routeId
            , ServerWebExchange exchange
            , long requestCostMillis) {
        super(source, traceId, routeId, exchange);
        setResponseStatus(exchange.getResponse().getStatusCode());
        setResponseSize(NumberUtils.toInt(exchange.getResponse().getHeaders().getFirst("Content-Length")));
        setRequestCostMillis(requestCostMillis);
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
