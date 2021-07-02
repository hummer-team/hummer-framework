package com.hummer.kafka.product.plugin.support.pool;

import com.hummer.common.exceptions.SysException;
import com.hummer.common.utils.FunctionUtil;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.kafka.product.plugin.domain.serializer.MessageBodyJsonSerializer;
import com.hummer.kafka.product.plugin.domain.serializer.MessageBodyThirftSerializer;
import com.hummer.kafka.product.plugin.support.producer.CloseableKafkaProducer;
import com.hummer.kafka.product.plugin.support.producer.SendMessageMetadata;
import joptsimple.internal.Strings;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteBufferSerializer;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * simple producer pool
 *
 * @author bingy
 */
public class ProducerPool {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProducerPool.class);

    private ProducerPool() {

    }

    /**
     * get producer instance from pool
     *
     * @return {@link com.hummer.kafka.product.plugin.support.producer.CloseableKafkaProducer<java.lang.String,java.lang.Object>}
     * @author liguo
     * @date 2019/8/12 14:14
     * @since 1.0.0
     **/
    public static CloseableKafkaProducer<String, Object> get(final String appId) {
        String singleType = "single";
        String type = PropertiesContainer.valueOfString(formatKey(String.format("%s.instance.scope.type", appId)
                , null)
                , singleType);

        LOGGER.debug("kafka producer instance scope type is {}", type);

        if (type == null || singleType.equalsIgnoreCase(type)) {
            return SingleProducer.get();
        }

        String preThreadType = "preThread";
        if (type.equalsIgnoreCase(preThreadType)) {
            return ThreadLocalProducer.get();
        }

        return KeySharedProducer.get(appId);
    }

    public static void refresh() {
        SingleProducer.refresh();
        ThreadLocalProducer.refresh();
        KeySharedProducer.refresh();
    }

    private static <K, V> CloseableKafkaProducer<K, V> producer() {
        return producer(null);
    }

    private static <K, V> CloseableKafkaProducer<K, V> producer(final String key) {
        // producer core configuration
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
                , PropertiesContainer
                        .valueOfStringWithAssertNotNull(formatKey(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
                                , key)));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, bodySerializer());


        Partitioner partitioner = SpringApplicationContext.getBeanWithNull(String.format("%s_PartitionsSerializer", key)
                , Partitioner.class);
        if (partitioner != null) {
            properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, Partitioner.class);
        }

        properties.put(ProducerConfig.ACKS_CONFIG, PropertiesContainer
                .valueOfString(formatKey(ProducerConfig.ACKS_CONFIG, key), "1"));

        FunctionUtil.actionByCondition(PropertiesContainer
                        .valueOfInteger(formatKey(ProducerConfig.BUFFER_MEMORY_CONFIG, key))
                , v -> v > 0
                , v -> properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, v));

        FunctionUtil.actionByCondition(PropertiesContainer
                        .valueOfInteger(formatKey(ProducerConfig.RETRIES_CONFIG, key))
                , v -> v > 0
                , v -> properties.put(ProducerConfig.RETRIES_CONFIG, v));

        FunctionUtil.actionByCondition(PropertiesContainer
                        .valueOfInteger(formatKey(ProducerConfig.BATCH_SIZE_CONFIG, key))
                , v -> v > 0
                , v -> properties.put(ProducerConfig.BATCH_SIZE_CONFIG, v));

        FunctionUtil.actionByCondition(PropertiesContainer
                        .valueOfInteger(formatKey(ProducerConfig.LINGER_MS_CONFIG, key))
                , v -> v > 0
                , v -> properties.put(ProducerConfig.LINGER_MS_CONFIG, v));

        FunctionUtil.actionByCondition(PropertiesContainer
                        .valueOfInteger(formatKey(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, key), 10485760)
                , v -> v > 0
                , v -> properties.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, v));

        FunctionUtil.actionByCondition(PropertiesContainer
                        .valueOfInteger(formatKey(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, key))
                , v -> v > 0
                , v -> properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, v));

        FunctionUtil.actionByCondition(PropertiesContainer
                        .valueOfString(formatKey(ProducerConfig.CLIENT_ID_CONFIG, key))
                , v -> !Strings.isNullOrEmpty(v)
                , v -> properties.put(ProducerConfig.CLIENT_ID_CONFIG, v));

        KafkaProducer<K, V> kafkaProducer = new KafkaProducer<>(properties);
        SendMessageMetadata sendMessageMetadata = SendMessageMetadata
                .builder()
                .sendMessageTimeOutMills(PropertiesContainer.valueOf(formatKey("send.timeout.mills", key)
                        , Long.class, 3000L))
                .closeProducerTimeOutMillis(PropertiesContainer.valueOf(formatKey("producer.close.timeout.mills"
                        , key)
                        , Long.class, 3000L))
                .build();
        LOGGER.info("closeable Kafka producer instance builder done.");
        return new CloseableKafkaProducer<K, V>(kafkaProducer, sendMessageMetadata);
    }

    private static String formatKey(final String key, final String prefix) {
        final String messageBusKeyPrefix = "hummer.message.kafka.producer";
        return
                Strings.isNullOrEmpty(prefix)
                        ? String.format("%s.%s", messageBusKeyPrefix, key)
                        : String.format("%s.%s.%s", messageBusKeyPrefix, prefix, key);
    }

    private static Class<?> bodySerializer() {
        final String fastJson = "fastJson";

        String bodySerializerType = PropertiesContainer.valueOfString(
                formatKey(String.format("%s", ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG), null)
                , fastJson);
        if (fastJson.equalsIgnoreCase(bodySerializerType)) {
            return MessageBodyJsonSerializer.class;
        }

        final String thirft = "thirft";
        if (thirft.equalsIgnoreCase(bodySerializerType)) {
            return MessageBodyThirftSerializer.class;
        }

        final String byteBuffer = "byteBuffer";
        if (byteBuffer.equalsIgnoreCase(bodySerializerType)) {
            return ByteBufferSerializer.class;
        }

        final String bytes = "bytes";
        if (bytes.equalsIgnoreCase(bodySerializerType)) {
            return BytesSerializer.class;
        }

        return MessageBodyJsonSerializer.class;
    }

    /**
     * single producer instance
     */
    public static class SingleProducer {
        private static final AtomicReference<CloseableKafkaProducer<String, Object>> producer =
                new AtomicReference<>(producer());

        private SingleProducer() {

        }

        public static CloseableKafkaProducer<String, Object> get() {
            CloseableKafkaProducer<String, Object> producerInstance = producer.get();
            if (producerInstance != null) {
                return producerInstance;
            }
            producer.compareAndSet(null, producer());
            return producer.get();
        }

        public static void refresh() {
            if (producer.get() == null) {
                return;
            }
            get();
        }
    }

    /**
     * per thread own producer instance
     */
    public static class ThreadLocalProducer {
        private static final ThreadLocal<CloseableKafkaProducer<String, Object>> threadLocal
                = ThreadLocal.withInitial(ProducerPool::producer);

        private ThreadLocalProducer() {

        }

        public static CloseableKafkaProducer<String, Object> get() {
            return threadLocal.get();
        }

        public static void remove() {
            threadLocal.remove();
        }

        public static void refresh() {
            remove();
        }
    }

    /**
     * the same key shard producer instance,recommend key for app id or topic id
     */
    public static class KeySharedProducer {
        private static final Map<String, CloseableKafkaProducer<String, Object>>
                MAP = new ConcurrentHashMap<>(16);

        private KeySharedProducer() {

        }

        public static CloseableKafkaProducer<String, Object> get(final String key) {
            if (Strings.isNullOrEmpty(key)) {
                throw new SysException(50000, "key is null,can not get producer instance");
            }

            return MAP.putIfAbsent(key, producer(key));
        }

        public static void remove() {
            MAP.clear();
        }

        public static void refresh() {
            if (MAP.isEmpty()) {
                return;
            }
            Map<String, CloseableKafkaProducer<String, Object>>
                    newMap = new ConcurrentHashMap<>(16);
            for (Map.Entry<String, CloseableKafkaProducer<String, Object>> entry : MAP.entrySet()) {
                entry.getValue().flush();
                newMap.put(entry.getKey(), producer(entry.getKey()));
            }

            MAP.clear();
            MAP.putAll(newMap);
        }
    }
}
