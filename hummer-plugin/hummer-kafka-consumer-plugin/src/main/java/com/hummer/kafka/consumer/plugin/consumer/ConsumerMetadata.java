package com.hummer.kafka.consumer.plugin.consumer;

import com.hummer.kafka.consumer.plugin.callback.ConsumerHandle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/12 17:56
 **/
@Builder
@Getter
@Setter
public class ConsumerMetadata {
    private Collection<String> topicIds;
    private String groupName;
    private long pollTimeOutMillis;
    private ConsumerHandle consumerHandle;
    private ConsumerRebalanceListener rebalanceListener;
    private OffsetCommitCallback commitCallback;
    private ExecutorService executorService;
    private Boolean asyncCommitOffset;
    private OffsetSeekEnum offsetSeekEnum;
    private int commitBatchSize;
    private OffsetStore offsetStore;

    @Override
    public String toString() {
        return "[" +
                "\ntopicIds=" + topicIds +
                "\n, groupName='" + groupName + '\'' +
                "\n, pollTimeOutMillis=" + pollTimeOutMillis +
                "\n, consumerHandle=" + consumerHandle +
                "\n, rebalanceListener=" + rebalanceListener +
                "\n, commitCallback=" + commitCallback +
                "\n, executorService=" + executorService +
                "\n, asyncCommitOffset=" + asyncCommitOffset +
                "\n, offsetSeekEnum=" + offsetSeekEnum +
                "\n, commitBatchSize=" + commitBatchSize +
                "\n, offsetStore=" + offsetStore +
                " ]";
    }
}
