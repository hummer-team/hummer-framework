package com.hummer.proxy.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author lee
 */
public class JavaDynamicProxy implements InvocationHandler {
    private final Object targetObject;
    private final InterceptHandler interceptHandler;

    public JavaDynamicProxy(Object targetObject, InterceptHandler interceptHandler) {
        this.targetObject = targetObject;
        this.interceptHandler = interceptHandler;
    }

    public Object newProxyInstance() {
        return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),
                targetObject.getClass().getInterfaces(), this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object beforeResult = interceptHandler.before(proxy, args);
        try {
            Object ret = method.invoke(targetObject, args);
            interceptHandler.after(beforeResult, ret);
            return ret;
        } catch (Throwable e) {
            interceptHandler.exception(beforeResult, e);
            throw e;
        }
    }
}
