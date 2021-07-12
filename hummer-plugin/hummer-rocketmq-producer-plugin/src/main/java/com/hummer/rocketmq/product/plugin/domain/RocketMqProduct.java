package com.hummer.rocketmq.product.plugin.domain;

public interface RocketMqProduct {
    void doSendBySync(RocketMqProducerMetadata metadata) throws Exception;
    void doSendByAsync(RocketMqProducerMetadata metadata) throws Exception;
}
