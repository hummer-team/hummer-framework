package com.hummer.common.coder;

import com.hummer.core.PropertiesContainer;
import io.protostuff.JsonIOUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffCoder {
    private static final Map<Class<?>, Schema<?>> SCHEMA_MAP = new ConcurrentHashMap<>();

    private ProtostuffCoder() {

    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) SCHEMA_MAP.get(clazz);
        if (Objects.isNull(schema)) {
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                SCHEMA_MAP.putIfAbsent(clazz, schema);
            }
        }
        return schema;
    }

    public static <T> byte[] encodeWithJson(T t) {
        Schema<T> schema = getSchema((Class<T>) t.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate(getBufferSize());
        byte[] data;
        try {
            data = JsonIOUtil.toByteArray(t, schema, true, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    public static <T> T decodeWithJson(byte[] bytes, Class<T> target) throws IOException {
        Schema<T> schema = getSchema(target);
        T obj = schema.newMessage();
        JsonIOUtil.mergeFrom(bytes, obj, schema, true);
        return obj;
    }

    public static <T> byte[] encode(T t) {
        Class<T> clazz = (Class<T>) t.getClass();
        Schema<T> schema = getSchema(clazz);
        LinkedBuffer buffer = LinkedBuffer.allocate(getBufferSize());
        byte[] data;
        try {
            data = ProtobufIOUtil.toByteArray(t, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    /**
     * Decode the given byte array into an object of type {@code R} with the default charset.
     *
     * @param bytes   origin input bytes
     * @param target  target object class
     * @return
     */
    public static <T> T decode(byte[] bytes, Class<T> target) {
        Schema<T> schema = getSchema(target);
        T obj = schema.newMessage();
        ProtobufIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    private static int getBufferSize() {
        return PropertiesContainer.valueOfInteger("hummer.http.message.protostuff.buffer.size"
                , LinkedBuffer.DEFAULT_BUFFER_SIZE * 2);
    }
}
