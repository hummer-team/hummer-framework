package com.hummer.doorgod.service.domain.configuration;

import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.hummer.doorgod.service.domain.filter.factory.AddRequestHeader2GatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.FailoverLoadBalancerGatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.ProvideServiceTimeGatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.RequestBlacklistGatewayFilterFactory;
import com.hummer.doorgod.service.domain.filter.factory.ResponseBodyGatewayFilterFactory;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
    public NacosRule nacosWeightedRule() {
        NacosRule rule = new NacosRule();
        rule.setLoadBalancer(new DynamicServerListLoadBalancer());
        return rule;
    }

    @Bean
    @ConditionalOnBean(value = NacosRule.class)
    public FailoverLoadBalancerGatewayFilterFactory customReactiveLoadBalancerClientGatewayFilterFactory(
            LoadBalancerClientFactory clientFactory
            , NacosRule nacosRule
    ) {
        return new FailoverLoadBalancerGatewayFilterFactory(clientFactory, nacosRule);
    }
}
