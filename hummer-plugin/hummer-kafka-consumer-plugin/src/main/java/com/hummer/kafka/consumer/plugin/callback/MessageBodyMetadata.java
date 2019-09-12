package com.hummer.kafka.consumer.plugin.callback;

import lombok.Builder;
import lombok.Getter;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/12 18:04
 **/
@Builder
@Getter
public final class MessageBodyMetadata {
    private String key;
    private Object body;
    private String topicId;
    private long offset;
}
