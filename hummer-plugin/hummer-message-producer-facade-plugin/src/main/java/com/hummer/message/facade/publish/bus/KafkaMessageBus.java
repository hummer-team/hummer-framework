package com.hummer.message.facade.publish.bus;

import com.hummer.common.exceptions.SysException;
import com.hummer.kafka.product.plugin.domain.producer.Producer;
import com.hummer.message.facade.metadata.KafkaMessageMetadata;
import com.hummer.message.facade.metadata.MessagePublishMetadataKey;
import com.hummer.message.facade.publish.BaseMessageBusTemplate;
import com.hummer.message.facade.publish.MessageBus;
import joptsimple.internal.Strings;
import org.apache.commons.collections.MapUtils;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:37
 **/
@Service(MessagePublishMetadataKey.KAFKA_MESSAGE_DRIVER_NAME)
public class KafkaMessageBus extends BaseMessageBusTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessageBus.class);
    @Autowired(required = false)
    private Producer producer;

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

        if (Strings.isNullOrEmpty(messageBus.getTopicId())) {
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
        producer.doSendBySync(record, sendMessageTimeOut, ((metadata, exception) -> {
            callbackOfKafka(metadata, messageBus, exception, start);
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
        producer.doSendByAsync(record, ((metadata, exception) -> callbackOfKafka(metadata, messageBus, exception, start))
                , messageBus.getTopicId());
    }

    @Override
    protected boolean enable(String topicId) {
        //get metadata
        KafkaMessageMetadata metadata = KafkaMessageMetadata.get(topicId);
        return metadata.isEnable();
    }

    private ProducerRecord<String, Object> builderProducerRecord(final MessageBus messageBus) {
        ProducerRecord<String, Object> record;
        if (MapUtils.isNotEmpty(messageBus.getAffiliated())) {
            ArrayList<Header> headers = new ArrayList<>();
            messageBus.getAffiliated().forEach((k, v) -> headers.add(new Header() {
                @Override
                public String key() {
                    return k;
                }

                @Override
                public byte[] value() {
                    try {
                        return v.getBytes("utf-8");
                    } catch (UnsupportedEncodingException e) {
                        throw new SysException(50000, String.format("head %s serializer to  bytes failed", k));
                    }
                }
            }));
            record = new ProducerRecord<>(messageBus.getTopicId()
                    , Optional.ofNullable(messageBus.getKafka()).orElse(MessageBus.Kafka.builder().build()).getPartition()
                    , String.valueOf(messageBus.getMessageKey())
                    , messageBus.getBody()
                    , headers);
        } else {
            record = new ProducerRecord<>(messageBus.getTopicId()
                    , Optional.ofNullable(messageBus.getKafka()).orElse(MessageBus.Kafka.builder().build()).getPartition()
                    , String.valueOf(messageBus.getMessageKey())
                    , messageBus.getBody());
        }
        return record;
    }


    private long getSendMessageTimeOut(MessageBus messageBus) {
        return messageBus.getSendTimeOutMills() <= 0
                ? 0
                : messageBus.getSendTimeOutMills();
    }
}
