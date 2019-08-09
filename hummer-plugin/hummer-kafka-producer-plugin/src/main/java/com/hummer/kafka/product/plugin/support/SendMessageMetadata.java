package com.hummer.kafka.product.plugin.support;


import lombok.Builder;
import lombok.Data;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/8 17:09
 **/
@Builder
@Data
public class SendMessageMetadata {
    private long sendMessageTimeOutMills;
    private long closeProducerTimeOutMillis;
}
