package com.hummer.api.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.ImmutableList;
import com.hummer.kafka.consumer.plugin.callback.ConsumerHandle;
import com.hummer.kafka.consumer.plugin.callback.MessageBodyMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/9/12 14:22
 **/
@Service
public class MessageConsumer implements ConsumerHandle {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageConsumer.class);

    /**
     * handle
     *
     * @param messageBodyCollection message
     * @return void
     * @author liguo
     * @date 2019/8/12 18:06
     * @since 1.0.0
     **/
    @Override
    public void handle(ImmutableList<MessageBodyMetadata> messageBodyCollection) {
        messageBodyCollection.forEach(m -> {
            LOGGER.info("consumer message {}", JSON.toJSONString(m));
        });
    }
}
