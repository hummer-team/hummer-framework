package com.hummer.rocketmq.product.plugin.domain;

import com.hummer.rocketmq.product.plugin.domain.queue.QueueSelectorFactory;
import com.hummer.rocketmq.product.plugin.domain.serializer.MessageSerializerFactory;
import com.hummer.rocketmq.product.plugin.support.RocketMqProducerPool;
import org.apache.commons.collections.MapUtils;
import org.apache.rocketmq.common.message.Message;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Map;

/**
 * @author lee
 */
@Service
public class RocketMqProductService implements RocketMqProduct {
    @Override
    public void doSendBySync(RocketMqProducerMetadata metadata) throws Exception {
        Message message = builderMessage(metadata);
        RocketMqProducerPool.RocketMqMetadata metadata1 = RocketMqProducerPool.getMetadata(metadata.getTopicId());
        if (metadata.isOneway()) {
            RocketMqProducerPool
                    .get()
                    .setProperties(metadata1, true)
                    .sendOneway(message, QueueSelectorFactory.selector(metadata.getSelectQueue()));
        } else {
            RocketMqProducerPool
                    .get()
                    .setProperties(metadata1, true)
                    .send(message, QueueSelectorFactory.selector(metadata.getSelectQueue())
                            , timeoutMills(metadata.getTimeoutMills(), metadata1.getSentMsgTimeoutMills()));
        }
    }

    @Override
    public void doSendByAsync(RocketMqProducerMetadata metadata) throws Exception {
        Message message = builderMessage(metadata);
        RocketMqProducerPool.RocketMqMetadata metadata1 = RocketMqProducerPool.getMetadata(metadata.getTopicId());
        RocketMqProducerPool
                .get()
                .setProperties(metadata1, false)
                .sendAsync(message, QueueSelectorFactory.selector(metadata.getSelectQueue())
                        , metadata.getSendCallback()
                        , timeoutMills(metadata.getTimeoutMills(), metadata1.getSentMsgTimeoutMills()));
    }

    private long timeoutMills(long msgTimeout, long defTimeout) {
        return msgTimeout > 0 ? msgTimeout : defTimeout;
    }

    private Message builderMessage(final RocketMqProducerMetadata metadata) {
        Message message = new Message();
        message.setTopic(metadata.getTopicId());
        message.setKeys(metadata.getMessageKey());
        if (metadata.getDelayTimeLevel() > -1) {
            message.setDelayTimeLevel(metadata.getDelayTimeLevel());
        }
        message.setWaitStoreMsgOK(metadata.isAck());
        byte[] body = MessageSerializerFactory.factory(metadata.getSerializerType()).serializer(metadata.getBody());
        message.setBody(body);
        if (MapUtils.isNotEmpty(metadata.getAffiliated())) {
            for (Map.Entry<String, String> entry : metadata.getAffiliated().entrySet()) {
                message.putUserProperty(entry.getKey(), entry.getValue());
            }
        }
        message.setTags(metadata.getTag());
        return message;
    }

    @PreDestroy
    private void destroy() {
        RocketMqProducerPool.get().stop();
    }
}
