package com.hummer.common.coder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.hummer.common.exceptions.SysException;
import org.springframework.http.MediaType;

import java.io.IOException;

public enum CoderEnum implements Coder {
    /**
     * MSG_PACK_BINARY
     */
    MSG_PACK_BINARY(new MediaType("application", "x-msgpack-binary")) {
        @Override
        public <T> T decodeWithBinary(byte[] bytes, Class<T> target) throws IOException {
            return MsgPackCoder.decodeWithBinary(bytes, target);
        }

        @Override
        public <T> byte[] encodeWithBinary(T t) {
            try {
                return MsgPackCoder.encodeWithBinary(t);
            } catch (JsonProcessingException e) {
                throw new SysException(e);
            }
        }

        @Override
        public <T> T decodeWithBinary(byte[] bytes, TypeReference<T> reference) throws IOException {
            return MsgPackCoder.decodeWithBinary(bytes, reference);
        }
    },
    /**
     * MSG_PACK_JSON
     */
    MSG_PACK_JSON(new MediaType("application", "x-msgpack-json")) {
        @Override
        public <T> T decodeWithJson(byte[] bytes, Class<T> target) throws IOException {
            return MsgPackCoder.decodeWithJson(bytes, target);
        }

        @Override
        public <T> T decodeWithJson(byte[] bytes, TypeReference<T> reference) throws IOException {
            return MsgPackCoder.decodeWithJson(bytes, reference);
        }

        @Override
        public <T> byte[] encodeWithJson(T t) throws IOException {
            return MsgPackCoder.encodeWithJson(t);
        }
    },
    /**
     * PROTOSTUFF_BINARY
     */
    PROTOSTUFF_BINARY(new MediaType("application", "x-protostuff-binary")) {
        @Override
        public <T> T decodeWithBinary(byte[] bytes, Class<T> target) throws IOException {
            return ProtostuffCoder.decode(bytes, target);
        }

        @Override
        public <T> byte[] encodeWithBinary(T t) {
            return ProtostuffCoder.encode(t);
        }
    },
    /**
     * PROTOSTUFF_JSON
     */
    PROTOSTUFF_JSON(new MediaType("application", "x-protostuff-json")) {
        @Override
        public <T> T decodeWithJson(byte[] bytes, Class<T> target) throws IOException {
            return ProtostuffCoder.decodeWithJson(bytes, target);
        }

        @Override
        public <T> byte[] encodeWithJson(T t) {
            return ProtostuffCoder.encodeWithJson(t);
        }
    },
    /**
     * FAST_JSON
     */
    FAST_JSON(new MediaType("application", "json"));

    private MediaType mediaType;

    CoderEnum(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public static CoderEnum getCoderByName(String coderName) {
        for (CoderEnum c : CoderEnum.values()) {
            if (c.name().equalsIgnoreCase(coderName)) {
                return c;
            }
        }

        return FAST_JSON;
    }

    public MediaType getMediaType() {
        return mediaType;
    }
}
