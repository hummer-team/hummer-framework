package com.hummer.rest.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

public class FirstCharToUpperCaseSerializer implements ObjectSerializer {
    public static final FirstCharToUpperCaseSerializer instance = new FirstCharToUpperCaseSerializer();

    /**
     * fastjson invokes this call-back method during serialization when it encounters a field of the
     * specified type.
     *
     * @param serializer
     * @param object     src the object that needs to be converted to Json.
     * @param fieldName  parent object field name
     * @param fieldType  parent object field type
     * @param features   parent object field serializer features
     * @throws IOException
     */
    @Override
    public void write(JSONSerializer serializer, Object object
            , Object fieldName, Type fieldType
            , int features) throws IOException {
        if (object != null) {
            String originFieldName = fieldName.toString();
            String newFieldName = originFieldName.substring(0, 1).toUpperCase() + originFieldName.substring(1);
            serializer.writeWithFieldName(object, newFieldName);
        }
    }
}
