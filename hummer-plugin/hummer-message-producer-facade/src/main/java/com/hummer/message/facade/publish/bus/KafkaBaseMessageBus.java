package com.hummer.message.facade.publish.bus;

import com.hummer.common.exceptions.SysException;
import com.hummer.kafka.product.plugin.domain.product.Product;
import com.hummer.kafka.product.plugin.support.SendMessageMetadata;
import com.hummer.message.facade.metadata.KafkaMessageMetadata;
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
@Service(value = "KafkaBaseMessageBus")
public class KafkaBaseMessageBus extends BaseMessageBusTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaBaseMessageBus.class);
    @Autowired
    private Product product;

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
    protected void doSend(final MessageBus messageBus) {
        if (messageBus.getKafka() == null) {
            LOGGER.error("kafka message bus,kafka configuration is null don't send message");
            return;
        }
        if (Strings.isNullOrEmpty(messageBus.getKafka().getTopicId())) {
            throw new SysException(50000, "message driver is kafka but no settings topic id,please settings");
        }
        KafkaMessageMetadata kafkaMessageMetadata = KafkaMessageMetadata.getKafkaMessageMetadata(messageBus.getAppId());
        SendMessageMetadata metadata = SendMessageMetadata
                .builder()
                .sendMessageTimeOutMills(kafkaMessageMetadata.getSendMessageTimeOutMills())
                .build();
        ProducerRecord<String, Object> record = builderProducerRecord(messageBus);
        long sendMessageTimeOut = messageBus.getSendMessageTimeOutMills() == null ||
                messageBus.getSendMessageTimeOutMills() <= 0 ? 3000 : messageBus.getSendMessageTimeOutMills();
        product.doSendBySync(record, sendMessageTimeOut);
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
                        throw new SysException(50000, String.format("head %s serializer to  bytes failed", h.getName()));
                    }
                }
            }));
            record = new ProducerRecord<String, Object>(messageBus.getKafka().getTopicId()
                    , null
                    , String.valueOf(messageBus.getKafka().getMessageKey())
                    , messageBus.getBody()
                    , headers);
        } else {
            record = new ProducerRecord<>(messageBus.getKafka().getTopicId(), messageBus.getBody());
        }
        return record;
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
    protected void doSendAsync(MessageBus messageBus) {

    }
}
