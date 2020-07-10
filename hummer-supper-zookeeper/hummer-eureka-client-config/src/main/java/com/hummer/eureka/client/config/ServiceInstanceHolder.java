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
        if (Boolean.FALSE
                .equals(PropertiesContainer.valueOf("eureka.client.enabled", Boolean.class, Boolean.FALSE))) {
            return PropertiesContainer.valueOfStringWithAssertNotNull(String.format("%s.host", refApplicationId));
        }

        ServiceInstance instance = null;
        try {
            instance = loadBalancerClient
                    .choose(refApplicationId);

        } catch (Throwable e) {
            log.error("get {} service instance failed,"
                    , refApplicationId, e);
        }
        //LoadBalancerBuilder.newBuilder().withClientConfig(IClientConfig.Builder.newBuilder().build());

        if (instance == null) {
            log.warn("application id {} not exists,use application properties", refApplicationId);
            return PropertiesContainer.valueOfStringWithAssertNotNull(String.format("%s.host", refApplicationId));
        }

        String serviceHost = instance.getUri().toString();
        log.debug("app id {} uri is {},LoadBalancerRule {}"
                , refApplicationId
                , serviceHost
                , PropertiesContainer.valueOfString(String.format("%s.ribbon.NFLoadBalancerRuleClassName"
                        , refApplicationId), ""));
        return serviceHost;
    }
}
