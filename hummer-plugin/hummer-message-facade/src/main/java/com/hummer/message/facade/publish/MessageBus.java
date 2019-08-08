package com.hummer.message.facade.publish;

import com.hummer.message.facade.metadata.Message;
import com.hummer.message.facade.metadata.MessagePublishMetadataKey;
import com.hummer.message.facade.publish.bus.KafkaBaseMessageBus;
import com.hummer.message.facade.publish.bus.RabbitMqBaseMessageBus;
import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.spring.plugin.context.exceptions.KeyNotExistsException;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * message bus static proxy
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 16:07
 **/
public class MessageBus {

    private static Map<String, BaseMessageBusTemplate> messageMap = new HashMap<>(2);

    static {
        messageMap.put(MessagePublishMetadataKey.KAFKA_MESSAGE_DRIVER_NAME,
                PropertiesContainer.valueOf("KafkaBaseMessageBus", BaseMessageBusTemplate.class));
        messageMap.put(MessagePublishMetadataKey.RABBITMQ_MESSAGE_DRIVER_NAME,
                PropertiesContainer.valueOf("RabbitMqBaseMessageBus", BaseMessageBusTemplate.class));
    }

    /**
     * publish message to bus server
     *
     * @param message message
     */
    public static void publish(final Message message) {
        if (!messageMap.containsKey(message.getMessageDriver())) {
            throw new KeyNotExistsException(40000, String.format("message bus driver type %s invalid"
                    , message.getMessageDriver()));
        }

        messageMap
                .get(message.getMessageDriver())
                .publish(message.getBody(), message.getAppId(), null);
    }
}
