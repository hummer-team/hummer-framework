package com.hummer.message.facade.publish.bus;

import com.hummer.message.facade.metadata.MessagePublishMetadataKey;
import com.hummer.message.facade.publish.BaseMessageBusTemplate;
import com.hummer.message.facade.publish.MessageBus;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:37
 **/
@Service(MessagePublishMetadataKey.RABBITMQ_MESSAGE_DRIVER_NAME)
public class RabbitMqBaseMessageBus extends BaseMessageBusTemplate {

    /**
     * send one message
     *
     * @param body  message body
     * @param appId business unique id
     * @return void
     * @author liguo
     * @date 2019/8/5 14:26
     * @since 1.0.0
     **/
    @Override
    public void doSend(final MessageBus messageBus) {

    }

    /**
     * send message to message bus server by async
     *
     * @param messageBus message bus entity
     * @return void
     * @author liguo
     * @date 2019/8/9 16:26
     * @since 1.0.0
     **/
    @Override
    protected void doSendAsync(final MessageBus messageBus) {

    }
}
