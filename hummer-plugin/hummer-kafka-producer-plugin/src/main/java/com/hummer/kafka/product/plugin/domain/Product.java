package com.hummer.kafka.product.plugin.domain;

import com.hummer.kafka.product.plugin.support.SendMessageMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/7 9:59
 **/
public interface Product {
    /**
     * send message to kafka server
     *
     * @param messageRecord   message
     * @param messageMetadata metadata
     * @return void
     * @author liguo
     * @date 2019/8/8 18:07
     * @since 1.0.0
     **/
    <K, V> void doSend(final ProducerRecord<K, V> messageRecord, final SendMessageMetadata messageMetadata);
}
