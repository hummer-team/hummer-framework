package com.hummer.message.facade.metadata;

public class MessagePublishMetadataKey {
    private MessagePublishMetadataKey() {

    }

    public static final String MESSAGE_DRIVER_KEY = "hummer.message.driver.type";
    public static final String KAFKA_MESSAGE_DRIVER_NAME = "kafka";
    public static final String RABBITMQ_MESSAGE_DRIVER_NAME = "rabbitmq";
}
