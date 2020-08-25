package com.hummer.config;

import com.hummer.core.spi.CustomizeContextInit;
import org.springframework.context.ConfigurableApplicationContext;

public class NacosConfigInit implements CustomizeContextInit {
    /**
     * impl custom init
     */
    @Override
    public void init(ConfigurableApplicationContext context) {
        new NaCosConfig().refreshConfig(true);
    }
}
