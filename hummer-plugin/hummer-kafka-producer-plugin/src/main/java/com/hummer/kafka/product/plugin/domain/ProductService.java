package com.hummer.kafka.product.plugin.domain;

import com.hummer.kafka.product.plugin.support.SendMessageMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/7 10:00
 **/
@Service
public class ProductService implements Product {
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
    @Override
    public <K, V> void doSend(ProducerRecord<K, V> messageRecord, SendMessageMetadata messageMetadata) {

    }
}
