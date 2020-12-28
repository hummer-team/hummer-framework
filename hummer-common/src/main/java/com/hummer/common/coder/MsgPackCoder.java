package com.hummer.common.coder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * wrapper package coder.
 *
 * @author edz
 */
public class MsgPackCoder {
    private static final ObjectMapper OBJECT_MAPPER_BINARY = new ObjectMapper(new MessagePackFactory());
    private static final ObjectMapper OBJECT_MAPPER_JSON = new ObjectMapper();
    private static final Map<String, JavaType> JAVA_TYPE_HASH_MAP = new java.util.concurrent.ConcurrentHashMap<>();

    private MsgPackCoder() {

    }


    public static <T> T decodeWithBinary(byte[] bytes, Class<T> target) throws IOException {
        return OBJECT_MAPPER_BINARY.readValue(bytes, target);
    }

    public static <T> T decodeWithBinary(byte[] bytes, TypeReference<T> reference) throws IOException {
        return OBJECT_MAPPER_BINARY.readValue(bytes, reference);
    }

    public static <T> byte[] encodeWithBinary(T t) throws JsonProcessingException {
        return OBJECT_MAPPER_BINARY.writer().writeValueAsBytes(t);
    }

    public static <T> T decodeWithJson(byte[] bytes, Class<T> target) throws IOException {
        return OBJECT_MAPPER_JSON.readValue(bytes, target);
    }

    public static <T> T decodeWithJson(byte[] bytes, TypeReference<T> reference) throws IOException {
        return OBJECT_MAPPER_JSON.readValue(bytes, reference);
    }

    public static <T> byte[] encodeWithJson(T t) throws JsonProcessingException {
        return OBJECT_MAPPER_JSON.writer().writeValueAsBytes(t);
    }

    public static JavaType getJavaType(Type type, @Nullable Class<?> contextClass) {
        JavaType javaType = JAVA_TYPE_HASH_MAP.get(type.getTypeName());
        if (javaType == null) {
            javaType = JAVA_TYPE_HASH_MAP.get(type.getTypeName());
            if (javaType == null) {
                TypeFactory typeFactory = OBJECT_MAPPER_JSON.getTypeFactory();
                javaType = typeFactory.constructType(GenericTypeResolver.resolveType(type, contextClass));
                JAVA_TYPE_HASH_MAP.putIfAbsent(type.getTypeName(), javaType);
            }
        }

        return javaType;
    }

    public static SerializationConfig getSerializationConfigForJson() {
        return OBJECT_MAPPER_JSON.getSerializationConfig();
    }

    public static SerializationConfig getSerializationConfigForBinary() {
        return OBJECT_MAPPER_BINARY.getSerializationConfig();
    }

    public static void setSerializationConfigOfJson(SerializationConfig config){
        OBJECT_MAPPER_JSON.setConfig(config);
    }

    public static DeserializationConfig getDeserializationConfigForJson() {
        return OBJECT_MAPPER_JSON.getDeserializationConfig();
    }

    public static DeserializationConfig getDeserializationConfigForBinary() {
        return OBJECT_MAPPER_BINARY.getDeserializationConfig();
    }

    public static Object readJavaTypeWithJson(JavaType javaType, HttpInputMessage inputMessage) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        try {
            if (inputMessage instanceof MappingJacksonInputMessage) {
                Class<?> deserializationView = ((MappingJacksonInputMessage) inputMessage).getDeserializationView();
                if (deserializationView != null) {
                    ObjectReader objectReader = OBJECT_MAPPER_JSON.readerWithView(deserializationView).forType(javaType);

                    Reader reader = new InputStreamReader(inputMessage.getBody(), charset);
                    return objectReader.readValue(reader);
                }
            }
            Reader reader = new InputStreamReader(inputMessage.getBody(), charset);
            return OBJECT_MAPPER_JSON.readValue(reader, javaType);

        } catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotReadableException("JSON parse error: " + ex.getOriginalMessage(), ex, inputMessage);
        }
    }

    public static Object readJavaTypeWithBinary(JavaType javaType, byte[] bytes) throws IOException {
        try {
            return OBJECT_MAPPER_BINARY.readValue(bytes, javaType);
        } catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotReadableException("JSON parse error: " + ex.getOriginalMessage(), ex);
        }
    }

    public static Object readJavaTypeWithJson(JavaType javaType, byte[] bytes) throws IOException {
        try {
            return OBJECT_MAPPER_JSON.readValue(bytes, javaType);
        } catch (InvalidDefinitionException ex) {
            throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
        } catch (JsonProcessingException ex) {
            throw new HttpMessageNotReadableException("JSON parse error: " + ex.getOriginalMessage(), ex);
        }
    }

    private Object readJavaTypeWithJson(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage)
            throws IOException {
        return readJavaTypeWithJson(getJavaType(type, contextClass), inputMessage);
    }
}
