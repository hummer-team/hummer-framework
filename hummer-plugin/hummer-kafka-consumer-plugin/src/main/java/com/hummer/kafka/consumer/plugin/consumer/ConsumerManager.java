package com.hummer.kafka.consumer.plugin.consumer;

import com.hummer.common.utils.FunctionUtil;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerManager.class);
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
        if (!PropertiesContainer.valueOf("hummer.message.kafka.consumer.enable", Boolean.class, false)) {
            LOGGER.debug("kafka consumer disabled");
            return;
        }
        LOGGER.info("starting kafka consumer this metadata info {}", metadata);
        checkAndSet(metadata);
        //register thread shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> this.shutdown()));

        consumerTask = new KafkaConsumerTaskHolder(metadata);
        //start consumer task
        consumerTask.start();
    }


    /**
     * stop consumer task
     *
     * @return void
     * @author liguo
     * @date 2019/9/11 17:03
     * @since 1.0.0
     **/
    public void shutdown() {
        if (consumerTask != null) {
            consumerTask.shutdown();
            LOGGER.info("kafka consumer stop success");
        }
    }

    private void checkAndSet(ConsumerMetadata metadata) {
        FunctionUtil.actionByCondition(
                metadata.getCommitCallback()
                , v -> v == null
                , v -> metadata.setCommitCallback(SpringApplicationContext.getBean(COMMIT_OFFSET_CALLBACK_DEFAULT
                        , OffsetCommitCallback.class)
                ));
        FunctionUtil.actionByCondition(
                metadata.getOffsetStore()
                , v -> v == null
                , v -> metadata.setOffsetStore(SpringApplicationContext.getBean(OFFSET_STORE_DEFAULT
                        , OffsetStore.class)
                ));
        FunctionUtil.actionByCondition(metadata.getPollTimeOutMillis()
                , v -> v <= 0
                , v -> metadata.setPollTimeOutMillis(PropertiesContainer.valueOf(
                        "hummer.message.kafka.consumer.pool.timeout.millis.default"
                        , Long.class
                        , 3000L)));
        FunctionUtil.actionByCondition(metadata.getCommitBatchSize()
                , v -> v <= 0
                , v -> metadata.setCommitBatchSize(PropertiesContainer.valueOfInteger(
                        "hummer.message.kafka.consumer.commit.batch.default"
                        , 1000)));

        FunctionUtil.actionByCondition(metadata.getOffsetSeekEnum()
                , v -> v == null
                , v -> metadata.setOffsetSeekEnum(OffsetSeekEnum.END));

        FunctionUtil.actionByCondition(metadata.getAsyncCommitOffset()
                , v -> v == null
                , v -> metadata.setAsyncCommitOffset(Boolean.TRUE));
    }
}
