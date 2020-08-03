package com.hummer.doorgod.service.domain.configuration;

import com.hummer.doorgod.service.domain.filter.ProvideServiceTimeGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DoorGodBeanDefine {
    @Bean
    public ProvideServiceTimeGatewayFilterFactory provideServiceTimeGatewayFilterFactory() {
        return new ProvideServiceTimeGatewayFilterFactory();
    }
}
