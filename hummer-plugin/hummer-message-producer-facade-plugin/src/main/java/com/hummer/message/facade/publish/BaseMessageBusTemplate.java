package com.hummer.message.facade.publish;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.hummer.message.facade.metadata.KafkaMessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/2 15:48
 **/
public abstract class BaseMessageBusTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMessageBusTemplate.class);

    /**
     * send one message
     *
     * @param messageBus message body
     * @return void
     * @author liguo
     * @date 2019/8/5 14:26
     * @since 1.0.0
     **/
    public void send(final MessageBus messageBus) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(messageBus.getNamespaceId()), "app id can't null");

        KafkaMessageMetadata metadata = KafkaMessageMetadata.getKafkaMessageMetadata(messageBus.getNamespaceId());
        //if disabled send message
        if (!metadata.isEnable()) {
            return;
        }

        try {
            if (messageBus.isAsync()) {
                doSendAsync(messageBus);
            } else {
                doSend(messageBus);
            }
        } catch (Throwable throwable) {
            LOGGER.error("send message to bus server failed,message:{},exception"
                    , messageBus, throwable);
            //1.send exception message to log center

            //2.call business customer exception handle logic
            if (messageBus.getCallback() != null) {
                messageBus.getCallback().callBack(messageBus.getBody(), throwable);
            } else {
                throw throwable;
            }
        }
    }

    /**
     * send message to message bus server by sync
     *
     * @param messageBus message bus entity
     * @return void
     * @author liguo
     * @date 2019/8/9 16:13
     * @since 1.0.0
     **/
    protected abstract void doSend(final MessageBus messageBus);

    /**
     * send message to message bus server by async
     *
     * @param messageBus message bus entity
     * @return void
     * @author liguo
     * @date 2019/8/9 16:26
     * @since 1.0.0
     **/
    protected abstract void doSendAsync(final MessageBus messageBus);
}
