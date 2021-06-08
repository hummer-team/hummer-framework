package com.hummer.core;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.lang.Nullable;

import java.util.Locale;
import java.util.Map;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/13 15:55
 **/
public class SpringApplicationContext implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringApplicationContext.class);
    private static ApplicationContext applicationContext;
    private static boolean isRegisterShutdownHook = false;
    private static final Object obj = new Object();

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBeanIfNullThrow(Class<T> tClass) {
        T tReturn = applicationContext.getBean(tClass);
        if (tReturn == null) {
            throw new IllegalArgumentException("this class not exists SpringApplicationContext");
        }

        return tReturn;
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

    public static <T extends ApplicationEvent> void publishEvent(T event) {
        applicationContext.publishEvent(event);
    }

    /**
     * get message value,support multiple language
     *
     * @param name    this is key name
     * @param objects this is Placeholder
     * @param locale  local
     * @return
     */
    public static String getMessage(String name, Locale locale, @Nullable Object[] objects) {
        return applicationContext.getMessage(name, objects, locale);
    }

    /**
     * get bean if failed then null
     *
     * @param beanName bean name
     * @param clazz    class
     * @return T
     * @author liguo
     * @date 2019/8/12 15:38
     * @since 1.0.0
     **/
    public static <T> T getBeanWithNull(String beanName, Class<T> clazz) {
        try {
            return applicationContext.getBean(beanName, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * get class type,convert to map
     *
     * @param clazz target class type
     * @return {@link java.util.Map<java.lang.String,T>}
     * @author liguo
     * @date 2019/7/11 10:55
     * @since 1.0.0
     **/
    public static <T> Map<String, T> getBeansAsMap(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link InitializingBean#afterPropertiesSet()}
     * or org.springframework.context.ApplicationListener custom init-method. Invoked after {@link ResourceLoaderAware#setResourceLoader},
     * {@link ApplicationEventPublisherAware#setApplicationEventPublisher} and
     * {@link MessageSourceAware}, if applicable.
     *
     * @param applicationContext the ApplicationContext object to be used by this object
     * @throws ApplicationContextException in case of context initialization errors
     * @throws BeansException              if thrown by application context methods
     * @see BeanInitializationException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringApplicationContext.applicationContext != null) {
            LOGGER.warn("this application context already init");
        }
        SpringApplicationContext.applicationContext = applicationContext;

        if (SpringApplicationContext.applicationContext instanceof GenericApplicationContext) {
            if (Boolean.FALSE.equals(isRegisterShutdownHook)) {
                synchronized (obj) {
                    if (Boolean.FALSE.equals(isRegisterShutdownHook)) {
                        isRegisterShutdownHook = true;
                        ((GenericApplicationContext) SpringApplicationContext.applicationContext)
                                .registerShutdownHook();
                    }
                }
            }
        }
        LOGGER.info("customer spring context set application context success" +
                        ".[Parent:{}\n->evn:{}\n->application name:{}]"
                , applicationContext.getParent()
                , applicationContext.getEnvironment()
                , applicationContext.getApplicationName());
    }

    /**
     * dynamic register bean to container
     *
     * @param beanName               beanName
     * @param abstractBeanDefinition abstractBeanDefinition
     * @return void
     * @author liguo
     * @date 2019/6/25 18:29
     * @version 1.0.0
     **/
    public static void registerDynamicBen(String beanName, AbstractBeanDefinition abstractBeanDefinition) {
        Preconditions.checkNotNull(applicationContext, "Spring ApplicationContext is null!");

        ConfigurableApplicationContext configurableApplicationContext =
                (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory =
                (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        //allow Override
        defaultListableBeanFactory.setAllowBeanDefinitionOverriding(true);
        defaultListableBeanFactory.registerBeanDefinition(beanName, abstractBeanDefinition);
        LOGGER.info("dynamic register bean {} done", beanName);
    }

    public static void removeBean(String beanName) {
        if (Strings.isNullOrEmpty(beanName)) {
            return;
        }

        Preconditions.checkNotNull(applicationContext, "Spring ApplicationContext is null!");
        ConfigurableApplicationContext applicationContexts = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContexts.getBeanFactory();
        beanFactory.removeBeanDefinition(beanName);

        LOGGER.info("remove register bean {} done", beanName);
    }

    public static void removeBeans(String... beanIds) {
        if (beanIds == null || beanIds.length == 0) {
            return;
        }
        Preconditions.checkNotNull(applicationContext, "Spring ApplicationContext is null!");
        ConfigurableApplicationContext applicationContexts = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContexts.getBeanFactory();
        for (String beanId : beanIds) {
            if (beanId != null && !beanId.isEmpty() && beanFactory.isBeanNameInUse(beanId)) {
                beanFactory.removeBeanDefinition(beanId);
            }
        }
    }
}
