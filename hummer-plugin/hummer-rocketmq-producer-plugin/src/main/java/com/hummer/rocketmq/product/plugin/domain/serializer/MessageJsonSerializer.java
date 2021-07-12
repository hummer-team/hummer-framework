package com.hummer.rocketmq.product.plugin.domain.serializer;

import com.alibaba.fastjson.JSON;

public class MessageJsonSerializer<T> implements Serializer<T> {

    public static final Serializer INSTANCE = new MessageJsonSerializer<>();

    @Override
    public byte[] serializer(T data) {
        return JSON.toJSONBytes(data);
    }
}
