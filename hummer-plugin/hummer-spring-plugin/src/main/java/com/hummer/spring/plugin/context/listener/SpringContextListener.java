package com.hummer.spring.plugin.context.listener;

import com.hummer.spring.plugin.context.SpringApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * listener spring boot link {#ApplicationPreparedEvent} event,this event express spring boot context load done,but bean
 * no begin loading.
 *
 * @link https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/ content `Application Events and Listeners`
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/13 16:27
 **/
@Component
public class SpringContextListener implements ApplicationListener<ApplicationPreparedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringContextListener.class);

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        if (SpringApplicationContext.getApplicationContext() == null) {
            SpringApplicationContext context = new SpringApplicationContext();
            context.setApplicationContext(event.getApplicationContext());
            LOGGER.info("SpringContext load success,now begin create bean");
        }
    }
}