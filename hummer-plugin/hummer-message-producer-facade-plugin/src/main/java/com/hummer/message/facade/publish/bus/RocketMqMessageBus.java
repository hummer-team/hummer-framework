package com.hummer.message.facade.publish.bus;

import com.google.common.base.Strings;
import com.hummer.message.facade.metadata.MessagePublishMetadataKey;
import com.hummer.message.facade.metadata.RocketMqMessageMetadata;
import com.hummer.message.facade.publish.BaseMessageBusTemplate;
import com.hummer.message.facade.publish.MessageBus;
import com.hummer.rocketmq.product.plugin.domain.RocketMqProducerMetadata;
import com.hummer.rocketmq.product.plugin.domain.RocketMqProduct;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:37
 **/
@Service(MessagePublishMetadataKey.ROCKETMQ_MESSAGE_DRIVER_NAME)
public class RocketMqMessageBus extends BaseMessageBusTemplate {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqMessageBus.class);
    @Autowired(required = false)
    private RocketMqProduct rocketMqProduct;

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
        if (Strings.isNullOrEmpty(messageBus.getRocketMq().getTag())) {
            LOGGER.warn("message not settings tag, recommend set business tag flag");
        }
        if (messageBus.getMessageKey() == null) {
            LOGGER.warn("message not settings id, recommend set business id flag");
        }
    }

    /**
     * send one message
     *
     * @param messageBus messageBus
     * @return void
     * @author liguo
     * @date 2019/8/5 14:26
     * @since 1.0.0
     **/
    @Override
    public void doSend(final MessageBus messageBus) {
        long start = System.currentTimeMillis();
        RocketMqProducerMetadata metadata = builderProducerMetadata(messageBus);
        try {
            rocketMqProduct.doSendBySync(metadata);
        } catch (Exception e) {
            callbackOfRocketMq(null, messageBus, e, start);
        }
    }

    @NotNull
    private RocketMqProducerMetadata builderProducerMetadata(MessageBus messageBus) {
        long start = System.currentTimeMillis();
        RocketMqProducerMetadata metadata = new RocketMqProducerMetadata();

        metadata.setBody(messageBus.getBody());
        metadata.setTag(messageBus.getRocketMq().getTag());
        metadata.setAck(messageBus.getRocketMq().isAck());
        metadata.setAffiliated(messageBus.getAffiliated());
        metadata.setDelayTimeLevel(messageBus.getRocketMq().getDelayLevel());
        metadata.setTopicId(messageBus.getTopicId());
        metadata.setTimeoutMills(messageBus.getSendTimeOutMills());
        metadata.setSerializerType(RocketMqMessageMetadata.get(messageBus.getTopicId()).getSerializerType());
        metadata.setSelectQueue(RocketMqMessageMetadata.get(messageBus.getTopicId()).getSelectorQueue());
        metadata.setSendCallback(new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                callbackOfRocketMq(sendResult, messageBus, null, start);
            }

            @Override
            public void onException(Throwable e) {
                callbackOfRocketMq(null, messageBus, e, start);
            }
        });
        return metadata;
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
        long start = System.currentTimeMillis();
        RocketMqProducerMetadata metadata = builderProducerMetadata(messageBus);
        try {
            rocketMqProduct.doSendByAsync(metadata);
        } catch (Exception e) {
            callbackOfRocketMq(null, messageBus, e, start);
        }
    }

    /**
     * if disabled then don't send message
     *
     * @param topicId topic id
     * @return if disabled then don't send message
     */
    @Override
    protected boolean enable(String topicId) {
        return RocketMqMessageMetadata.get(topicId).isEnable();
    }
}
