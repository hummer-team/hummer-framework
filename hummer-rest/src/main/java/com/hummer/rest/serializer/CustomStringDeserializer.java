package com.hummer.rest.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.serializer.StringCodec;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

/**
 * description java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 * </p>
 * @date 2020/6/24 12:34
 */

public class CustomStringDeserializer extends StringCodec {

    public static CustomStringDeserializer instance = new CustomStringDeserializer();

    @Override
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {

        T val = super.deserialze(parser, clazz, fieldName);
        if (val instanceof String) {
            return StringUtils.isEmpty((String) val) ? (T) null : (T) val;
        }
        return val;
    }

}
