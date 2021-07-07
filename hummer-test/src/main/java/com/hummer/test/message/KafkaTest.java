package com.hummer.test.message;

import com.hummer.local.persistence.plugin.bean.MapLocalPersistence;
import com.hummer.message.facade.event.ProducerEvent;
import com.hummer.message.facade.publish.EventWrapper;
import com.hummer.message.facade.publish.MessageBus;
import com.hummer.message.facade.retry.MessageRetrySchedule;
import com.hummer.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class KafkaTest extends BaseTest {
    @Autowired
    private MapLocalPersistence mapLocalPersistence;
    @Autowired
    private MessageRetrySchedule schedule;

    @Test
    public void producerSyncMessage() {
        MessageBus
                .builder()
                .kafka(MessageBus.Kafka.builder().topicId("topic001").build())
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
                    .body("test")
                    .async(true)
                    .callback((partition, offset, messageBody, throwable) -> System.out.println("send done " + offset))
                    .messageKey("000000001")
                    .build()
                    .publish();
        }
        Thread.sleep(2000);
    }

    @Test
    public void localStore() throws InterruptedException {
        MessageBus bus = MessageBus
                .builder()
                .kafka(MessageBus.Kafka.builder().topicId("test01").build())
                .body("test")
                .topicId("test01")
                .messageKey("000000004")
                .build();
        ProducerEvent event = new ProducerEvent();
        event.setMessageBus(bus);
        EventWrapper.post(event);
        Thread.sleep(3000);
    }

    @Test
    public void getMessage() {
        List<String> list = mapLocalPersistence.getAllOperationKey();
        System.out.println(list);
    }

    @Test
    public void retry() {
        schedule.test();
    }
}
