package com.hummer.spring.plugin.context;

import com.hummer.spring.plugin.context.config.PropertiesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/13 17:13
 **/
@Configuration
public class CoreContext implements EnvironmentAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(CoreContext.class);

    @Override
    public void setEnvironment(Environment event) {
        PropertiesContainer.loadData(event);
        LOGGER.info("evn is {}",event.getActiveProfiles());
    }

    @Bean
    // @Conditional(SpringContextCondition.class)
    public SpringApplicationContext springApplicationContext(ApplicationContext applicationContext) {
        SpringApplicationContext context = new SpringApplicationContext();
        context.setApplicationContext(applicationContext);
        LOGGER.info("init spring application context done.");
        return context;
    }

}
