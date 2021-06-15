package com.hummer.first.restfull.plugin;

/**
 * @author edz
 */
public interface CustomParseRespProvider<T> {
    /**
     * parse remote api response.
     *
     * @param response string of json
     * @return T
     */
    T parse(String response);
}
