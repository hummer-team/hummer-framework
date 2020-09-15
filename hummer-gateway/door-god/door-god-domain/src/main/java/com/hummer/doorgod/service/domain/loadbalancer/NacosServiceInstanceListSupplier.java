package com.hummer.doorgod.service.domain.loadbalancer;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class NacosServiceInstanceListSupplier implements ServiceInstanceListSupplier {
    private final String serviceId;
    private final NacosDiscoveryProperties nacosDiscoveryProperties;

    public NacosServiceInstanceListSupplier(String serviceId, NacosDiscoveryProperties nacosDiscoveryProperties) {
        this.serviceId = serviceId;
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public Flux<List<ServiceInstance>> get() {
        List<Instance> instanceList = null;
        try {

            instanceList = nacosDiscoveryProperties.namingServiceInstance().selectInstances(serviceId, true);
        } catch (NacosException e) {
            log.error("no active service instance for {}", serviceId);
        }

        if (CollectionUtils.isEmpty(instanceList)) {
            return Flux.empty();
        }
        List<ServiceInstance> list =
                instanceList.stream()
                        .filter(f -> f.getWeight() > 0.0)
                        .map(i -> new DefaultServiceInstance(i.getInstanceId()
                                , i.getServiceName()
                                , i.getIp()
                                , i.getPort()
                                , false
                                , i.getMetadata()))
                        .collect(Collectors.toList());
        return Flux.just(list);
    }
}
