package com.hummer.message.facade.metadata;

import lombok.Getter;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 14:28
 **/
@Getter
public class KafkaMessageMetadata<T> extends MessagePublishMetadata {
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
        return metadata;
    }
}
