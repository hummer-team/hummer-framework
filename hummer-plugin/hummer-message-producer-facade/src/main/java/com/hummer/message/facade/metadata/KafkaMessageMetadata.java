package com.hummer.message.facade.metadata;

import com.hummer.common.utils.SupplierUtil;
import com.hummer.core.PropertiesContainer;
import lombok.Getter;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Objects;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 14:28
 **/
@Getter
public class KafkaMessageMetadata<T> extends MessagePublishMetadata {
    private Partitioner partitionsSerializer;
    private Serializer<T> valueSerializer;


    public KafkaMessageMetadata() {

    }

    /**
     * get kafka publish message metadata
     *
     * @param appId app id
     * @return {@link com.hummer.message.facade.metadata.KafkaMessageMetadata}
     * @author liguo
     * @date 2019/8/6 18:26
     * @since 1.0.0
     **/
    public static KafkaMessageMetadata getKafkaMessageMetadata(final String appId) {
        return get(appId, () -> builderKafkaMetadata(appId));
    }

    @SuppressWarnings("unchecked")
    private static KafkaMessageMetadata builderKafkaMetadata(final String appId) {
        KafkaMessageMetadata metadata = new KafkaMessageMetadata();
        metadata.builder(appId);
        metadata.partitionsSerializer = SupplierUtil.with(
                () -> PropertiesContainer.valueOf(formatKey(appId, "serializer.partitions")
                        , Partitioner.class
                        , null)
                , Objects::nonNull
                , () -> PropertiesContainer.valueOf(formatKeyByDefault("serializer.partitions")
                        , Partitioner.class, null));

        metadata.valueSerializer = SupplierUtil.with(
                () -> PropertiesContainer.valueOf(formatKey(appId, "serializer.value")
                        , Serializer.class
                        , null)
                , Objects::nonNull
                , () -> PropertiesContainer.valueOf(formatKeyByDefault("serializer.value")
                        , Serializer.class, null));

        return metadata;
    }
}
