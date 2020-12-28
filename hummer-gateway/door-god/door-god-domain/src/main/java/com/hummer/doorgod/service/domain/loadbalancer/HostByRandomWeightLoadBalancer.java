package com.hummer.doorgod.service.domain.loadbalancer;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.ExtendBalancer;
import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hummer.core.SpringApplicationContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component("hostByRandomWeightLb")
public class HostByRandomWeightLoadBalancer
        extends BaseReactorServiceInstanceLoadbalancer implements ReactorServiceInstanceLoadBalancer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NacosRule.class);

    @Override
    protected Mono<Instance> choose(Request request
            , String serviceId
            , List<Instance> instances) {
        return Mono.fromCallable(() -> {
            List<Instance> instancesToChoose = instances;
            NacosDiscoveryProperties discoveryProperties = SpringApplicationContext.getBean(NacosDiscoveryProperties.class);
            if (StringUtils.isNotBlank(discoveryProperties.getClusterName())) {
                List<Instance> sameClusterInstances = instances.stream()
                        .filter(instance -> Objects.equals(discoveryProperties.getClusterName(),
                                instance.getClusterName()))
                        .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(sameClusterInstances)) {
                    instancesToChoose = sameClusterInstances;
                } else {
                    LOGGER.warn(
                            "A cross-cluster call occurs，name = {}, clusterName = {}, instance = {}",
                            serviceId, discoveryProperties.getClusterName(), instances);
                }
            }

            return ExtendBalancer.getHostByRandomWeight2(instancesToChoose);
        });
    }

    /**
     * Choose the next server based on the load balancing algorithm.
     *
     * @param request - an input request
     * @return - mono of response
     */
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        return null;
    }
}
