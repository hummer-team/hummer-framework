package com.hummer.common.eventbus;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

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
}
