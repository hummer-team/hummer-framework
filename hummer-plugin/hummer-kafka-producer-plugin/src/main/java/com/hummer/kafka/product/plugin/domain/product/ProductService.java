package com.hummer.kafka.product.plugin.domain.product;

import com.hummer.common.exceptions.SysException;
import com.hummer.kafka.product.plugin.support.producer.CloseableKafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/7 10:00
 **/
@Service
public class ProductService implements Product {

    @Autowired
    private CloseableKafkaProducer<String, Object> producer;

    /**
     * send message to kafka server
     *
     * @param messageRecord    message
     * @param sendTimeOutMills metadata
     * @return void
     * @author liguo
     * @date 2019/8/8 18:07
     * @since 1.0.0
     **/
    @Override
    public void doSendBySync(final ProducerRecord<String, Object> messageRecord, final long sendTimeOutMills) {
        producer.send(messageRecord, sendTimeOutMills, (metadata, exception) -> {
            //exception message send log center
            throw new SysException(5000, "do send message sync failed", exception);
        });
    }

    /**
     * send message to kafka server by async
     *
     * @param messageRecord message
     * @return void
     * @author liguo
     * @date 2019/8/9 14:46
     * @since 1.0.0
     **/
    @Override
    public void doSendByAsync(final ProducerRecord<String, Object> messageRecord) {
        producer.sendAsync(messageRecord, ((metadata, exception) -> {
            //exception message send log center
            throw new SysException(5000, "do send message async failed", exception);
        }));
    }
}
