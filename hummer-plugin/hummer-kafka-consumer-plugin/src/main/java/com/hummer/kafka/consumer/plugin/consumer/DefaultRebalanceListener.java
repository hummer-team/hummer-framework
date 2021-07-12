package com.hummer.kafka.consumer.plugin.consumer;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/12 18:41
 **/
public final class DefaultRebalanceListener<K, V> implements ConsumerRebalanceListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRebalanceListener.class);
    private final KafkaConsumer<K, V> consumer;
    private final OffsetStore offsetStore;
    private final ConsumerMetadata consumerMetadata;

    public DefaultRebalanceListener(final KafkaConsumer<K, V> consumer
            , final OffsetStore offsetStore
            , final ConsumerMetadata consumerMetadata) {
        this.consumer = consumer;
        this.offsetStore = offsetStore;
        this.consumerMetadata = consumerMetadata;
    }

    /**
     * A callback method the user can implement to provide handling of offset commits to a customized store on the start
     * of a rebalance operation. This method will be called before a rebalance operation starts and after the consumer
     * stops fetching data. It is recommended that offsets should be committed in this callback to either Kafka or a
     * custom offset store to prevent duplicate data.
     * <p>
     * For examples on usage of this API, see Usage Examples section of {@link KafkaConsumer KafkaConsumer}
     * <p>
     * <b>NOTE:</b> This method is only called before rebalances. It is not called prior to {@link KafkaConsumer#close()}.
     * <p>
     * It is common for the revocation callback to use the consumer instance in order to commit offsets. It is possible
     * for a {@link WakeupException} or {@link InterruptException}
     * to be raised from one these nested invocations. In this case, the exception will be propagated to the current
     * invocation of {@link KafkaConsumer#poll(Duration)} in which this callback is being executed. This means it is not
     * necessary to catch these exceptions and re-attempt to wakeup or interrupt the consumer thread.
     *
     * @param partitions The list of com.hummer.kafka.product.plugin.domain.partitions that were assigned to the consumer on the last rebalance
     * @throws WakeupException    If raised from a nested call to {@link KafkaConsumer}
     * @throws InterruptException If raised from a nested call to {@link KafkaConsumer}
     */
    @Override
    public void onPartitionsRevoked(final Collection<TopicPartition> partitions) {
        //commit
        if (consumerMetadata.getOffsetSeekEnum() != OffsetSeekEnum.SPECIFIC_POINT) {
            partitions.forEach(consumer::committed);
        }

        LOGGER.info("Lost com.hummer.kafka.product.plugin.domain.partitions in rebalance,offsetSeek type {}. Committing current offsets:{}"
                , consumerMetadata.getOffsetSeekEnum()
                , partitions);
    }

    /**
     * A callback method the user can implement to provide handling of customized offsets on completion of a successful
     * partition re-assignment. This method will be called after the partition re-assignment completes and before the
     * consumer starts fetching data, and only as the result of a {@link Consumer#poll(Duration) poll(long)} call.
     * <p>
     * It is guaranteed that all the processes in a consumer group will execute their
     * {@link #onPartitionsRevoked(Collection)} callback before any instance executes its
     * {@link #onPartitionsAssigned(Collection)} callback.
     * <p>
     * It is common for the assignment callback to use the consumer instance in order to query offsets. It is possible
     * for a {@link WakeupException} or {@link InterruptException}
     * to be raised from one these nested invocations. In this case, the exception will be propagated to the current
     * invocation of {@link KafkaConsumer#poll(Duration)} in which this callback is being executed. This means it is not
     * necessary to catch these exceptions and re-attempt to wakeup or interrupt the consumer thread.
     *
     * @param partitions The list of com.hummer.kafka.product.plugin.domain.partitions that are now assigned to the consumer (may include com.hummer.kafka.product.plugin.domain.partitions previously
     *                   assigned to the consumer)
     * @throws WakeupException    If raised from a nested call to {@link KafkaConsumer}
     * @throws InterruptException If raised from a nested call to {@link KafkaConsumer}
     */
    @Override
    public void onPartitionsAssigned(final Collection<TopicPartition> partitions) {
        switch (consumerMetadata.getOffsetSeekEnum()) {
            case BEGIN:
                consumer.seekToBeginning(partitions);
                break;
            case END:
                consumer.seekToEnd(partitions);
                break;
            default:
                //read out of store com.hummer.kafka.product.plugin.domain.partitions
                for (TopicPartition partition : partitions) {
                    consumer.seek(partition
                            , offsetStore.getOffset(partition.topic(), partition.hashCode()));
                }
                break;
        }
        LOGGER.info("kafka on partition assigned `{}`, partition {}"
                , consumerMetadata.getOffsetSeekEnum()
                , partitions);
    }
}
