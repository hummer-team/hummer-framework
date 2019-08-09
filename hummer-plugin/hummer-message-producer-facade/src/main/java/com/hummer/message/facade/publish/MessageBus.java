package com.hummer.message.facade.publish;

import com.alibaba.fastjson.JSON;
import com.hummer.common.exceptions.SysException;
import com.hummer.message.facade.metadata.MessagePublishMetadataKey;
import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.spring.plugin.context.SpringApplicationContext;
import lombok.Builder;
import lombok.Getter;
import org.apache.http.Header;

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
    /**
     * message body
     */
    private Object body;
    /**
     * business unique id
     */
    private String appId;
    /**
     * Kafka message configuration
     */
    private Kafka kafka;
    /**
     * rabbit mq message configuration
     */
    private RabbitMq rabbitMq;
    /**
     * send message done callback , if parameter throwable is null then represent send message success else send message failed
     */
    private PublishMessageExceptionCallback callback;
    /**
     * send message timeout millisecond
     */
    private Long sendMessageTimeOutMills;
    /**
     * if true then async send message else sync send message
     */
    private boolean async;

    @Builder
    @Getter
    public static class Kafka {
        /**
         * topic id
         */
        private String topicId;
        /**
         * message id
         */
        private Object messageKey;
        /**
         * message partition,please be careful use this properties
         */
        private Integer partition;
        /**
         * head parameter
         */
        private Collection<Header> header;
    }

    @Builder
    @Getter
    public static class RabbitMq {
        /**
         * rabbit mq message route key
         */
        private String routeKey;
        /**
         * rabbit mq exchange
         */
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
     *
     * @throws SysException if message driver invalid
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
