package com.hummer.doorgod.service.domain.route;


import com.hummer.core.SpringApplicationContext;
import com.hummer.doorgod.service.domain.configuration.LoadBalancerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
@Slf4j
public class InMemoryRouteRepository {
    private final ConcurrentHashMap<String, LoadBalancerConfig> loadBalancerMap = new ConcurrentHashMap<>();
    @Autowired
    private RouteDefinitionRepository routeDefinitionWriter;

    public void add(RouteDefinition definition) {
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        SpringApplicationContext.publishEvent(new RefreshRoutesEvent(this));
    }

    public void update(RouteDefinition definition) {
        routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        SpringApplicationContext.publishEvent(new RefreshRoutesEvent(this));
    }

    public void delete(String id) {
        routeDefinitionWriter.delete(Mono.just(id)).block();
        loadBalancerMap.remove(id);
    }

    public Mono<List<RouteDefinition>> getAllRouteForBlock() {
        return routeDefinitionWriter.getRouteDefinitions().collectList();
    }

    public void update(LoadBalancerConfig config) {
        loadBalancerMap.put(config.getRouteId(), config);
    }

    public LoadBalancerConfig getLoadBalancer(String routeId, Supplier<LoadBalancerConfig> defaultVal) {
        LoadBalancerConfig config = loadBalancerMap.get(routeId);
        return config != null ? config : defaultVal.get();
    }
}
