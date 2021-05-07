package com.hummer.redis.plugin.test.proxy;

import com.hummer.proxy.plugin.DynamicProxyWrapper;
import com.hummer.proxy.plugin.InterceptHandler;
import org.junit.Test;

public class UserManagerTest {
    @Test
    public void showName() {
        UserManagerDynamicProxy proxy = new UserManagerDynamicProxy(new UserManagerImpl());
        UserManager userManager = (UserManager) proxy.newProxyInstance();
        userManager.showName("ok");
    }

    @Test
    public void showName2() {
        UserManager userManager = DynamicProxyWrapper.javaDynamicProxy(new UserManagerImpl()
                , new InterceptHandler<Long>() {
                    @Override
                    public Long before(Object proxy, Object[] args) {
                        System.out.println("before");
                        return System.currentTimeMillis();
                    }

                    @Override
                    public void after(Long before, Object result) {
                        System.out.println("after " + (System.currentTimeMillis() - before));
                    }
                });
        userManager.showName("ok4");
    }

    @Test
    public void showName3() throws Throwable {
        UserManager userManager = DynamicProxyWrapper.javassistBefore(
                "com.hummer.redis.plugin.test.proxy.UserManagerImpl"
                , "showName"
                , "System.out.println(\"before\");");
        userManager.showName("haha");
    }

    @Test
    public void showName4() throws Throwable {
        UserManager userManager = DynamicProxyWrapper.javassistBeforeAndAfter(
                "com.hummer.redis.plugin.test.proxy.UserManagerImpl"
                , "showName"
                , "System.out.println(\"before\");"
                , "System.out.println(\"after\");");
        userManager.showName("haha>>>");
    }

    @Test
    public void showName5() {
        UserManager userManager = DynamicProxyWrapper.cglibIntercept(UserManagerImpl.class
                , new InterceptHandler<Long>() {
                    @Override
                    public Long before(Object proxy, Object[] args) {
                        System.out.println("before");
                        return System.currentTimeMillis();
                    }

                    @Override
                    public void after(Long before, Object result) {
                        System.out.println("after " + (System.currentTimeMillis() - before));
                    }
                });
        userManager.showName("cglib");
    }

    @Test
    public void showName6(){
        UserManager2 userManager = DynamicProxyWrapper.cglibIntercept(UserManager2.class
                , new InterceptHandler<Long>() {
                    @Override
                    public Long before(Object proxy, Object[] args) {
                        System.out.println("before");
                        return System.currentTimeMillis();
                    }

                    @Override
                    public void after(Long before, Object result) {
                        System.out.println("after " + (System.currentTimeMillis() - before));
                    }
                },"showName");
        userManager.showName("cglib");
        userManager.showName2("cccc");
    }
}
