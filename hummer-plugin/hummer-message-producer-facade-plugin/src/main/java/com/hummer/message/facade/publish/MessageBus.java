package com.hummer.message.facade.publish;

import com.alibaba.fastjson.JSON;
import com.hummer.common.exceptions.SysException;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.Header;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.hummer.common.constant.MessageConfigurationKey.HUMMER_MESSAGE_DRIVER_TYPE_KAFKA_KEY;
import static com.hummer.common.constant.MessageConfigurationKey.HUMMER_MESSAGE_DRIVER_TYPE_KEY;
import static com.hummer.message.facade.metadata.MessagePublishMetadataKey.KAFKA_MESSAGE_DRIVER_NAME;
import static com.hummer.message.facade.metadata.MessagePublishMetadataKey.RABBITMQ_MESSAGE_DRIVER_NAME;

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
        MESSAGE_MAP.put(RABBITMQ_MESSAGE_DRIVER_NAME,
                SpringApplicationContext.getBean(RABBITMQ_MESSAGE_DRIVER_NAME, BaseMessageBusTemplate.class));
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
    private final Object messageKey;
    /**
     * send message done callback , if parameter throwable is null then represent send message success else send message failed
     */
    private final PublishMessageCallback callback;
    /**
     * send message timeout millisecond
     */
    private final long syncSendMessageTimeOutMills;
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
    private String topicId;

    /**
     * publish message to bus server
     *
     * @throws SysException if message driver invalid
     */
    public void publish() {
        checkMessageDriver();
        this.topicId = this.kafka != null ? this.kafka.topicId : "";
        //do send
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

    public Map<String, Object> toMetadata() {
        checkMessageDriver();

        if (this.kafka != null) {
            return kafka.toMetadata();
        }

        if (this.rocketMq != null) {
            // TODO: 2021/7/5
        }

        throw new IllegalArgumentException("kafka or rocketMQ  must choose one.");
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
         * topic id
         */
        private final String topicId;

        /**
         * message partition,please be careful use this properties
         */
        private final Integer partition;
        /**
         * head parameter
         */
        private final Collection<Header> header;

        public Map<String, Object> toMetadata() {
            Map<String, Object> map = new ConcurrentHashMap<>(3);
            map.put("topicId", topicId);
            if (partition != null) {
                map.put("partition", partition);
            }
            if (CollectionUtils.isNotEmpty(header)) {
                map.put("header", header);
            }
            return map;
        }

        public Kafka fromMetadata(Map<String, Object> map) {
            return new Kafka((String) map.get("topicId"), (Integer) map.get("partition")
                    , (Collection<Header>) map.get("header"));
        }
    }

    @Builder
    @Getter
    public static class RocketMq {
        /**
         * rocket mq message route key
         */
        private final String topicId;
        private final int partition;
    }
}
