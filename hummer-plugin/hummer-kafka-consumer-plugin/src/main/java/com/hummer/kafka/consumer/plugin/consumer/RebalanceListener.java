package com.hummer.kafka.consumer.plugin.consumer;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/12 18:41
 **/
@Service
public class RebalanceListener implements ConsumerRebalanceListener {
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
     * @param partitions The list of partitions that were assigned to the consumer on the last rebalance
     * @throws WakeupException    If raised from a nested call to {@link KafkaConsumer}
     * @throws InterruptException If raised from a nested call to {@link KafkaConsumer}
     */
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {

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
     * @param partitions The list of partitions that are now assigned to the consumer (may include partitions previously
     *                   assigned to the consumer)
     * @throws WakeupException    If raised from a nested call to {@link KafkaConsumer}
     * @throws InterruptException If raised from a nested call to {@link KafkaConsumer}
     */
    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

    }
}
