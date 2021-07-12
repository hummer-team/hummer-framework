package com.hummer.rocketmq.product.plugin.domain;

import lombok.Data;
import org.apache.rocketmq.client.producer.SendCallback;

import java.util.Map;

@Data
public class RocketMqProducerMetadata {
    private long timeoutMills;
    private String tag;
    private String messageKey;
    private boolean ack;
    private int delayTimeLevel;
    private Object body;
    private String topicId;
    private String serializerType;
    private String selectQueue;
    private SendCallback sendCallback;
    private Map<String, String> affiliated;
}
