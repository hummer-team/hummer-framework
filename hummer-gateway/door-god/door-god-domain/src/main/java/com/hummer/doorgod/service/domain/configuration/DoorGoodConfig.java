package com.hummer.doorgod.service.domain.configuration;

import lombok.Data;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

@Data
public class DoorGoodConfig {
    private List<RouteDefinition> routeDefinition;
    private List<LoadBalancerConfig> loadBalancerConfig;
    private SentinelConfig sentinelConfig;
}
