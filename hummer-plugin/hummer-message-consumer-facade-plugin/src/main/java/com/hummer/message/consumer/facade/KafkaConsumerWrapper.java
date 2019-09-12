package com.hummer.message.consumer.facade;

import com.hummer.core.SpringApplicationContext;
import com.hummer.kafka.consumer.plugin.callback.ConsumerHandle;
import com.hummer.kafka.consumer.plugin.consumer.ConsumerManager;
import com.hummer.kafka.consumer.plugin.consumer.ConsumerMetadata;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/9/11 17:54
 **/
public class KafkaConsumerWrapper {

    public static void start(
            final @NotNull Collection<String> topicIds
            , final @NotNull String groupName
            , final @NotNull ConsumerHandle handle) {
        final ConsumerMetadata metadata = ConsumerMetadata
                .builder()
                .topicIds(topicIds)
                .groupName(groupName)
                .consumerHandle(handle)
                .build();
        SpringApplicationContext.getBean(ConsumerManager.class)
                .start(metadata);
    }

    public static void start(final @NotNull ConsumerMetadata metadata) {
        SpringApplicationContext.getBean(ConsumerManager.class)
                .start(metadata);
    }
}
