package com.hummer.kafka.product.plugin.domain.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * implement message body  json serializer
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/9 10:51
 **/
public class MessageBodyJsonSerializer<T> implements Serializer<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBodyJsonSerializer.class);

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
     * Convert {@code data} into a byte array.
     *
     * @param topic topic associated with data
     * @param data  typed data
     * @return serialized bytes
     */
    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null) {
            LOGGER.warn("message topic {} , body is null serializer 0 bytes data", topic);
            return new byte[0];
        }
        long start = System.currentTimeMillis();
        byte[] bytes = JSON.toJSONBytes(data, SerializerFeature.WriteNonStringValueAsString);
        LOGGER.info("message topic {}, body fast json serializer data size {} byte,cost time {} millis"
                , topic
                , bytes.length
                , System.currentTimeMillis() - start);
        return bytes;
    }

    /**
     * Close this serializer.
     * <p>
     * This method must be idempotent as it may be called multiple times.
     */
    @Override
    public void close() {

    }
}
