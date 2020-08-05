package com.hummer.doorgod.api.controller;

import com.hummer.doorgod.facade.RouteFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping(value = "/v1")
public class RouteController {
    @Autowired
    private RouteFacade routeFacade;

    @GetMapping("/all-routes")
    public Mono<List<RouteDefinition>> queryAllRoutes(){
        return routeFacade.getAllRoute();
    }
}
