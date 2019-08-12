package com.hummer.kafka.product.plugin.domain.product;

import com.hummer.common.exceptions.SysException;
import com.hummer.kafka.product.plugin.support.pool.ProducerPool;
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
     * @param messageRecord            message
     * @param waitCompleteTimeOutMills wait send message complete feature time out
     * @return void
     * @author liguo
     * @date 2019/8/8 18:07
     * @since 1.0.0
     **/
    @Override
    public void doSendBySync(final ProducerRecord<String, Object> messageRecord
            , final long waitCompleteTimeOutMills) {
        ProducerPool
                .get(messageRecord.topic())
                .send(messageRecord, waitCompleteTimeOutMills, (metadata, exception) -> {
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
        ProducerPool
                .get(messageRecord.topic())
                .sendAsync(messageRecord, ((metadata, exception) -> {
                    //exception message send log center
                    throw new SysException(5000, "do send message async failed", exception);
                }));
    }
}
