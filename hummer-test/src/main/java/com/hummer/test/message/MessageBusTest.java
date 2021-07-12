package com.hummer.test.message;

import com.hummer.local.persistence.plugin.bean.MapLocalPersistence;
import com.hummer.message.facade.event.ProducerEvent;
import com.hummer.message.facade.publish.EventWrapper;
import com.hummer.message.facade.publish.MessageBus;
import com.hummer.message.facade.publish.PublishCallback;
import com.hummer.message.facade.retry.MessageRetrySchedule;
import com.hummer.test.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class MessageBusTest extends BaseTest {
    @Autowired
    private MapLocalPersistence mapLocalPersistence;
    @Autowired
    private MessageRetrySchedule schedule;

    @Test
    public void producerSyncMessageOfKafka() {
        MessageBus
                .builder()
                .kafka(MessageBus.Kafka.builder().build())
                .body("test")
                .messageKey("000000001")
                .topicId("topic001")
                .build()
                .publish();
    }

    @Test
    public void producerAsyncMessageOfKafka() throws InterruptedException {
        for (int i = 100; i < 200; i++) {
            MessageBus
                    .builder()
                    .kafka(MessageBus.Kafka.builder().build())
                    .body("test" + i * 100)
                    .async(true)
                    .callback(new PublishCallback() {
                        @Override
                        public void callBack(int partition, long offset, Object messageBody, Throwable throwable) {
                            System.out.println("send done " + offset);
                        }
                    })
                    .messageKey("000000001" + i * 10)
                    .topicId("topic001")
                    .build()
                    .publish();
        }
        Thread.sleep(2000);
    }

    @Test
    public void localStore() throws InterruptedException {
        MessageBus bus = MessageBus
                .builder()
                .kafka(MessageBus.Kafka.builder().build())
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

    @Test
    public void sendOfRocketMq() {
        MessageBus.builder()
                .rocketMq(MessageBus.RocketMq.builder().tag("0001").build())
                .topicId("test001")
                .body("testddddd")
                .messageKey("455")
                .callback(new PublishCallback() {
                    @Override
                    public void callBack(int partition, long offset, Object messageBody, Throwable throwable) {
                        System.out.println("offset :" + offset);
                    }
                })
                .build()
                .publish();
    }
}
