package com.hummer.kafka.consumer.plugin.consumer;

import com.hummer.kafka.consumer.plugin.callback.HandleBusiness;
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
    private HandleBusiness handleBusiness;
    private ConsumerRebalanceListener rebalanceListener;
    private OffsetCommitCallback commitCallback;
    private ExecutorService executorService;
    private boolean asyncCommitOffset;
    private OffsetSeekEnum offsetSeekEnum;
    private int commitBatchSize;
    private OffsetStore offsetStore;
}
