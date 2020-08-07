package com.hummer.doorgod.service.domain.event.handle;

import com.hummer.doorgod.service.domain.event.GlobalExceptionEvent;
import com.hummer.doorgod.service.domain.event.GlobalRequestEvent;
import com.hummer.doorgod.service.domain.event.RequestBlacklistEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@Slf4j
public class EventHandle {

    @EventListener
    public void handle(@NotNull GlobalExceptionEvent event) {
        log.error("this request uri {}, exception {}"
                , event.getExchange().getRequest().getURI()
                , event.getThrowable());
    }

    @EventListener
    public void handle(@NotNull GlobalRequestEvent event) {
        log.debug("handle GlobalRequestEvent {} - {} - {}"
                , event.getTraceId()
                , event.getExchange().getRequest().getURI()
                , event.getExchange().getResponse().getStatusCode());
    }

    @EventListener
    public void handle(RequestBlacklistEvent event) {
        log.error("handle RequestBlacklistEvent {}", event);
    }
}