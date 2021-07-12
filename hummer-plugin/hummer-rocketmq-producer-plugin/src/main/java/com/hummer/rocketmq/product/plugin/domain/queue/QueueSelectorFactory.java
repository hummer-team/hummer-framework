package com.hummer.rocketmq.product.plugin.domain.queue;

import com.google.common.base.Strings;
import com.hummer.rocketmq.product.plugin.domain.SelectorQueueEnum;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByMachineRoom;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByRandom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * selector message queue
 *
 * @author lee
 */
public class QueueSelectorFactory {
    private static final Map<SelectorQueueEnum, MessageQueueSelector> QUEUE_MAP = new ConcurrentHashMap<>();

    static {
        QUEUE_MAP.put(SelectorQueueEnum.BY_HASH, new SelectMessageQueueByHash());
        QUEUE_MAP.put(SelectorQueueEnum.BY_RANDOM, new SelectMessageQueueByRandom());
        QUEUE_MAP.put(SelectorQueueEnum.BY_MACHINE_ROOM, new SelectMessageQueueByMachineRoom());
        QUEUE_MAP.put(SelectorQueueEnum.BY_MESSAGE_KEY_HASH_MOD, new SelectQueueByMessageKey());
    }

    public static MessageQueueSelector selector(final String selectQueueStrategy) {
        if (Strings.isNullOrEmpty(selectQueueStrategy)) {
            return null;
        }

        SelectorQueueEnum queueEnum = SelectorQueueEnum.valueOf(selectQueueStrategy);
        return QUEUE_MAP.get(queueEnum);
    }
}
