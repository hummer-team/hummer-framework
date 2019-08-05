package com.hummer.message.facade.metadata;

import com.hummer.common.utils.SupplierUtil;
import com.hummer.spring.plugin.context.PropertiesContainer;
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

    @SuppressWarnings("unchecked")
    public KafkaMessageMetadata(String appId) {
        super(appId);
        this.partitionsSerializer = SupplierUtil.with(
                () -> PropertiesContainer.valueOf(formatKey(appId, "serializer.partitions")
                        , Partitioner.class
                        , null)
                , Objects::nonNull
                , () -> PropertiesContainer.valueOf(formatKeyByDefault("serializer.partitions")
                        , Partitioner.class, null));

        this.valueSerializer = SupplierUtil.with(
                () -> PropertiesContainer.valueOf(formatKey(appId, "serializer.value")
                        , Serializer.class
                        , null)
                , Objects::nonNull
                , () -> PropertiesContainer.valueOf(formatKeyByDefault("serializer.value")
                        , Serializer.class, null));
    }
}
