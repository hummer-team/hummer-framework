package com.hummer.message.facade.metadata;

import lombok.Builder;
import lombok.Getter;

/**
 * @param <T>
 * @author bingy
 */
@Builder
@Getter
public class Message<T> {
    private T body;
    private String appId;
    private String kafkaTopicId;
    private String messageId;
    private String rabbitMqExchange;
    private String routeKey;
    private String messageDriver;
}
