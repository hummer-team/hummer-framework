package com.hummer.kafka.consumer.plugin.consumer;

import com.hummer.kafka.consumer.plugin.callback.HandleBusiness;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/13 17:43
 **/
@Component
public class ConsumerManager {
    @Autowired
    private OffsetCommitCallback commitCallback;
    @Autowired
    private ConsumerRebalanceListener rebalanceListener;
    @Autowired(required = false)
    private HandleBusiness handleBusiness;

    /**
     * start this consumer service
     *
     * @param []
     * @return void
     * @author liguo
     * @date 2019/8/13 17:52
     * @since 1.0.0
     **/
    public void start() {
        //
        KafkaConsumerTask consumerTask = new KafkaConsumerTask(null);
        //register thread shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                consumerTask.shutdown();
            }
        });
    }
}
