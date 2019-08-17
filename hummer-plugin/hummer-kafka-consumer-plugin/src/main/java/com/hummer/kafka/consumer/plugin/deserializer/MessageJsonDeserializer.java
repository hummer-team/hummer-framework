package com.hummer.kafka.consumer.plugin.deserializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * this class implement message body json deserializer
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/12 17:07
 **/
public class MessageJsonDeserializer<T> implements Deserializer<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageJsonDeserializer.class);

    /**
     * Configure this class.
     *
     * @param configs configs in key/value pairs
     * @param isKey   whether is for key or value
     */
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    /**
     * Deserialize a record value from a byte array into a value or object.
     *
     * @param topic topic associated with the data
     * @param data  serialized bytes; may be null; implementations are recommended to handle null by returning a value or null rather than throwing an exception.
     * @return deserialized typed data; may be null
     */
    @Override
    public T deserialize(String topic, byte[] data) {
        LOGGER.debug("topic {} message body deserializer size {} bytes", topic, data.length);
        return JSON.parseObject(data, new TypeReference<T>() {
        }.getType());
    }

    @Override
    public void close() {

    }
}
