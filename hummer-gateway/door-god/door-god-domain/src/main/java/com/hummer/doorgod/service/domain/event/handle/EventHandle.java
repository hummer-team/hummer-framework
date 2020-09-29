package com.hummer.doorgod.service.domain.event.handle;

import com.hummer.doorgod.service.domain.event.GlobalExceptionEvent;
import com.hummer.doorgod.service.domain.event.GlobalRequestEvent;
import com.hummer.doorgod.service.domain.event.ProvideServiceTimeEvent;
import com.hummer.doorgod.service.domain.event.RequestBlacklistEvent;
import com.hummer.doorgod.service.domain.event.ServiceDiscoveryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Component
@Slf4j
public class EventHandle {

    @EventListener
    public void handleGlobalExceptionEvent(@NotNull final GlobalExceptionEvent event) {
        log.error("this request {} uri {}, exception {}"
                , event.getRouteId()
                , event.getExchange().getAttributes().get(GATEWAY_REQUEST_URL_ATTR)
                , ExceptionUtils.getStackTrace(event.getThrowable()));
    }

    @EventListener
    public void handleGlobalRequestEvent(@NotNull final GlobalRequestEvent event) {
        log.info("handle GlobalRequestEvent {} - {} - {} - {} - {} byte - {} millis"
                , event.getTraceId()
                , event.getRouteId()
                , event.getExchange().getAttributes().get(GATEWAY_REQUEST_URL_ATTR)
                , event.getResponseStatus()
                , event.getResponseSize()
                , event.getRequestCostMillis());
    }


    @EventListener
    public void handleServiceDiscoveryEvent(@NotNull final ServiceDiscoveryEvent event) {
        long slow = 10L;
        if (event.getGetInstanceListCostMillis() >= slow || event.getChooseInstanceCostMillis() >= slow) {
            log.error("handle ServiceDiscoveryEvent {}", event);
        }
    }

    @EventListener
    public void handleRequestBlacklistEvent(final RequestBlacklistEvent event) {
        log.error("handle RequestBlacklistEvent {}", event);
    }

    @EventListener
    public void handleProvideServiceTimeEvent(final ProvideServiceTimeEvent event) {
        log.debug("handle ProvideServiceTimeEvent this route id {} request cost {} millis,url {}"
                , event.getRouteId()
                , event.getCostMillis()
                , event.getExchange().getAttribute(GATEWAY_REQUEST_URL_ATTR));
        //todo
    }

    @EventListener
    public void handleApplicationReadyEvent(final ApplicationStartedEvent event) {
        //todo
    }
}