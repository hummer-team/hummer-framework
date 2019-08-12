package com.hummer.core.starter;

import com.hummer.core.PropertiesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * implement load property configuration
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/7/8 11:20
 **/
public class BaseEnvironment implements EnvironmentAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseEnvironment.class);

    /**
     * Set the {@code Environment} that this component runs in.
     *
     * @param event
     */
    @Override
    public void setEnvironment(Environment event) {
        PropertiesContainer.loadPropertyData(event);
        LOGGER.info("evn is {}", event.getActiveProfiles());
    }
}
