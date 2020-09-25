package com.hummer.doorgod.service.domain.loadbalancer;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceInstance;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hummer.doorgod.service.domain.configuration.LoadBalancerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.reactive.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@Slf4j
public abstract class BaseReactorServiceInstanceLoadbalancer {
    //private ServiceInstanceListSupplier supplier
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    public BaseReactorServiceInstanceLoadbalancer() {

    }

    protected abstract Mono<Instance> choose(Request request
            , String serviceId
            , List<Instance> instances);

    public Mono<Response<ServiceInstance>> tryChoose(Request request
            , String serviceId
            , LoadBalancerConfig loadBalancerConfig) {

        long start = System.currentTimeMillis();
        List<Instance> instances = null;
        try {
            NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
            instances = namingService.selectInstances(serviceId, true);
        } catch (NacosException e) {
            log.error("get {} service instance failed", serviceId, e);
        }

        log.debug("this service {} get service instance cost {} millis,for nacos service"
                , serviceId
                , System.currentTimeMillis() - start);

        //if get service instance failed,then return backup host if exists
        if (CollectionUtils.isEmpty(instances) && Strings.isEmpty(loadBalancerConfig.getBackUpUri())) {
            return Mono.just(new EmptyResponse());
        }

        if (CollectionUtils.isEmpty(instances) && !Strings.isEmpty(loadBalancerConfig.getBackUpUri())) {
            return builderBackupServiceInstance(loadBalancerConfig);
        }

        return choose(request, serviceId, instances)
                .flatMap(instance -> {
                    NacosServiceInstance serviceInstance = new NacosServiceInstance();
                    serviceInstance.setMetadata(instance.getMetadata());
                    serviceInstance.setServiceId(instance.getServiceName());
                    serviceInstance.setHost(instance.getIp());
                    serviceInstance.setPort(instance.getPort());
                    serviceInstance.setSecure(false);
                    return Mono.just(new DefaultResponse(serviceInstance));
                });
    }

    private Mono<Response<ServiceInstance>> builderBackupServiceInstance(LoadBalancerConfig config) {
        BackUpServiceInstance backUpServiceInstance = new BackUpServiceInstance();
        URI uri = URI.create(config.getBackUpUri());
        backUpServiceInstance.setHost(uri.getHost());
        backUpServiceInstance.setPort(uri.getPort());
        backUpServiceInstance.setSecure(uri.getScheme().startsWith("http://"));
        return Mono.just(new DefaultResponse(backUpServiceInstance));
    }
}
