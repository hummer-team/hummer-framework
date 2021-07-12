package com.hummer.kafka.consumer.plugin.properties;

import com.google.common.base.Preconditions;
import com.hummer.common.utils.FunctionUtil;
import com.hummer.core.PropertiesContainer;
import com.hummer.kafka.consumer.plugin.deserializer.MessageJsonDeserializer;
import joptsimple.internal.Strings;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteBufferDeserializer;
import org.apache.kafka.common.serialization.BytesDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * kafka consumer properties
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/12 16:56
 **/
public class ConsumerProperties {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerProperties.class);

    private ConsumerProperties() {

    }

    /**
     * builder consumer properties
     *
     * @param consumerGroupName group name , must settings
     * @return java.util.Properties
     * @author liguo
     * @date 2019/8/12 17:24
     * @since 1.0.0
     **/
    public static Properties builderProperties(final String consumerGroupName) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(consumerGroupName)
                , "please set consumer group name");

        Properties properties = new Properties();
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupName);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, PropertiesContainer
                .valueOfStringWithAssertNotNull(String.format("hummer.message.kafka.consumer.%s"
                        , ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG)));

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, bodyDeserializer());
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG
                , PropertiesContainer.valueOfString(formatKey(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, null)
                        , "earliest"));

        FunctionUtil.actionByCondition(PropertiesContainer.valueOfInteger(
                formatKey(ConsumerConfig.FETCH_MAX_BYTES_CONFIG))
                , v -> v > 0
                , v -> properties.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, v));

        FunctionUtil.actionByCondition(PropertiesContainer.valueOfInteger(
                formatKey(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG))
                , v -> v > 0
                , v -> properties.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, v));

        FunctionUtil.actionByCondition(PropertiesContainer.valueOfInteger(
                formatKey(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG))
                , v -> v > 0
                , v -> properties.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, v));

        FunctionUtil.actionByCondition(PropertiesContainer.valueOfInteger(
                formatKey(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG))
                , v -> v > 0
                , v -> properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, v));
        LOGGER.debug("kafka consumer properties is \n {}", properties);
        return properties;
    }

    private static Class<?> bodyDeserializer() {
        final String fastJson = "fastJson";

        String bodySerializerType = PropertiesContainer.valueOfString(
                formatKey(String.format("%s", ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG), null)
                , fastJson);
        if (fastJson.equalsIgnoreCase(bodySerializerType)) {
            return MessageJsonDeserializer.class;
        }


        final String byteBuffer = "byteBuffer";
        if (byteBuffer.equalsIgnoreCase(bodySerializerType)) {
            return ByteBufferDeserializer.class;
        }

        final String bytes = "bytes";
        if (bytes.equalsIgnoreCase(bodySerializerType)) {
            return BytesDeserializer.class;
        }

        return MessageJsonDeserializer.class;
    }

    private static String formatKey(final String key) {
        return formatKey(key, null);
    }

    private static String formatKey(final String key, final String prefix) {
        final String messageBusKeyPrefix = "hummer.message.kafka.consumer";
        return
                Strings.isNullOrEmpty(prefix)
                        ? String.format("%s.%s", messageBusKeyPrefix, key)
                        : String.format("%s.%s.%s", messageBusKeyPrefix, prefix, key);
    }
}
