package com.hummer.test.message;

import com.hummer.message.facade.publish.MessageBus;
import com.hummer.test.BaseTest;
import org.junit.Test;

public class KafkaTest extends BaseTest {
    @Test
    public void producerSyncMessage() {
        MessageBus
                .builder()
                .kafka(MessageBus.Kafka.builder().topicId("topic001").build())
                .appId("test01")
                .body("test")
                .messageKey("000000001")
                .build()
                .publish();
    }

    @Test
    public void producerAsyncMessage() throws InterruptedException {
        for (int i = 100; i < 200; i++) {
            MessageBus
                    .builder()
                    .kafka(MessageBus.Kafka.builder().topicId("topic001").build())
                    .appId("test01" + i)
                    .body("test")
                    .async(true)
                    .callback((partition, offset, messageBody, throwable) -> System.out.println("send done " + offset))
                    .messageKey("000000001")
                    .build()
                    .publish();
        }
        Thread.sleep(2000);
    }
}
