package com.hummer.kafka.consumer.plugin.consumer;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.hummer.kafka.consumer.plugin.properties.ConsumerProperties;
import com.hummer.kafka.consumer.plugin.callback.MessageBodyMetadata;
import joptsimple.internal.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * kafka consumer runner,use one thread poll message , mutilate thread consumer message.
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/12 16:31
 **/
public final class KafkaConsumerTaskHolder implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerTaskHolder.class);
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final KafkaConsumer<String, Object> consumer;
    private final ConsumerMetadata metadata;
    private final ConsumerRebalanceListener rebalanceListener;

    public KafkaConsumerTaskHolder(final ConsumerMetadata metadata) {
        Preconditions.checkArgument(Strings.isNullOrEmpty(metadata.getGroupName())
                , "please settings consumer group name");
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(metadata.getTopicIds())
                , "topic id can not empty,please settings");
        Preconditions.checkArgument(metadata.getHandleBusiness() != null
                , "please implement interface HandleBusiness");

        this.metadata = metadata;
        this.consumer = new KafkaConsumer<>(ConsumerProperties.builderProperties(metadata.getGroupName()));
        this.rebalanceListener = new DefaultRebalanceListener<>(consumer, metadata.getOffsetStore(), metadata);
        this.consumer.subscribe(metadata.getTopicIds()
                , rebalanceListener);
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
            //for
            AtomicInteger counter = new AtomicInteger();
            while (!closed.get()) {
                //get message for kafka
                ConsumerRecords<String, Object>
                        records = consumer.poll(Duration.of(metadata.getPollTimeOutMillis()
                        , ChronoUnit.MILLIS));

                Iterator<ConsumerRecord<String, Object>> recordIterator = records.iterator();
                List<MessageBodyMetadata> list = Lists.newArrayListWithCapacity(16);
                List<RecordMetadata> recordList = Lists.newArrayListWithCapacity(16);
                while (recordIterator.hasNext()) {
                    ConsumerRecord<String, Object> recordItem = recordIterator.next();
                    if (recordItem != null) {
                        //append list
                        list.add(MessageBodyMetadata
                                .builder()
                                .body(recordItem.value())
                                .key(recordItem.key())
                                .build());
                        if (metadata.getCommitBatchSize() > 0) {
                            recordList.add(RecordMetadata.builder()
                                    .offset(recordItem.offset())
                                    .partition(recordItem.partition()).build());
                        }
                    }
                }

                LOGGER.debug("consumer will handle {} count message", list.size());
                long start = System.currentTimeMillis();
                //callback business handle,case one async case two sync
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

                //commit offset
                if (metadata.getCommitBatchSize() > 0) {
                    Map<TopicPartition, OffsetAndMetadata> offsetsMap =
                            new ConcurrentHashMap<>(metadata.getCommitBatchSize());
                    for (RecordMetadata metadata : recordList) {
                        offsetsMap.put(new TopicPartition(metadata.getTopicId(), metadata.getPartition())
                                , new OffsetAndMetadata(metadata.getOffset() + 1
                                        , "no metadata"));
                    }
                    if (counter.get() % metadata.getCommitBatchSize() == 0) {
                        if (metadata.isAsyncCommitOffset()) {
                            consumer.commitAsync(offsetsMap, metadata.getCommitCallback());
                        } else {
                            consumer.commitSync(offsetsMap);
                        }
                    }

                    counter.incrementAndGet();
                }

                LOGGER.debug("business handle done cost {} millis", System.currentTimeMillis() - start);
                //commit this offset value
                if (metadata.isAsyncCommitOffset()) {
                    consumer.commitAsync(metadata.getCommitCallback());
                } else {
                    consumer.commitSync();
                }
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
