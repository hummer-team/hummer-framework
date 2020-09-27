package com.hummer.doorgod.service.domain.configuration;

import lombok.Data;

@Data
public class LoadBalancerConfig {
    public static final LoadBalancerConfig DEFAULT = new LoadBalancerConfig();
    private String routeId;
    private String loadBalancerName;
    private String backUpUri;
}
