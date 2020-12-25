package com.hummer.common.coder;

import com.fasterxml.jackson.core.type.TypeReference;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

public interface Coder {
    default <T> T decodeWithBinary(byte[] bytes, Class<T> target) throws IOException {
        throw new NotImplementedException();
    }

    default <T> byte[] encodeWithBinary(T t) {
        throw new NotImplementedException();
    }

    default <T> T decodeWithJson(byte[] bytes, Class<T> target) throws IOException {
        throw new NotImplementedException();
    }

    default <T> T decodeWithJson(byte[] bytes, TypeReference<T> reference) throws IOException {
        throw new NotImplementedException();
    }

    default <T> byte[] encodeWithJson(T t) throws IOException {
        throw new NotImplementedException();
    }

    default <T> T decodeWithBinary(byte[] bytes, TypeReference<T> reference) throws IOException {
        throw new NotImplementedException();
    }
}
