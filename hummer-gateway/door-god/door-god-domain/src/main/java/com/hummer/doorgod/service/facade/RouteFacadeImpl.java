package com.hummer.doorgod.service.facade;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.google.common.collect.Maps;
import com.hummer.doorgod.facade.RouteFacade;
import com.hummer.doorgod.service.domain.route.InMemoryRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class RouteFacadeImpl implements RouteFacade {
    @Autowired
    private InMemoryRouteRepository repository;

    @Override
    public Mono<List<RouteDefinition>> getAllRoute() {
        return repository.getAllRouteForBlock();
    }

    @Override
    public Mono<Map<String, Object>> getAllSentinelConfig() {
        return Mono.fromCallable(() -> {
            Map<String, Object> objects = Maps.newConcurrentMap();

            objects.put("degradeRule", DegradeRuleManager.getRules());
            objects.put("gatewayFlowRule", GatewayRuleManager.getRules());
            objects.put("apiRule", GatewayApiDefinitionManager.getApiDefinitions());

            return objects;
        });
    }
}
