package com.hummer.core.spi;

import org.springframework.context.ConfigurableApplicationContext;

public interface CustomizeContextInit {
    /**
     * impl custom init
     */
    void init(ConfigurableApplicationContext context);
}
