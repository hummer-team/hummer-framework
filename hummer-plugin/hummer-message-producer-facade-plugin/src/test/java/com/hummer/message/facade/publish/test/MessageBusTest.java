package com.hummer.message.facade.publish.test;

import com.hummer.message.facade.publish.MessageBus;
import org.junit.Test;


/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:45
 **/
public class MessageBusTest {
    @Test(expected = ExceptionInInitializerError.class)
    public void sendMessage() {
        MessageBus
                .builder()
                .kafka(MessageBus.Kafka.builder().topicId("test").build())
                .appId("test")
                .body("test")
                .build()
                .publish();
    }
}
