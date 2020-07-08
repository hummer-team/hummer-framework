package com.hummer.eureka.client.config;

import com.hummer.core.PropertiesContainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ServiceInstanceHolder {
    @Autowired
    private LoadBalancerClient loadBalancerClient;

    public String getServiceInstance(String refApplicationId) {
        ServiceInstance instance = loadBalancerClient
                .choose(refApplicationId);

        //LoadBalancerBuilder.newBuilder().withClientConfig(IClientConfig.Builder.newBuilder().build());

        if (instance == null) {
            throw new NullPointerException(String.format("this service app name %s not exists"
                    , refApplicationId));
        }

        //DiscoveryClient
        String serviceHost = instance.getUri().toString();
        log.debug("app id {} uri is {},LoadBalancerRule {}"
                , refApplicationId
                , serviceHost
                , PropertiesContainer.valueOfString(String.format("%s.ribbon.NFLoadBalancerRuleClassName"
                        , refApplicationId), ""));
        return serviceHost;
    }
}
