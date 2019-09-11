package com.hummer.message.consumer.facade;

import com.hummer.core.SpringApplicationContext;
import com.hummer.kafka.consumer.plugin.consumer.ConsumerManager;
import com.hummer.kafka.consumer.plugin.consumer.ConsumerMetadata;

import java.util.Collections;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/9/11 17:54
 **/
public class KafkaConsumer {
    public static void start() {
        final ConsumerMetadata metadata = ConsumerMetadata
                .builder()
                .topicIds(Collections.singleton("log-type-group-out2"))
                .groupName("log-type-group-01")
                .build();
        SpringApplicationContext.getBean(ConsumerManager.class)
                .start(metadata);
    }
}
