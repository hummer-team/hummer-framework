package com.hummer.kafka.consumer.plugin.consumer;

import lombok.Builder;
import lombok.Getter;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/26 18:14
 **/
@Builder
@Getter
public class RecordMetadata {
    private int partition;
    private long offset;
    private String topicId;
}
