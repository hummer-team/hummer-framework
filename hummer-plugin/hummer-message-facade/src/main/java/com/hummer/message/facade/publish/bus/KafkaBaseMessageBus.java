package com.hummer.message.facade.publish.bus;

import com.hummer.kafka.product.plugin.domain.Product;
import com.hummer.kafka.product.plugin.support.SendMessageMetadata;
import com.hummer.message.facade.metadata.KafkaMessageMetadata;
import com.hummer.message.facade.publish.BaseMessageBusTemplate;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:37
 **/
@Service(value = "KafkaBaseMessageBus")
public class KafkaBaseMessageBus extends BaseMessageBusTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaBaseMessageBus.class);
    @Autowired
    private Product product;

    /**
     * send batch message
     *
     * @param body  message body
     * @param appId business unique id
     * @return void
     * @author liguo
     * @date 2019/8/5 14:26
     * @since 1.0.0
     **/
    @Override
    protected <T extends Serializable> void doSendBatch(Collection<T> body, String appId) {

    }

    /**
     * send one message
     *
     * @param body  message body
     * @param appId business unique id
     * @return void
     * @author liguo
     * @date 2019/8/5 14:26
     * @since 1.0.0
     **/
    @Override
    protected <T extends Serializable> void doSend(T body, String appId) {
        KafkaMessageMetadata kafkaMessageMetadata = kafkaMessageMetadata(appId);
        SendMessageMetadata metadata = SendMessageMetadata
                .builder()
                .sendMessageTimeOutMills(kafkaMessageMetadata.getSendMessageTimeOutMills())
                .build();
        product.doSend(new ProducerRecord<>(null, body), metadata);
        LOGGER.warn("{}", body);
    }
}
