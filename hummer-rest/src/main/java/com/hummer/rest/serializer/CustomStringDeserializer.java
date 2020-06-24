package com.hummer.rest.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.StringCodec;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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

public class CustomStringDeserializer implements ObjectSerializer, ObjectDeserializer {

    public static CustomStringDeserializer instance = new CustomStringDeserializer();

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
            throws IOException {
        write(serializer, (String) object);
    }

    public void write(JSONSerializer serializer, String value) {
        SerializeWriter out = serializer.out;

        if (value == null) {
            out.writeNull(SerializerFeature.WriteNullStringAsEmpty);
            return;
        }

        out.writeString(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        if (clazz == StringBuffer.class) {
            final JSONLexer lexer = parser.lexer;
            if (lexer.token() == JSONToken.LITERAL_STRING) {
                String val = lexer.stringVal();
                lexer.nextToken(JSONToken.COMMA);

                return (T) new StringBuffer(val);
            }

            Object value = parser.parse();

            if (value == null) {
                return null;
            }

            return (T) new StringBuffer(value.toString());
        }

        if (clazz == StringBuilder.class) {
            final JSONLexer lexer = parser.lexer;
            if (lexer.token() == JSONToken.LITERAL_STRING) {
                String val = lexer.stringVal();
                lexer.nextToken(JSONToken.COMMA);

                return (T) new StringBuilder(val);
            }

            Object value = parser.parse();

            if (value == null) {
                return null;
            }

            return (T) new StringBuilder(value.toString());
        }

        return (T) deserialze(parser);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialze(DefaultJSONParser parser) {
        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_STRING) {
            String val = lexer.stringVal();
            lexer.nextToken(JSONToken.COMMA);
            return StringUtils.isEmpty (val) ? null:(T)val;
        }

        if (lexer.token() == JSONToken.LITERAL_INT) {
            String val = lexer.numberString();
            lexer.nextToken(JSONToken.COMMA);
            return StringUtils.isEmpty (val) ? null :(T) val;
        }

        Object value = parser.parse();

        if (value == null) {
            return null;
        }

        return StringUtils.isEmpty (value.toString()) ? null :(T) value.toString();
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }

}
