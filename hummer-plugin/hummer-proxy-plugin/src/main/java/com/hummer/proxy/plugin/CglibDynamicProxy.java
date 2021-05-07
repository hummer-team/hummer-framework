package com.hummer.proxy.plugin;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author lee
 */
public class CglibDynamicProxy {
    private final Class<?> aClass;
    private final InterceptHandler interceptHandler;
    private final List<String> targetMethods;

    public CglibDynamicProxy(Class<?> aClass, InterceptHandler interceptHandler, List<String> targetMethods) {
        this.aClass = aClass;
        this.interceptHandler = interceptHandler;
        this.targetMethods = targetMethods;
    }

    @SuppressWarnings("unchecked")
    public Object newProxyInstance() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(aClass);
        enhancer.setCallbacks(new Callback[]{new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                Object o = interceptHandler.before(obj, args);
                try {
                    Object result = proxy.invokeSuper(obj, args);
                    interceptHandler.after(o, result);
                    return result;
                } catch (Throwable e) {
                    interceptHandler.exception(o, e);
                    throw e;
                }
            }
        }, NoOp.INSTANCE});
        enhancer.setCallbackFilter(new CallbackFilter() {
            @Override
            public int accept(Method method) {
                return targetMethods.isEmpty() || targetMethods.contains(method.getName()) ? 0 : 1;
            }
        });
        return enhancer.create();
    }
}
