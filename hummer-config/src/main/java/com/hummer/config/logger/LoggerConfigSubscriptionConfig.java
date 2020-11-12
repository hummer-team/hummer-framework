package com.hummer.config.logger;

import com.hummer.config.NaCosConfig;
import com.hummer.config.bo.ConfigListenerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerConfigSubscriptionConfig {
    @Autowired
    private NaCosConfig naCosConfig;
    @Autowired
    @Qualifier("loggerConfigListener")
    private LoggerConfigListener loggerConfigListener;

    @Bean
    public void registerLoggerLevelListener() {
        // 添加config change监听
        naCosConfig.addListener(ConfigListenerKey
                .builder()
                .dataId("application-logger.properties")
                .groupId("DEFAULT_GROUP")
                .build(), loggerConfigListener);
    }
}
