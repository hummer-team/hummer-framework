package com.hummer.rocketmq.product.plugin.support;

import com.google.common.base.Strings;
import com.hummer.core.PropertiesContainer;
import lombok.Getter;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author lee
 */
public class RocketMqProducerPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqProducerPool.class);

    private RocketMqProducerPool() {

    }

    private static RocketMqProducer builder(String topicId) {
        RocketMqMetadata mqMetadata = new RocketMqMetadata(topicId);

        DefaultMQProducer mqProducer = new DefaultMQProducer(null, mqMetadata.getProducerGroupName()
                , RPCHookImpl.INSTANCE, mqMetadata.isTrace(), null);

        mqProducer.setNamesrvAddr(mqMetadata.getNameServer());
        mqProducer.setSendLatencyFaultEnable(true);
        mqProducer.setVipChannelEnabled(false);
        mqProducer.setSendMessageWithVIPChannel(false);

        LOGGER.info("rocketMq metadata {}", mqMetadata);

        return new RocketMqProducer(mqProducer);
    }

    private static String formatKey(final String key, final String prefix) {
        return Strings.isNullOrEmpty(prefix)
                ? String.format("hummer.message.rocketmq.producer.%s", key)
                : String.format("hummer.message.rocketmq.producer.%s.%s", prefix, key);
    }

    public static RocketMqProducer get() {
        return SingleProducer.get();
    }

    public static RocketMqMetadata getMetadata(String topicId) {
        return new RocketMqMetadata(topicId);
    }

    @Getter
    public static class RocketMqMetadata {
        private final String producerGroupName;
        private final boolean trace;
        private final int sendFailRetryCount;
        private final int sentMsgTimeoutMills;
        private final int defTopicQueueCount;
        private final int compressMsgBodyOverLimit;
        private final int maxMessageSize;
        private final String nameServer;

        public RocketMqMetadata(String topicId) {
            this.nameServer = PropertiesContainer.valueOfStringWithAssertNotNull("hummer.message.rocketmq.producer.nameserver");

            this.producerGroupName = PropertiesContainer.valueOfString("hummer.message.rocketmq.producer.group"
                    , PropertiesContainer.valueOfString("spring.application.name"));

            this.trace = PropertiesContainer.valueOf(formatKey("trace", topicId), Boolean.class
                    , PropertiesContainer.valueOf(formatKey("trace", null), Boolean.class, true));

            this.sendFailRetryCount = PropertiesContainer.valueOfInteger(formatKey("sendfailed.count", topicId)
                    , PropertiesContainer.valueOfInteger("hummer.message.rocketmq.producer.sendfailed.count", 2));

            this.sentMsgTimeoutMills = PropertiesContainer.valueOfInteger(formatKey("send.timeout.mills", topicId)
                    , PropertiesContainer.valueOfInteger(formatKey("send.timeout.mills", null), 3000));

            this.defTopicQueueCount = PropertiesContainer.valueOfInteger(formatKey("topic.queue.count", topicId)
                    , PropertiesContainer.valueOfInteger(formatKey("topic.queue.count", null), 4));

            this.compressMsgBodyOverLimit = PropertiesContainer.valueOfInteger(formatKey("compress.over.limit", topicId)
                    , PropertiesContainer.valueOfInteger(formatKey("compress.over.limit", null), 1024 * 4));

            this.maxMessageSize = PropertiesContainer.valueOfInteger(formatKey("message.max.size", topicId)
                    , PropertiesContainer.valueOfInteger(formatKey("message.max.size", null), 1024 * 1024 * 4));
        }

        @Override
        public String toString() {
            return "RocketMqMetadata \n{" +
                    "\n producerGroupName=" + producerGroupName +
                    "\n trace=" + trace +
                    "\n sendFailRetryCount=" + sendFailRetryCount +
                    "\n sentMsgTimeoutMills=" + sentMsgTimeoutMills +
                    "\n defTopicQueueCount=" + defTopicQueueCount +
                    "\n compressMsgBodyOverLimit=" + compressMsgBodyOverLimit +
                    "\n maxMessageSize=" + maxMessageSize +
                    "\n nameServer=" + nameServer +
                    "\n}";
        }
    }

    private static class SingleProducer {
        private static final AtomicReference<RocketMqProducer> producerRef = new AtomicReference<>();

        private SingleProducer() {

        }

        public static RocketMqProducer get() {
            RocketMqProducer producer = producerRef.get();
            if (producer != null) {
                return producer;
            }

            producerRef.compareAndSet(null, builder(null));
            return producerRef.get();
        }
    }
}
