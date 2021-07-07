package com.hummer.message.facade.event.handler;

import com.google.common.eventbus.Subscribe;
import com.hummer.message.facade.event.ProducerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmHandler.class);

    @Subscribe
    public void alarmHandler(ProducerEvent event) {
        LOGGER.info("send alarm message is todo");
    }
}
