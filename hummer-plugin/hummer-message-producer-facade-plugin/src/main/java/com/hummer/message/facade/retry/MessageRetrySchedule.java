package com.hummer.message.facade.retry;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.hummer.common.utils.DateUtil;
import com.hummer.core.PropertiesContainer;
import com.hummer.local.persistence.plugin.bean.MapLocalPersistence;
import com.hummer.message.facade.event.MessageEvent;
import com.hummer.message.facade.publish.MessageBus;
import com.hummer.message.facade.publish.PublishCallback;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static com.hummer.message.facade.publish.MessageBus.KAFKA_BOCKER;

/**
 * retry send message to brokerMQ
 *
 * @author lee
 */
@Service
public class MessageRetrySchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageRetrySchedule.class);

    @Autowired
    private MapLocalPersistence mapLocalPersistence;
    private ScheduledExecutorService schedule;

    @PostConstruct
    private void init() {
        long delay = PropertiesContainer.valueOf("hummer.message.schedule.time.cycle.mills", Long.class, 30000L);
        ThreadFactory factory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("hummer-message-%d")
                .setUncaughtExceptionHandler((t, e) -> LOGGER.error("message retry thread exception ", e))
                .build();
        schedule = Executors.newSingleThreadScheduledExecutor(factory);
        schedule.scheduleWithFixedDelay(this::tryRunTask
                , 1000L, delay, TimeUnit.MILLISECONDS);
    }

    @PreDestroy
    private void destroy() throws InterruptedException {
        schedule.awaitTermination(2000, TimeUnit.MILLISECONDS);
        schedule.shutdown();
        LOGGER.info("message retry schedule shutdown ok");
    }

    public void test() {
        runTask();
    }

    private void tryRunTask() {
        try {
            runTask();
        } catch (Throwable e) {
            LOGGER.error("message retry schedule throwable", e);
        }
    }

    private void runTask() {
        Set<String> names = mapLocalPersistence.getSetAllKeys();
        if (CollectionUtils.isEmpty(names)) {
            return;
        }

        for (String name : names) {
            List<byte[]> messages = mapLocalPersistence.removeForListWithOffset(name, 0, 500);
            if (CollectionUtils.isEmpty(messages)) {
                continue;
            }
            for (byte[] by : messages) {
                handler(by);
            }
        }
    }

    private void handler(byte[] by) {
        long start = System.currentTimeMillis();
        MessageEvent event = MessageEvent.parseBytes(by);
        if (filter(event)) {
            return;
        }
        retrySend(event);
        LOGGER.info("retry send message done,top ic  {} message id {},cost {} millis", event.getTopicId()
                , event.getMessageKey(), System.currentTimeMillis() - start);
    }

    private boolean filter(MessageEvent event) {
        return event.getRetryCount() > event.getMaxRetry() || event.getExpireDateTime().before(DateUtil.now());
    }

    private void retrySend(MessageEvent event) {
        if (KAFKA_BOCKER.equalsIgnoreCase(event.getBusDriverType())) {
            MessageBus.builder()
                    .messageKey(event.getMessageKey())
                    .topicId(event.getTopicId())
                    .async(event.isAsync())
                    .retry(true)
                    .callback(new PublishCallback() {
                        @Override
                        public void success(int partition, long offset) {
                            LOGGER.info("message id {} to kafka of retry,send success partition@offset {}@{}"
                                    , event.getMessageKey(), partition, offset);
                        }

                        @Override
                        public void exception(Object messageBody, Throwable throwable) {
                            event.updateRetry();
                            mapLocalPersistence.addToList(event.getTopicId(), event.toBytes());
                            LOGGER.warn("message id {} to kafka retry {} failed,topic id {} add to local queue ok"
                                    , event.getMessageKey(), event.getRetryCount(), event.getTopicId());
                        }
                    })
                    .affiliated(event.getMessageAffiliateData())
                    .sendTimeOutMills(event.getSyncSendMessageTimeOutMills())
                    .kafka(MessageBus.Kafka.builder().partition(event.getPartition())
                            .build())
                    .build()
                    .publish();
        } else {
            MessageBus.builder()
                    .messageKey(event.getMessageKey())
                    .topicId(event.getTopicId())
                    .async(event.isAsync())
                    .retry(true)
                    .callback(new PublishCallback() {
                        @Override
                        public void success(int partition, long offset) {
                            LOGGER.info("message id {} to rocketMq of retry,send success partition@offset {}@{}"
                                    , event.getMessageKey(), partition, offset);
                        }

                        @Override
                        public void exception(Object messageBody, Throwable throwable) {
                            event.updateRetry();
                            mapLocalPersistence.addToList(event.getTopicId(), event.toBytes());
                            LOGGER.warn("message {} to rocketMQ retry {} failed,topic id {}", event.getMessageKey()
                                    , event.getRetryCount(), event.getTopicId());
                        }
                    })
                    .affiliated(event.getMessageAffiliateData())
                    .sendTimeOutMills(event.getSyncSendMessageTimeOutMills())
                    .rocketMq(MessageBus.RocketMq.builder().ack(event.isAck())
                            .tag(event.getTag())
                            .delayLevel(event.getDelayLevel())
                            .build())
                    .topicId(event.getTopicId())
                    .build()
                    .publish();
        }
    }
}
