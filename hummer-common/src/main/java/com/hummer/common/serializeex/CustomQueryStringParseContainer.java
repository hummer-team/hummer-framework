package com.hummer.common.serializeex;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/7 16:59
 **/
public class CustomQueryStringParseContainer {
    /**
     * story query string parse
     **/
    private static final Map<String, CustomQueryStringParse> PARSE_HASH_MAP = Maps.newHashMapWithExpectedSize(7);

    /**
     * 注册自定义解析器
     *
     * @param fieldType 要解析的字段类型
     * @param parse
     * @return void
     * @author liguo
     * @date 2018/12/7 17:04
     * @version 1.0.0
     **/
    public static void register(Class<?> fieldType, CustomQueryStringParse parse) {
        PARSE_HASH_MAP.put(fieldType.getName(), parse);
    }

    /**
     * 获取自定义解析器
     *
     * @param fieldType 要解析的字段类型
     * @return com.yeshj.classs.learning.reservation.support.serializeex.CustomQueryStringParse
     * @author liguo
     * @date 2018/12/7 17:06
     * @version 1.0.0
     **/
    public static CustomQueryStringParse getParse(Class<?> fieldType) {
        return PARSE_HASH_MAP.get(fieldType.getName());
    }
}
