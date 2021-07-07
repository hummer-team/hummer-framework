package com.hummer.message.facade.publish.bus;

import com.hummer.common.exceptions.SysException;
import com.hummer.kafka.product.plugin.domain.product.Product;
import com.hummer.message.facade.metadata.MessagePublishMetadataKey;
import com.hummer.message.facade.publish.BaseMessageBusTemplate;
import com.hummer.message.facade.publish.MessageBus;
import joptsimple.internal.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:37
 **/
@Service(value = MessagePublishMetadataKey.KAFKA_MESSAGE_DRIVER_NAME)
public class KafkaBaseMessageBus extends BaseMessageBusTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaBaseMessageBus.class);
    @Autowired(required = false)
    private Product product;

    /**
     * verified message , if verified failed then throw exception
     *
     * @param messageBus messageBus
     * @return void
     * @author liguo
     * @date 2019/9/12 13:51
     * @since 1.0.0
     **/
    @Override
    protected void verified(MessageBus messageBus) {
        if (messageBus.getKafka() == null) {
            LOGGER.error("kafka message bus but kafka configuration is null, don't send message");
            throw new SysException(50000, "please set message bus kafka properties ");
        }

        if (Strings.isNullOrEmpty(messageBus.getKafka().getTopicId())) {
            throw new SysException(50000, "message driver is kafka but no settings topic id,please settings");
        }
    }

    /**
     * send one message
     *
     * @param messageBus message entity
     * @return void
     * @author liguo
     * @date 2019/8/5 14:26
     * @since 1.0.0
     **/
    @Override
    protected void doSend(final MessageBus messageBus) {
        ProducerRecord<String, Object> record = builderProducerRecord(messageBus);
        long sendMessageTimeOut = getSendMessageTimeOut(messageBus);
        long start = System.currentTimeMillis();
        product.doSendBySync(record, sendMessageTimeOut, ((metadata, exception) -> {
            callback(metadata, messageBus, exception, start);
        }), messageBus.getTopicId());
    }

    /**
     * send message to message bus server by async
     *
     * @param messageBus message bus entity
     * @return void
     * @author liguo
     * @date 2019/8/9 16:26
     * @since 1.0.0
     **/
    @Override
    protected void doSendAsync(final MessageBus messageBus) {
        ProducerRecord<String, Object> record = builderProducerRecord(messageBus);
        long start = System.currentTimeMillis();
        product.doSendByAsync(record, ((metadata, exception) -> callback(metadata, messageBus, exception, start))
                , messageBus.getTopicId());
    }

    private ProducerRecord<String, Object> builderProducerRecord(final MessageBus messageBus) {
        ProducerRecord<String, Object> record;
        if (CollectionUtils.isNotEmpty(messageBus.getKafka().getHeader())) {
            ArrayList<Header> headers = new ArrayList<>();
            messageBus.getKafka().getHeader().forEach(h -> headers.add(new Header() {
                @Override
                public String key() {
                    return h.getName();
                }

                @Override
                public byte[] value() {
                    try {
                        return h.getValue().getBytes("utf-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new SysException(50000, String.format("head %s serializer to  bytes failed"
                                , h.getName()));
                    }
                }
            }));
            record = new ProducerRecord<>(messageBus.getKafka().getTopicId()
                    , messageBus.getKafka().getPartition()
                    , String.valueOf(messageBus.getMessageKey())
                    , messageBus.getBody()
                    , headers);
        } else {
            record = new ProducerRecord<>(messageBus.getKafka().getTopicId()
                    , messageBus.getKafka().getPartition()
                    , String.valueOf(messageBus.getMessageKey())
                    , messageBus.getBody());
        }
        return record;
    }


    private long getSendMessageTimeOut(MessageBus messageBus) {
        return messageBus.getSyncSendMessageTimeOutMills() <= 0
                ? 0
                : messageBus.getSyncSendMessageTimeOutMills();
    }
}
