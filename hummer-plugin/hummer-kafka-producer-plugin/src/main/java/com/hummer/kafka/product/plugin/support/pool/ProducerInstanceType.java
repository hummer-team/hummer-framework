package com.hummer.kafka.product.plugin.support.pool;

/**
 * producer instance type
 *
 * @author bingy
 */
public enum ProducerInstanceType {
    /**
     * single
     */
    SINGLE,
    /**
     * thread local producer
     */
    THREAD_LOCAL_PRODUCER,
    /**
     * key shared producer，recommend key for app id or topic id
     */
    KEY_SHARED_PRODUCER
}
