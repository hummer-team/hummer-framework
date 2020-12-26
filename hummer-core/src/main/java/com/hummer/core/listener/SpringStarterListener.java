package com.hummer.core.listener;

import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.core.spi.CustomizeContextInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ServiceLoader;
import java.util.TimeZone;

/**
 * listener spring boot link {#ApplicationPreparedEvent} event,this event express spring boot context load done,but bean
 * no begin loading.
 *
 * @link https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/ content `Application Events and Listeners`
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/13 16:27
 **/
public class SpringStarterListener implements ApplicationListener<ApplicationPreparedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringStarterListener.class);
    private static volatile boolean isLoad = false;

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        if (SpringApplicationContext.getApplicationContext() == null) {
            //init application context
            SpringApplicationContext context = new SpringApplicationContext();
            context.setApplicationContext(event.getApplicationContext());
        }
        LOGGER.info("SpringContext load success,property configuration load success,now begin create bean");
        //load property configuration
        PropertiesContainer.loadPropertyData(event.getApplicationContext().getEnvironment());
        if (!isLoad) {
            executeCustomizeContextInit(event.getApplicationContext());
            isLoad = true;
        }
    }

    private void executeCustomizeContextInit(ConfigurableApplicationContext context) {
        ServiceLoader<CustomizeContextInit> loaders =
                ServiceLoader.load(CustomizeContextInit.class);
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+08"));
        for (CustomizeContextInit init : loaders) {
            init.init(context);
        }
    }
}