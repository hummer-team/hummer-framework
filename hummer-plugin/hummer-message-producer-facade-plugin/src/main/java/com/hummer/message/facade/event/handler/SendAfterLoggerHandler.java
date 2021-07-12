package com.hummer.message.facade.event.handler;

import com.google.common.eventbus.Subscribe;
import com.hummer.message.facade.event.ProducerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lee
 */
public class SendAfterLoggerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendAfterLoggerHandler.class);

    @Subscribe
    public void handler(ProducerEvent event) {
        if (event.getException() != null) {
            LOGGER.error("send message to {} broker failed,cost {} millis,topic {}"
                    , event.getBusDriverType()
                    , System.currentTimeMillis() - event.getStartTime()
                    , event.getMessageBus().getTopicId()
                    , event.getException());
        } else {
            LOGGER.info("send message to {} broker success cost {} millis,topic {},partition@offset {}@{}"
                    , event.getBusDriverType()
                    , System.currentTimeMillis() - event.getStartTime()
                    , event.getMessageBus().getTopicId()
                    , event.getPartition()
                    , event.getOffset());
        }
    }
}
