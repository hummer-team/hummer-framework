package com.hummer.message.facade.publish;

import com.alibaba.fastjson.JSON;
import com.hummer.common.exceptions.SysException;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.hummer.common.constant.MessageConfigurationKey.HUMMER_MESSAGE_DRIVER_TYPE_KAFKA_KEY;
import static com.hummer.common.constant.MessageConfigurationKey.HUMMER_MESSAGE_DRIVER_TYPE_KEY;
import static com.hummer.message.facade.metadata.MessagePublishMetadataKey.KAFKA_MESSAGE_DRIVER_NAME;
import static com.hummer.message.facade.metadata.MessagePublishMetadataKey.ROCKETMQ_MESSAGE_DRIVER_NAME;

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
    public static final String KAFKA_BOCKER = "kafka";
    public static final String ROCKETMQ_BOCKER = "rocketMQ";
    private static final Map<String, BaseMessageBusTemplate> MESSAGE_MAP = new HashMap<>(2);

    static {
        MESSAGE_MAP.put(KAFKA_MESSAGE_DRIVER_NAME,
                SpringApplicationContext.getBean(KAFKA_MESSAGE_DRIVER_NAME, BaseMessageBusTemplate.class));
        MESSAGE_MAP.put(ROCKETMQ_MESSAGE_DRIVER_NAME,
                SpringApplicationContext.getBean(ROCKETMQ_MESSAGE_DRIVER_NAME, BaseMessageBusTemplate.class));
    }

    /**
     * message body
     */
    private final Object body;
    /**
     * Kafka message configuration
     */
    private final Kafka kafka;
    /**
     * rocket mq message configuration
     */
    private final RocketMq rocketMq;
    /**
     * message key
     */
    private final String messageKey;
    /**
     * send message done callback , if parameter throwable is null then represent send message success else send message failed
     */
    private final PublishCallback callback;
    /**
     * send message timeout millisecond
     */
    private final long sendTimeOutMills;
    /**
     * if true then async send message else sync send message
     */
    private final boolean async;
    /**
     * true is retry
     */
    @Builder.Default
    private final boolean retry = false;
    /**
     * business group id
     */
    private final String topicId;
    /**
     * message affiliated data
     */
    @Builder.Default
    private final Map<String, String> affiliated = Collections.emptyMap();

    /**
     * publish message to bus server
     *
     * @throws SysException if message driver invalid
     */
    public void publish() {
        checkMessageDriver();
        MESSAGE_MAP.get(getMessageBusTypeAndAssert()).send(this);
    }

    private String getMessageBusTypeAndAssert() {
        String messageDriver = PropertiesContainer.valueOfString(HUMMER_MESSAGE_DRIVER_TYPE_KEY
                , HUMMER_MESSAGE_DRIVER_TYPE_KAFKA_KEY);
        if (!MESSAGE_MAP.containsKey(messageDriver)) {
            throw new SysException(50000
                    , String.format("message driver %s invalid,please setting `kafka` or `rabbitmq`", messageDriver));
        }
        return messageDriver;
    }

    @Override
    public String toString() {
        return String.format("[message bus entity: %s]", JSON.toJSONString(this));
    }

    private void checkMessageDriver() {
        if (this.kafka != null && this.rocketMq != null) {
            throw new IllegalArgumentException("kafka or rocketMq must choose one");
        }
    }

    @Builder
    @Getter
    public static class Kafka {
        /**
         * message partition,please be careful use this properties
         */
        private final Integer partition;
    }

    @Builder
    @Getter
    public static class RocketMq {
        /**
         * message tag flag
         */
        private final String tag;
        /**
         * if true then wait bocker response
         */
        @Builder.Default
        private final boolean ack = false;
        /**
         * 1s、 5s、 10s、 30s、 1m、 2m、 3m、 4m、 5m、 6m、 7m、 8m、 9m、 10m、 20m、 30m、 1h、 2h
         */
        @Builder.Default
        private final int delayLevel = -1;
    }
}
