package com.hummer.core.listener;

import com.hummer.core.PropertiesContainer;
import com.hummer.core.SpringApplicationContext;
import com.hummer.core.spi.CustomizeContextInit;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

import java.util.ServiceLoader;

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

    @SneakyThrows
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        if (SpringApplicationContext.getApplicationContext() == null) {
            //init application context
            SpringApplicationContext context = new SpringApplicationContext();
            context.setApplicationContext(event.getApplicationContext());

            LOGGER.info("SpringContext load success,property configuration load success,now begin create bean");
        }
        //load property configuration
        PropertiesContainer.loadPropertyData(event.getApplicationContext().getEnvironment());
        executeCustomizeContextInit();
    }

    private void executeCustomizeContextInit() {
        //
        ServiceLoader<CustomizeContextInit> loaders =
                ServiceLoader.load(CustomizeContextInit.class);

        while (loaders.iterator().hasNext()) {
           loaders.iterator().next().init();
        }
    }
}