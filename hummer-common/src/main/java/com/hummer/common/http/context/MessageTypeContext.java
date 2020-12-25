package com.hummer.common.http.context;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;

/**
 * @author edz
 */
public class MessageTypeContext<R> {
    private final TypeReference<R> typeRef;
    private final Class<?> classType;

    public MessageTypeContext(TypeReference<R> typeRef
            , Class<?> classType) {
        this.typeRef = typeRef;
        this.classType = classType;
    }

    public TypeReference<R> getTypeRef() {
        return typeRef;
    }

    public Type getType() {
        return typeRef.getType();
    }

    public Class<?> getClassType() {
        return classType;
    }
}
