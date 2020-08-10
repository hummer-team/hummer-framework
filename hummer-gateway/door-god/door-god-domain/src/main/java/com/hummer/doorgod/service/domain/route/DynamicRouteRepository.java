package com.hummer.doorgod.service.domain.route;


import com.hummer.core.SpringApplicationContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class DynamicRouteRepository {
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
        routeDefinitionWriter.delete(Mono.just(id));
    }

    public Mono<List<RouteDefinition>> getAllRouteForBlock() {
        return routeDefinitionWriter.getRouteDefinitions().collectList();
    }
}
