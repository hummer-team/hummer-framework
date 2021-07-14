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

        if (event.getException() == null) {
            return;
        }

        String driverType = PropertiesContainer.valueOfString(HUMMER_MESSAGE_DRIVER_TYPE_KEY
                , HUMMER_MESSAGE_DRIVER_TYPE_KAFKA_KEY);

        String retryKeyByTopic = String.format("hummer.message.%s.producer.%s.send.failed.retry.max"
                , driverType, event.getMessageBus().getTopicId());
        String retryKeyByDef = String.format("hummer.message.%s.producer.send.failed.retry.max", driverType);
        int maxRetry = PropertiesContainer.valueOfInteger(retryKeyByTopic
                , () -> PropertiesContainer.valueOfInteger(retryKeyByDef, 0));

        String expireKeyByTopic = String.format("hummer.message.%s.producer.%s.failed.message.local.store.second"
                , driverType, event.getMessageBus().getTopicId());
        String expireKeyByDef = String.format("hummer.message.%s.producer.failed.message.local.store.second", driverType);
        int expire = PropertiesContainer.valueOfInteger(expireKeyByTopic
                , () -> PropertiesContainer.valueOfInteger(expireKeyByDef, 0));

        LOGGER.info("send message to {} failed,need retry {} topic Id {} message key {}", driverType
                , maxRetry, event.getMessageBus().getTopicId(), event.getMessageBus().getMessageKey());
        if (maxRetry <= 0 || expire <= 0) {
            return;
        }

        MessageEvent event1 = new MessageEvent();
        event1.setTopicId(event.getMessageBus().getTopicId());
        event1.setBody(event.getMessageBus().getBody());
        event1.setMessageKey(event.getMessageBus().getMessageKey());
        event1.setSyncSendMessageTimeOutMills(event.getMessageBus().getSendTimeOutMills());
        event1.setMessageAffiliateData(event.getMessageBus().getAffiliated());
        event1.setCreatedTime(DateUtil.now());
        event1.setAsync(event.isAsync());
        event1.setMaxRetry(maxRetry);
        event1.setExpireDateTime(DateUtil.addSeconds(DateUtil.now(), expire));
        event1.setPartition(event.getPartition());
        event1.setBusDriverType(event.getBusDriverType());
        event1.setTag(event.getTag());
        event1.setDelayLevel(event.getDelayLevel());
        event1.setAck(event.isAck());

        long start = System.currentTimeMillis();
        SpringApplicationContext.getBean(MapLocalPersistence.class)
                .addToSetAndListWithTraction(event.getMessageBus().getTopicId(), event1.toBytes());

        LOGGER.info("send message to {} failed,need retry {},app id {} topic Id {},message store local ok,cost {} ms"
                , driverType, maxRetry, event.getMessageBus().getTopicId(), event.getMessageBus().getTopicId()
                , System.currentTimeMillis() - start);
    }
}
