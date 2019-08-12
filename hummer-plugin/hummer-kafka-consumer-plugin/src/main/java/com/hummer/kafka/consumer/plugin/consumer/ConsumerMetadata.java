package com.hummer.kafka.consumer.plugin.consumer;

import com.hummer.kafka.consumer.plugin.handle.HandleBusiness;
import lombok.Builder;
import lombok.Getter;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/12 17:56
 **/
@Builder
@Getter
public class ConsumerMetadata {
    private String topicId;
    private String groupName;
    private long pollTimeOutMillis;
    private HandleBusiness handleBusiness;
}
