package com.hummer.message.facade.publish;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.hummer.core.PropertiesContainer;
import com.hummer.local.persistence.plugin.bean.MapLocalPersistence;
import com.hummer.message.facade.metadata.KafkaMessageMetadata;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hummer.common.constant.MessageConfigurationKey.HUMMER_MESSAGE_DRIVER_TYPE_KAFKA_KEY;
import static com.hummer.common.constant.MessageConfigurationKey.HUMMER_MESSAGE_DRIVER_TYPE_KEY;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/2 15:48
 **/
public abstract class BaseMessageBusTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseMessageBusTemplate.class);
    @Autowired
    private MapLocalPersistence mapLocalPersistence;

    /**
     * send one message
     *
     * @param messageBus message body
     * @return void
     * @author liguo
     * @date 2019/8/5 14:26
     * @since 1.0.0
     **/
    public void send(final MessageBus messageBus) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(messageBus.getAppId())
                , "app id can't null");
        //get metadata
        KafkaMessageMetadata metadata = KafkaMessageMetadata.getKafkaMessageMetadata(messageBus.getAppId());
        //if disabled send message
        if (!metadata.isEnable()) {
            return;
        }
        //verify
        verified(messageBus);
        //send message
        if (messageBus.isAsync()) {
            doSendAsync(messageBus);
        } else {
            doSend(messageBus);
        }
    }

    protected void callback(final RecordMetadata metadata
            , final MessageBus messageBus
            , final Exception exception
            , final long startTime) {
        if (exception != null) {
            LOGGER.error("send message to kafka broker failed,cost {} millis,topic {} app id {}"
                    , System.currentTimeMillis() - startTime
                    , messageBus.getKafka().getTopicId()
                    , messageBus.getAppId()
                    , exception);
        } else {
            LOGGER.info("send message to kafka broker success cost {} millis,topic {} app id {},partition@offset {}"
                    , System.currentTimeMillis() - startTime
                    , messageBus.getKafka().getTopicId()
                    , messageBus.getAppId()
                    , metadata.toString());
        }
        //todo refactory
        String driverType = PropertiesContainer.valueOfString(HUMMER_MESSAGE_DRIVER_TYPE_KEY
                , HUMMER_MESSAGE_DRIVER_TYPE_KAFKA_KEY);
        String storeFailedKey = String.format("hummer.message.%s.%s.local.store.failed.message.second"
                , driverType, messageBus.getAppId());
        long storeTimeout = PropertiesContainer.valueOf(storeFailedKey, Long.class, 0L);
        if (storeTimeout > 0) {
            mapLocalPersistence.put(messageBus.getAppId()
                    , messageBus.getMessageKey().toString()
                    , JSON.toJSONBytes(messageBus));
        }
        
        if (messageBus.getCallback() != null) {
            messageBus.getCallback().callBack(metadata.partition(), metadata.offset(), messageBus.getBody(), exception);
        }
    }

    /**
     * verified message , if verified failed then throw exception
     *
     * @param messageBus
     * @return void
     * @author liguo
     * @date 2019/9/12 13:51
     * @since 1.0.0
     **/
    protected abstract void verified(final MessageBus messageBus);

    /**
     * send message to message bus server by sync
     *
     * @param messageBus message bus entity
     * @return void
     * @author liguo
     * @date 2019/8/9 16:13
     * @since 1.0.0
     **/
    protected abstract void doSend(final MessageBus messageBus);

    /**
     * send message to message bus server by async
     *
     * @param messageBus message bus entity
     * @return void
     * @author liguo
     * @date 2019/8/9 16:26
     * @since 1.0.0
     **/
    protected abstract void doSendAsync(final MessageBus messageBus);
}
