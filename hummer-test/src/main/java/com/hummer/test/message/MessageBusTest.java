package com.hummer.test.message;

import com.hummer.local.persistence.plugin.bean.MapLocalPersistence;
import com.hummer.message.facade.event.ProducerEvent;
import com.hummer.message.facade.publish.EventWrapper;
import com.hummer.message.facade.publish.MessageBus;
import com.hummer.message.facade.publish.PublishCallback;
import com.hummer.message.facade.retry.MessageRetrySchedule;
import com.hummer.test.BaseTest;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
        for (int i = 201; i < 1201; i++) {
            MessageBus.builder()
                    .rocketMq(MessageBus.RocketMq.builder().tag("order").build())
                    .topicId("test004")
                    .body("testddddd" + i)
                    .messageKey("455" + i)
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

    @Test
    public void sendAsyncOfRocketMq() throws InterruptedException {
        int message = 10;
        CountDownLatch latch = new CountDownLatch(message);
        for (int i = 0; i < message; i++) {
            MessageBus.builder()
                    .rocketMq(MessageBus.RocketMq.builder().tag("order-async").build())
                    .topicId("test004")
                    .async(true)
                    .body("testddddd" + i)
                    .messageKey("000" + i)
                    .callback(new PublishCallback() {
                        @Override
                        public void callBack(int partition, long offset, Object messageBody, Throwable throwable) {
                            System.out.println("offset :" + offset);
                            latch.countDown();
                        }
                    })
                    .build()
                    .publish();
        }
        latch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void sendRocketMq2() throws MQClientException, UnsupportedEncodingException, RemotingException
            , InterruptedException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer("test");
        producer.setNamesrvAddr("10.28.28.20:9876");
        producer.setVipChannelEnabled(false);
        producer.setSendMessageWithVIPChannel(false);
        producer.start();
        for (int i = 0; i < 100; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("TopicTest002" /* Topic */,
                    "TagA" /* Tag */,
                    ("Hello RocketMQ " +
                            i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        //Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }
}
