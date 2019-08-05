package com.hummer.message.facade.metadata;

import com.hummer.message.facade.publish.PublishMessageExceptionCallback;
import lombok.Getter;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.serialization.Serializer;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 14:28
 **/
@Getter
public class KafkaMessageMetadata<T> extends MessagePublishMetadata {
    private Partitioner partitionsSerializer;
    private Serializer<T> valueSerializer;

    public KafkaMessageMetadata(Partitioner partitionsSerializer
            , Serializer<T> valueSerializer
            , String appId
            , int perSecondSemaphore
            , String address
            , PublishMessageExceptionCallback callback) {
        super(appId, perSecondSemaphore, address, callback);
        this.partitionsSerializer = partitionsSerializer;
        this.valueSerializer = valueSerializer;
    }
}
