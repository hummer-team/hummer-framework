package com.hummer.config;

import com.hummer.config.bo.ConfigListenerKey;
import com.hummer.config.logger.LoggerConfigListener;
import com.hummer.config.subscription.ConfigLoadSubscriptionManagerImpl;
import com.hummer.config.subscription.ConfigSubscriptionManagerImpl;
import com.hummer.core.spi.CustomizeContextInit;
import org.springframework.context.ConfigurableApplicationContext;

public class NacosConfigInit implements CustomizeContextInit {
    /**
     * impl custom init
     */
    @Override
    public void init(ConfigurableApplicationContext context) {
        NaCosConfig config =
                new NaCosConfig(new ConfigSubscriptionManagerImpl(), new ConfigLoadSubscriptionManagerImpl());
        config.addLoadListener(ConfigListenerKey
                .builder()
                .dataId("application-logger.properties")
                .groupId("DEFAULT_GROUP")
                .build(), new LoggerConfigListener());

        config.refreshConfig(true);

    }
}
