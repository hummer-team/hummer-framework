package com.hummer.doorgod.facade;

import org.springframework.cloud.gateway.route.RouteDefinition;
import reactor.core.publisher.Mono;

import java.util.List;

public interface RouteFacade {
    Mono<List<RouteDefinition>> getAllRoute();
}
