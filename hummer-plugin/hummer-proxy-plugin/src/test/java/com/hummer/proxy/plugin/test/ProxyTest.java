package com.hummer.proxy.plugin.test;

import com.hummer.proxy.plugin.DynamicProxyWrapper;
import com.hummer.proxy.plugin.InterceptHandler;
import com.hummer.proxy.plugin.Proxy;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ProxyTest {
    @Test
    public void aClass() {
        Proxy proxy = Proxy.getProxy(A.class);

        A instance = (A) proxy.newInstance(getHandler());

        assertNull(instance.getName());
        instance.setName("qianlei", "hello");

        //cglib test
        instance = DynamicProxyWrapper.cglibIntercept(instance.getClass(), new InterceptHandler() {
            @Override
            public Object before(Object proxy, Object[] args) {
                System.out.println("before");
                return null;
            }

            @Override
            public void after(Object before, Object result) {
                System.out.println("after");
            }
        }, false, "setName");
        instance.setName("ss", "dd");
    }

    private InvocationHandler getHandler() {
        return new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if ("getName".equals(method.getName())) {
                    assertEquals(args.length, 0);
                } else if ("setName".equals(method.getName())) {
                    assertEquals(args.length, 2);
                    assertEquals(args[0], "qianlei");
                    assertEquals(args[1], "hello");
                }
                return null;
            }
        };
    }
}
