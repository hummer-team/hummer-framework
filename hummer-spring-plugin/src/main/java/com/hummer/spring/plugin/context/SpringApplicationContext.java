package com.hummer.spring.plugin.context;

import com.google.common.base.Preconditions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/13 15:55
 **/
public class SpringApplicationContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

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
        SpringApplicationContext.applicationContext = applicationContext;
    }

    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        return applicationContext.getBean(beanName, clazz);
    }

    public static <T> Map<String, T> getBeans(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void register(String beanName, AbstractBeanDefinition abstractBeanDefinition) {
        Preconditions.checkNotNull(applicationContext, "Spring ApplicationContext is null!");

        ConfigurableApplicationContext configurableApplicationContext =
                (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory defaultListableBeanFactory =
                (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        defaultListableBeanFactory.registerBeanDefinition(beanName, abstractBeanDefinition);
    }

    public static void removeBean(String beanId) {
        if (beanId == null || beanId.isEmpty()) {
            return;
        }
        Preconditions.checkNotNull(applicationContext, "Spring ApplicationContext is null!");
        ConfigurableApplicationContext applicationContexts = (ConfigurableApplicationContext) applicationContext;
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContexts.getBeanFactory();
        beanFactory.removeBeanDefinition(beanId);
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
