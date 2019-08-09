package com.hummer.kafka.product.plugin.domain.serializer;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * this class implement key mod partition size
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/9 16:54
 **/
public class PartitionsModSerializer implements Partitioner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartitionsModSerializer.class);

    /**
     * Compute the partition for the given record.
     *
     * @param topic      The topic name
     * @param key        The key to partition on (or null if no key)
     * @param keyBytes   The serialized key to partition on( or null if no key)
     * @param value      The value to partition on or null
     * @param valueBytes The serialized value to partition on or null
     * @param cluster    The current cluster metadata
     */
    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        int partitionInfoSize = cluster.partitionCountForTopic(topic);
        int index = Math.abs(key.hashCode() % partitionInfoSize);
        LOGGER.debug("message topic {} key {} assign to {} partition", topic, key, index);
        return index;
    }

    /**
     * This is called when partitioner is closed.
     */
    @Override
    public void close() {

    }

    /**
     * Configure this class with the given key-value pairs
     *
     * @param configs
     */
    @Override
    public void configure(Map<String, ?> configs) {

    }
}
