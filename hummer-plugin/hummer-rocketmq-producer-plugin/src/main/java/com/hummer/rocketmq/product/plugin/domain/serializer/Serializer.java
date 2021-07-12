package com.hummer.rocketmq.product.plugin.domain.serializer;

public interface Serializer<T> {
    byte[] serializer(T data);
}
