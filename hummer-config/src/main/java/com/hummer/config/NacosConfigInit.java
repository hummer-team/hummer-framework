package com.hummer.config;

import com.hummer.core.spi.CustomizeContextInit;

public class NacosConfigInit implements CustomizeContextInit {
    /**
     * impl custom init
     */
    @Override
    public void init() {
        new NaCosConfig().refreshConfig(true);
    }
}
