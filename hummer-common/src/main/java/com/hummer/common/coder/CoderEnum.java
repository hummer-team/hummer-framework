package com.hummer.common.coder;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.MediaType;

import java.io.IOException;

public enum CoderEnum implements Coder {
    MSG_PACK_BINARY(new MediaType("application", "x-msgpack-binary")) {
        @Override
        public <T> T decodeWithBinary(byte[] bytes, Class<T> target) throws IOException {
            return MsgPackCoder.decodeWithBinary(bytes,target);
        }
        @Override
        public <T> byte[] encodeWithBinary(T t) throws JsonProcessingException {
            return MsgPackCoder.encodeWithBinary(t);
        }
    },
    MSG_PACK_JSON(new MediaType("application", "x-msgpack-json")) {
        @Override
        public <T> T decodeWithBinary(byte[] bytes, Class<T> target) throws IOException {
            return null;
        }
        @Override
        public <T> byte[] encodeWithBinary(T t) {
            return new byte[0];
        }
    },
    PROTOSTUFF_BINARY(new MediaType("application", "x-protostuff-binary")) {
        @Override
        public <T> T decodeWithBinary(byte[] bytes, Class<T> target) throws IOException {
            return null;
        }
        @Override
        public <T> byte[] encodeWithBinary(T t) {
            return new byte[0];
        }
    },
    PROTOSTUFF_JSON(new MediaType("application", "x-protostuff-json")) {
        @Override
        public <T> T decodeWithBinary(byte[] bytes, Class<T> target) throws IOException {
            return null;
        }
        @Override
        public <T> byte[] encodeWithBinary(T t) {
            return new byte[0];
        }
    };

    private MediaType mediaType;

    CoderEnum(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public MediaType getMediaType() {
        return mediaType;
    }
}
