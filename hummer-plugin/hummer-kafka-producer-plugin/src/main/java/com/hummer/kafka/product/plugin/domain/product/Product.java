package com.hummer.kafka.product.plugin.domain.product;

import com.hummer.kafka.product.plugin.support.SendMessageMetadata;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/7 9:59
 **/
public interface Product {
    /**
     * send message to kafka server by sync
     *
     * @param messageRecord    message
     * @param sendTimeOutMills time out mills
     * @return void
     * @author liguo
     * @date 2019/8/8 18:07
     * @since 1.0.0
     **/
    void doSendBySync(final ProducerRecord<String, Object> messageRecord, final long sendTimeOutMills);

    /**
     * send message to kafka server by async
     *
     * @param messageRecord message*
     * @return void
     * @author liguo
     * @date 2019/8/9 14:46
     * @since 1.0.0
     **/
    void doSendByAsync(final ProducerRecord<String, Object> messageRecord);
}
