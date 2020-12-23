package com.hummer.common.coder;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public interface Coder{
    <T> T decodeWithBinary(byte[] bytes, Class<T> target) throws IOException;
    <T> byte[] encodeWithBinary(T t) throws JsonProcessingException;
}
