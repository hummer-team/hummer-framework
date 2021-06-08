package com.hummer.first.restfull.plugin;

/**
 * @author edz
 */
public interface CustomParseResp<T> {
    T parse(String response);
}
