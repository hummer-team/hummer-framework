package com.hummer.common.serializeex;

/**
 * query string parameter
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/7 16:54
 **/
public interface CustomQueryStringParse {
    /**
     * parse
     *
     * @param value          query string value
     * @param fieldName      parameter name
     * @param fieldTypeClass parameter type
     * @return T
     * @author liguo
     * @date 2018/12/7 16:57
     * @version 1.0.0
     **/
    <T> T parseValue(Object value, String fieldName, Class<?> fieldTypeClass);
}
