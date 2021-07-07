package com.hummer.common.eventbus;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.hummer.core.PropertiesContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.Executors;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/7/10 14:44
 **/
@Configuration
public class EventBusBean {
    @Bean
    @Conditional(EventBusCondition.class)
    public EventBus eventBus() {
        return new EventBus();
    }

    @Bean
    @Conditional(EventBusAsyncCondition.class)
    @Lazy
    public AsyncEventBus asyncEventBus() {
        int threadCount = PropertiesContainer.valueOfInteger("hummer.memory.event.bus.async.thread", 1);
        return new AsyncEventBus(Executors.newFixedThreadPool(threadCount));
    }
}
