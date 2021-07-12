package com.hummer.rocketmq.product.plugin.domain.serializer;

public class MessageSerializerFactory {

    public static <T> Serializer<T> factory(String serializerType) {
        if ("fastjson".equalsIgnoreCase(serializerType)) {
            return MessageJsonSerializer.INSTANCE;
        }
        // TODO: 2021/7/9 add other serializer

        //default is fastjson serializer
        return MessageJsonSerializer.INSTANCE;
    }
}
