package com.hummer.doorgod.service.domain.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component("roundLb")
@Slf4j
public class RoundLoadBalancer
        extends BaseReactorServiceInstanceLoadbalancer implements ReactorServiceInstanceLoadBalancer {
    private final AtomicInteger position;
    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    public RoundLoadBalancer() {
        this.position = new AtomicInteger();
    }

    public RoundLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider) {
        this.position = new AtomicInteger();
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    @SuppressWarnings("rawtypes")
    @Override
    // see original
    // https://github.com/Netflix/ocelli/blob/master/ocelli-core/
    // src/main/java/netflix/ocelli/loadbalancer/RoundRobinLoadBalancer.java
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get().next().map(this::getInstanceResponse);
    }

    private Response<ServiceInstance> getInstanceResponse(
            List<ServiceInstance> instances) {
        return null;
    }

    @Override
    protected Mono<Instance> choose(Request request, String serviceId, List<Instance> instances) {
        if (instances.isEmpty()) {
            log.warn("No servers available for service: " + serviceId);
            return Mono.empty();
        }
        // TODO: enforce order?
        return Mono.fromCallable(() -> {
            int pos = Math.abs(this.position.incrementAndGet());
            return instances.get(pos % instances.size());
        });
    }
}
