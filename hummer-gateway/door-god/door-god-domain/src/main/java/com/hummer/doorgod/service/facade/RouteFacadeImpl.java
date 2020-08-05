package com.hummer.doorgod.service.facade;

import com.hummer.doorgod.facade.RouteFacade;
import com.hummer.doorgod.service.domain.route.DynamicRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RouteFacadeImpl implements RouteFacade {
    @Autowired
    private DynamicRouteRepository repository;

    @Override
    public Mono<List<RouteDefinition>> getAllRoute() {
        return repository.getAllRouteForBlock();
    }
}
