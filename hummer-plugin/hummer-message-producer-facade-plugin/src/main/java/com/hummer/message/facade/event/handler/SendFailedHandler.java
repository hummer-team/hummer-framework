package com.hummer.message.facade.event.handler;

import com.google.common.eventbus.Subscribe;
import com.hummer.common.utils.DateUtil;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.local.persistence.plugin.bean.MapLocalPersistence;
import com.hummer.message.facade.event.MessageEvent;
import com.hummer.message.facade.event.ProducerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hummer.common.constant.MessageConfigurationKey.HUMMER_MESSAGE_DRIVER_TYPE_KAFKA_KEY;
import static com.hummer.common.constant.MessageConfigurationKey.HUMMER_MESSAGE_DRIVER_TYPE_KEY;

/**
 * @author edz
 */
public class SendFailedHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SendFailedHandler.class);

    @Subscribe
    public void handler(ProducerEvent event) {
        //if message from retry then ignore
        if (event.isRetry()) {
            return;
        }
        
        String driverType = PropertiesContainer.valueOfString(HUMMER_MESSAGE_DRIVER_TYPE_KEY
                , HUMMER_MESSAGE_DRIVER_TYPE_KAFKA_KEY);
        String retryKey = String.format("hummer.message.%s.producer.%s.send.failed.retry.max"
                , driverType, event.getMessageBus().getTopicId());
        int maxRetry = PropertiesContainer.valueOfInteger(retryKey, 0);
        String expireKey = String.format("hummer.message.%s.producer.%s.failed.message.local.store.second"
                , driverType, event.getMessageBus().getTopicId());
        int expire = PropertiesContainer.valueOfInteger(expireKey, 0);

        LOGGER.info("send message to {} failed,need retry {} topic Id {}", driverType
                , maxRetry, event.getMessageBus().getKafka().getTopicId());
        if (maxRetry <= 0 || expire <= 0) {
            return;
        }

        MessageEvent event1 = new MessageEvent();
        event1.setTopicId(event.getMessageBus().getTopicId());
        event1.setBody(event.getMessageBus().getBody());
        event1.setMessageKey(event.getMessageBus().getMessageKey());
        event1.setSyncSendMessageTimeOutMills(event.getMessageBus().getSyncSendMessageTimeOutMills());
        event1.setMessageDriverMetadata(event.getMessageBus().toMetadata());
        event1.setCreatedTime(DateUtil.now());
        event1.setAsync(event.isAsync());
        event1.setMaxRetry(maxRetry);
        event1.setExpireDateTime(DateUtil.addSeconds(DateUtil.now(), expire));
        event1.setPartition(event.getMessageBus().getKafka().getPartition());

        long start = System.currentTimeMillis();
        SpringApplicationContext.getBean(MapLocalPersistence.class)
                .addToSetAndListWithTraction(event.getMessageBus().getTopicId(), event1.toBytes());

        LOGGER.info("send message to {} failed,need retry {},app id {} topic Id {},message store local ok,cost {} ms"
                , driverType, maxRetry, event.getMessageBus().getTopicId(), event.getMessageBus().getKafka().getTopicId()
                , System.currentTimeMillis() - start);
    }
}
