package com.hummer.doorgod.facade;

import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import org.springframework.cloud.gateway.route.RouteDefinition;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface RouteFacade {
    Mono<List<RouteDefinition>> getAllRoute();
    Mono<Map<String,Object>> getAllSentinelConfig();

}
