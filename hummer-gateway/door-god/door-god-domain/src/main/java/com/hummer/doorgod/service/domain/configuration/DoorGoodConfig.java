package com.hummer.doorgod.service.domain.configuration;

import lombok.Data;
import org.springframework.cloud.gateway.route.RouteDefinition;

@Data
public class DoorGoodConfig {
    private RouteDefinition routeDefinition;
    private SentinelConfig sentinelConfig;
}
