package com.hummer.common.eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * if property key ‘memory.event.bus.enable’ is true then register all
 * mark {@link com.google.common.eventbus.Subscribe} event handle function
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/7/10 14:33
 **/
@Component
@Conditional(EventBusCondition.class)
public class EventBusRegister implements BeanPostProcessor {
    @Autowired
    private EventBus eventBus;

    /**
     * register all event subscribe .
     *
     * @param bean     bean
     * @param beanName name
     * @return java.lang.Object
     * @author liguo
     * @date 2019/7/10 14:39
     * @since 1.0.0
     **/
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getMethods();
        for (Method method : methods) {
            Subscribe subscribe = method.getAnnotation(Subscribe.class);
            if (subscribe != null) {
                eventBus.register(bean);
            }
        }
        return bean;
    }
}
