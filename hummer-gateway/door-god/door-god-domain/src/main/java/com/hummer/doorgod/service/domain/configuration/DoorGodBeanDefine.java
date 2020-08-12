package com.hummer.doorgod.service.domain.configuration;

import com.hummer.doorgod.service.domain.filter.factory.AddRequestHeader2GatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.FailoverLoadBalancerGatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.ProvideServiceTimeGatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.RequestBlacklistGatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.ResponseBodyGatewayFilterFactory;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DoorGodBeanDefine {
    @Bean
    public ProvideServiceTimeGatewayFilterFactory provideServiceTimeGatewayFilterFactory() {
        return new ProvideServiceTimeGatewayFilterFactory();
    }

    @Bean
    public AddRequestHeader2GatewayFilterFactory addRequestHeaderGatewayFilterFactory() {
        return new AddRequestHeader2GatewayFilterFactory();
    }

    @Bean
    public ResponseBodyGatewayFilterFactory responseBodyGatewayFilterFactory() {
        return new ResponseBodyGatewayFilterFactory();
    }

    @Bean
    public RequestBlacklistGatewayFilterFactory requestBlacklistAssertGatewayFilterFactory() {
        return new RequestBlacklistGatewayFilterFactory();
    }

    @Bean
    public FailoverLoadBalancerGatewayFilterFactory customReactiveLoadBalancerClientGatewayFilterFactory(
            LoadBalancerClientFactory clientFactory
    ) {
        return new FailoverLoadBalancerGatewayFilterFactory(clientFactory);
    }
}
