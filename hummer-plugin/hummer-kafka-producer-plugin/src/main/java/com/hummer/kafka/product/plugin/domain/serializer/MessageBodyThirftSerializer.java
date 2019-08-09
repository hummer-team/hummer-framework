package com.hummer.kafka.product.plugin.domain.serializer;

import com.hummer.common.exceptions.SysException;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TCompactProtocol;

import java.util.Map;

/**
 * implement message body  thrift serializer
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/9 11:12
 **/
public class MessageBodyThirftSerializer<T extends TBase> implements Serializer<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageBodyThirftSerializer.class);

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
        TSerializer serializer = new TSerializer(new TCompactProtocol.Factory());
        try {
            byte[] bytes = serializer.serialize(data);
            LOGGER.info("message topic {}, body thrift json serializer data size {} byte,cost time {} millis"
                    , bytes.length
                    , System.currentTimeMillis() - start);
            return bytes;
        } catch (TException e) {
            LOGGER.info("message topic {}, body thrift json serializer exception,cost time {} millis"
                    , e
                    , System.currentTimeMillis() - start);
            throw new SysException(50000, String.format("message topic %s body thrift Serializer failed", topic));
        }
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
