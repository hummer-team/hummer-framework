package com.hummer.rocketmq.product.plugin.domain;

/**
 * Selector queue strategy
 *
 * @author lee
 */
public enum SelectorQueueEnum {
    BY_HASH,
    BY_MACHINE_ROOM,
    BY_RANDOM,
    BY_MESSAGE_KEY_HASH_MOD
}
