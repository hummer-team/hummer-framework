package com.hummer.first.restfull.plugin.bean;

import com.hummer.common.utils.ResourceUtil;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.spi.CustomizeContextInit;
import com.hummer.first.restfull.plugin.annotation.HummerFirstRest;
import com.hummer.first.restfull.plugin.annotation.HummerFirstRestBootScan;
import com.hummer.first.restfull.plugin.bean.RegisterDynamicProxyBean;
import com.hummer.proxy.plugin.Proxy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hummer.common.SysConstant.APPLICATION_BOOT_CLASS;

/**
 * @author lee
 * @deprecated use {@link RegisterDynamicProxyBean}
 */
@Slf4j
@Deprecated
public class RegisterDynamicProxy implements CustomizeContextInit {
    /**
     * impl custom init
     *
     * @param context {@link ConfigurableApplicationContext}
     */
    @Override
    public void init(ConfigurableApplicationContext context) {
        boolean enable = PropertiesContainer.valueOf("enable.rest.proxy.custom.register", Boolean.class
                , Boolean.FALSE);
        if (!enable) {
            return;
        }

        Class<?> bootClass = PropertiesContainer.valueOf(APPLICATION_BOOT_CLASS, Class.class);
        HummerFirstRestBootScan restBootScan = bootClass.getAnnotation(HummerFirstRestBootScan.class);
        if (restBootScan == null) {
            return;
        }

        Set<Class<?>> classes = ResourceUtil.getClassesByPackageName(restBootScan.scanBasePackages());
        if (classes.isEmpty()) {
            return;
        }

        classes = classes.stream()
                .filter(p -> p.isInterface() && p.getAnnotation(HummerFirstRest.class) != null)
                .collect(Collectors.toSet());
        log.debug("use hummer simple rest feature class count {}", classes.size());

        for (Class<?> aClass : classes) {
            //create proxy
            Proxy proxy = Proxy.getProxy(aClass.getClassLoader(), aClass);
            Object oClass = proxy.newInstance(getProxyHandler());
            PropertiesContainer.put(aClass.getTypeName(), oClass);
        }

        log.debug("register hummer rest service proxy done");
    }

    private InvocationHandler getProxyHandler() {
        return new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                log.debug("begin invoke.");
                throw new NotImplementedException("not impl");
            }
        };
    }
}
