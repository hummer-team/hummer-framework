package com.hummer.core.starter;

import com.hummer.core.SpringApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/13 17:13
 **/
@Configuration
public class BootStarterBean extends BaseEnvironment {
    private static final Logger LOGGER = LoggerFactory.getLogger(BootStarterBean.class);

    @Bean
    public SpringApplicationContext springApplicationContext(ApplicationContext applicationContext) {
        SpringApplicationContext context = new SpringApplicationContext();
        context.setApplicationContext(applicationContext);
        LOGGER.info("init spring application context done.");
        return context;
    }
}
