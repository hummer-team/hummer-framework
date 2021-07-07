package com.hummer.message.facade.event.handler;

import com.google.common.eventbus.Subscribe;
import com.hummer.message.facade.event.ProducerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SendMessageAfterLoggerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendMessageAfterLoggerHandler.class);

    @Subscribe
    public void handler(ProducerEvent event) {
        if (event.getException() != null) {
            LOGGER.error("send message to kafka broker failed,cost {} millis,topic {} app id {}"
                    , System.currentTimeMillis() - event.getStartTime()
                    , event.getMessageBus().getKafka().getTopicId()
                    , event.getMessageBus().getTopicId()
                    , event.getException());
        } else {
            LOGGER.info("send message to kafka broker success cost {} millis,topic {} app id {},partition@offset {}"
                    , System.currentTimeMillis() - event.getStartTime()
                    , event.getMessageBus().getKafka().getTopicId()
                    , event.getMessageBus().getTopicId()
                    , event.getMetadata());
        }
    }
}
