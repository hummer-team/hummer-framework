package com.hummer.first.restfull.plugin;

import com.hummer.common.utils.ResourceUtil;
import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.core.spi.CustomizeContextInit;
import com.hummer.proxy.plugin.Proxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hummer.common.SysConstant.APPLICATION_BOOT_CLASS;

/**
 * @author lee
 */
@Slf4j
public class RegisterService implements CustomizeContextInit {
    /**
     * impl custom init
     *
     * @param context {@link ConfigurableApplicationContext}
     */
    @Override
    public void init(ConfigurableApplicationContext context) {
        Class<?> bootClass = PropertiesContainer.valueOf(APPLICATION_BOOT_CLASS, Class.class);
        HummerSimpleRestBootScan restBootScan = bootClass.getAnnotation(HummerSimpleRestBootScan.class);
        if (restBootScan == null) {
            return;
        }

        Set<Class<?>> classes = ResourceUtil.getClassesByPackageName(restBootScan.scanBasePackages());
        if (classes.isEmpty()) {
            return;
        }

        classes = classes.stream()
                .filter(p -> p.isInterface() && p.getAnnotation(HummerSimpleRest.class) != null)
                .collect(Collectors.toSet());
        log.debug("use hummer simple rest feature class count {}", classes.size());

        for (Class<?> aClass : classes) {
            HummerRestMetadata.put(aClass.getTypeName(), aClass);
            //create proxy
            Proxy proxy = Proxy.getProxy(aClass);
            Object oClass = proxy.newInstance(getProxyHandler());

            BeanDefinitionBuilder beanDefinitionBuilder =
                    BeanDefinitionBuilder.genericBeanDefinition(oClass.getClass());
            SpringApplicationContext.registerDynamicBen(aClass.getTypeName()
                    , beanDefinitionBuilder.getRawBeanDefinition());
        }

        log.debug("register hummer rest service proxy done");
    }

    private InvocationHandler getProxyHandler() {
        return new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        };
    }
}
