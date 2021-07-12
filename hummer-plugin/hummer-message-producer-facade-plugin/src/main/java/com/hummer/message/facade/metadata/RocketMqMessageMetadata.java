package com.hummer.message.facade.metadata;


import com.hummer.core.PropertiesContainer;
import lombok.Getter;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 14:28
 **/
@Getter
public class RocketMqMessageMetadata extends MessagePublishMetadata {
    private String selectorQueue;

    public RocketMqMessageMetadata() {

    }

    public static RocketMqMessageMetadata get(final String topicId) {
        return get(topicId, () -> builderMetadata(topicId));
    }

    private static RocketMqMessageMetadata builderMetadata(final String topicId) {
        RocketMqMessageMetadata metadata = new RocketMqMessageMetadata();
        metadata.builder(topicId);
        setProperties(metadata, topicId);
        return metadata;
    }

    private static void setProperties(RocketMqMessageMetadata metadata, String topicId) {
        metadata.selectorQueue = PropertiesContainer.valueOfString(formatKey(topicId, "selector.queue"
                , metadata.getDriverType()));
    }
}
