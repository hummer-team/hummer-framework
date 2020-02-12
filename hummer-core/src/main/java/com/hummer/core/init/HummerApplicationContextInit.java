package com.hummer.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/9/30 15:54
 **/
public class HummerApplicationContextInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HummerApplicationContextInit.class);

    /**
     * Initialize the given application context.
     *
     * @param applicationContext the application to configure
     */
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        final long start = System.currentTimeMillis();
        //PropertiesContainer.loadPropertyData(applicationContext.getEnvironment());
        LOGGER.info("......hummer init properties container done,cost {} ms....."
                , System.currentTimeMillis() - start);
    }
}
