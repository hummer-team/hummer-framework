package com.hummer.message.facade.publish;

import com.alibaba.fastjson.JSON;
import com.hummer.common.constant.MessageConfigurationKey;
import com.hummer.common.exceptions.SysException;
import com.hummer.message.facade.metadata.Message;
import com.hummer.message.facade.metadata.MessagePublishMetadataKey;
import com.hummer.message.facade.publish.bus.KafkaBaseMessageBus;
import com.hummer.message.facade.publish.bus.RabbitMqBaseMessageBus;
import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.spring.plugin.context.SpringApplicationContext;
import com.hummer.spring.plugin.context.exceptions.KeyNotExistsException;
import lombok.Builder;
import lombok.Getter;
import org.apache.http.Header;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.hummer.common.constant.MessageConfigurationKey.HUMMER_MESSAGE_DRIVER_TYPE_KAFKA_KEY;
import static com.hummer.common.constant.MessageConfigurationKey.HUMMER_MESSAGE_DRIVER_TYPE_KEY;

/**
 * message bus static proxy
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 16:07
 **/
@Builder
@Getter
public class MessageBus {
    private Object body;
    private String appId;
    private Kafka kafka;
    private RabbitMq rabbitMq;
    private PublishMessageExceptionCallback callback;
    private Long sendMessageTimeOutMills;
    private boolean async;

    @Builder
    @Getter
    public static class Kafka {
        private String topicId;
        private Object messageKey;
        private Collection<Header> header;
    }

    @Builder
    @Getter
    public static class RabbitMq {
        private String routeKey;
        private String exchange;
    }

    private static Map<String, BaseMessageBusTemplate> messageMap = new HashMap<>(2);

    static {
        messageMap.put(MessagePublishMetadataKey.KAFKA_MESSAGE_DRIVER_NAME,
                SpringApplicationContext.getBean("KafkaBaseMessageBus", BaseMessageBusTemplate.class));
        messageMap.put(MessagePublishMetadataKey.RABBITMQ_MESSAGE_DRIVER_NAME,
                SpringApplicationContext.getBean("RabbitMqBaseMessageBus", BaseMessageBusTemplate.class));
    }

    /**
     * publish message to bus server
     */
    public void publish() {
        String messageDriver = PropertiesContainer.valueOfString(HUMMER_MESSAGE_DRIVER_TYPE_KEY
                , HUMMER_MESSAGE_DRIVER_TYPE_KAFKA_KEY);
        if (!messageMap.containsKey(messageDriver)) {
            throw new SysException(50000
                    , String.format("message driver %s invalid,please setting `kafka` or `rabbitmq`", messageDriver));
        }
        messageMap
                .get(messageDriver)
                .send(this);
    }

    @Override
    public String toString() {
        return String.format("[message bus entity: %s]", JSON.toJSONString(this));
    }
}
