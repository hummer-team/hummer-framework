package com.hummer.config;

import com.hummer.config.subscription.ConfigSubscriptionManagerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/9/30 18:16
 **/
@Configuration
@ComponentScan(value = "com.hummer.config")
public class ConfigBean {
    @Bean
    public NaCosConfig naCosConfig() {

        return new NaCosConfig(new ConfigSubscriptionManagerImpl());
    }

}
