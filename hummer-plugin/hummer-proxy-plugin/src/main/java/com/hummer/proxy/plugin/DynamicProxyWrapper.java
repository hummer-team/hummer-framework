package com.hummer.proxy.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * DynamicProxyWrapper
 *
 * @author lee
 */
public class DynamicProxyWrapper {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T javaDynamicProxy(Object targetObject, InterceptHandler interceptHandler) {
        return (T) new JavaDynamicProxy(targetObject, interceptHandler).newProxyInstance();
    }

    @SuppressWarnings("unchecked")
    public static <T> T javassistBefore(String targetFullClassName, String targetMethod, String beforeMethod)
            throws Throwable {
        return (T) new JavassistDynamicProxy(targetFullClassName, targetMethod).insertBefore(beforeMethod);
    }

    @SuppressWarnings("unchecked")
    public static <T> T javassistAfter(String targetFullClassName, String targetMethod, String afterMethod)
            throws Throwable {
        return (T) new JavassistDynamicProxy(targetFullClassName, targetMethod).insertAfter(afterMethod);
    }

    @SuppressWarnings("unchecked")
    public static <T> T javassistBeforeAndAfter(String targetFullClassName, String targetMethod
            , String beforeMethod, String afterMethod) throws Throwable {
        return (T) new JavassistDynamicProxy(targetFullClassName, targetMethod).insertBeforeAndAfter(beforeMethod
                , afterMethod);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T cglibIntercept(Class<?> tClass, InterceptHandler interceptHandler, String... methods) {
        return (T) new CglibDynamicProxy(tClass, interceptHandler, Arrays.asList(methods)).newProxyInstance();
    }
}
