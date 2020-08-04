package com.hummer.doorgod.service.domain.configuration;

import com.hummer.doorgod.service.domain.filter.factory.AddRequestHeader2GatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.ProvideServiceTimeGatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.ResponseBodyGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DoorGodBeanDefine {
    @Bean
    public ProvideServiceTimeGatewayFilterFactory provideServiceTimeGatewayFilterFactory() {
        return new ProvideServiceTimeGatewayFilterFactory();
    }

    @Bean
    public AddRequestHeader2GatewayFilterFactory addRequestHeaderGatewayFilterFactory(){
        return new AddRequestHeader2GatewayFilterFactory();
    }

    @Bean
    public ResponseBodyGatewayFilterFactory responseBodyGatewayFilterFactory(){
        return new ResponseBodyGatewayFilterFactory();
    }
}
