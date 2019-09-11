package com.hummer.kafka.consumer.plugin.consumer;

import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.springframework.stereotype.Component;

import static com.hummer.kafka.consumer.plugin.KafkaConsumerConstant.COMMIT_OFFSET_CALLBACK_DEFAULT;
import static com.hummer.kafka.consumer.plugin.KafkaConsumerConstant.OFFSET_STORE_DEFAULT;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/13 17:43
 **/
@Component
public class ConsumerManager {
    private KafkaConsumerTaskHolder consumerTask;

    /**
     * start this consumer service
     *
     * @param metadata consumer configuration metadata
     * @return void
     * @author liguo
     * @date 2019/8/13 17:52
     * @since 1.0.0
     **/
    public void start(ConsumerMetadata metadata) {
        //
        if (metadata.getCommitCallback() == null) {
            metadata.setCommitCallback(SpringApplicationContext.getBean(COMMIT_OFFSET_CALLBACK_DEFAULT
                    , OffsetCommitCallback.class));
        }
        if (metadata.getOffsetStore() == null) {
            metadata.setOffsetStore(SpringApplicationContext.getBean(OFFSET_STORE_DEFAULT
                    , OffsetStore.class));
        }
        if (metadata.getPollTimeOutMillis() <= 0L) {
            metadata.setPollTimeOutMillis(PropertiesContainer.valueOf(
                    "hummer.kafka.consumer.pool.timeout.millis.default"
                    , Long.class
                    , 3000L));
        }
        if (metadata.getCommitBatchSize() <= 0) {
            metadata.setCommitBatchSize(PropertiesContainer.valueOfInteger(
                    "hummer.kafka.consumer.commit.batch.default"
                    , 1000));
        }
        consumerTask = new KafkaConsumerTaskHolder(metadata);
        //register thread shutdown
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                consumerTask.shutdown();
            }
        });
        //start consumer task
        consumerTask.run();
    }

    /**
     * stop consumer task
     *
     * @param []
     * @return void
     * @author liguo
     * @date 2019/9/11 17:03
     * @since 1.0.0
     **/
    public void stop() {
        if (consumerTask != null) {
            consumerTask.shutdown();
        }
    }
}
