package com.hummer.kafka.consumer.plugin.consumer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hummer.kafka.consumer.plugin.properties.ConsumerProperties;
import com.hummer.kafka.consumer.plugin.callback.MessageBodyMetadata;
import joptsimple.internal.Strings;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * kafka consumer runner
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/12 16:31
 **/
public class KafkaConsumerTaskHolder implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerTaskHolder.class);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final KafkaConsumer<String, Object> consumer;
    private final ConsumerMetadata metadata;

    public KafkaConsumerTaskHolder(final ConsumerMetadata metadata) {
        Preconditions.checkArgument(Strings.isNullOrEmpty(metadata.getGroupName())
                , "please settings consumer group name");
        this.metadata = metadata;
        this.consumer = new KafkaConsumer<>(ConsumerProperties.builderProperties(metadata.getGroupName()));
        this.consumer.subscribe(metadata.getTopicIds()
                , metadata.getRebalanceListener());
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            while (!closed.get()) {
                ConsumerRecords<String, Object>
                        records = consumer.poll(Duration.of(metadata.getPollTimeOutMillis()
                        , ChronoUnit.MILLIS));
                Iterator<ConsumerRecord<String, Object>> recordIterator = records.iterator();
                List<MessageBodyMetadata> list = Lists.newArrayListWithCapacity(16);
                while (recordIterator.hasNext()) {
                    ConsumerRecord<String, Object> recordItem = recordIterator.next();
                    if (recordItem != null) {
                        list.add(MessageBodyMetadata
                                .builder()
                                .body(recordItem.value())
                                .key(recordItem.key())
                                .build());
                    }
                }
                LOGGER.debug("consumer will handle {} count message", list.size());
                long start = System.currentTimeMillis();
                try {
                    if (metadata.getExecutorService() != null) {
                        metadata.getExecutorService()
                                .submit(() -> metadata.getHandleBusiness().handle(ImmutableList.copyOf(list)));
                    } else {
                        metadata.getHandleBusiness().handle(ImmutableList.copyOf(list));
                    }
                } catch (Throwable throwable) {
                    LOGGER.error("business handle failed ", throwable);
                }
                LOGGER.debug("business handle done cost {} millis", System.currentTimeMillis() - start);
                consumer.commitAsync(metadata.getCommitCallback());
            }
        } catch (WakeupException e) {
            //ignore
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    /**
     * shut down this consumer
     *
     * @return void
     * @author liguo
     * @date 2019/8/13 17:44
     * @since 1.0.0
     **/
    public void shutdown() {
        closed.set(true);
        consumer.wakeup();
        LOGGER.info("kafka consumer shutdown");
    }
}
