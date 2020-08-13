package com.hummer.doorgod.service.domain.loadbalancer;

import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.reactive.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.reactive.Request;
import org.springframework.cloud.client.loadbalancer.reactive.Response;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import reactor.core.publisher.Mono;

@Slf4j
public class NacosWeightedRule implements ReactorServiceInstanceLoadBalancer {
    private final NacosRule nacosRule;
    private final String serviceId;

    public NacosWeightedRule(NacosRule nacosRule
            , String serviceId) {
        this.nacosRule = nacosRule;
        this.serviceId = serviceId;
    }

    /**
     * Choose the next server based on the load balancing algorithm.
     *
     * @param request - an input request
     * @return - mono of response
     */
    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        //select active service instance by Weights.
        nacosRule.setLoadBalancer(new DynamicServerListLoadBalancer<>());
        Server server = nacosRule.choose(serviceId);
        if (server == null) {
            return Mono.just(new EmptyResponse());
        }
        log.debug("select service instance for {},server info {}"
                , serviceId
                , server.toString());

        DefaultServiceInstance serviceInstance = new DefaultServiceInstance(server.getMetaInfo().getAppName()
                , server.getMetaInfo().getAppName()
                , server.getHost()
                , server.getPort()
                , false);

        return Mono.just(new DefaultResponse(serviceInstance));

    }
}
