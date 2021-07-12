package com.hummer.rocketmq.product.plugin.domain.queue;

import com.google.common.hash.Hashing;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author lee
 */
public class SelectQueueByMessageKey implements MessageQueueSelector {
    @Override
    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
        int hash = Hashing.murmur3_32().hashString(String.valueOf(arg), StandardCharsets.UTF_8).asInt();
        if (hash < 0) {
            hash = Math.abs(hash);
        }

        hash = hash % mqs.size();
        return mqs.get(hash);
    }
}
