package com.hummer.eureka.client.config;

import org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "com.hummer.eureka.client.config")
@Import(value = {BlockingLoadBalancerClientAutoConfiguration.class})
public class ExportEurekaClientConfig {
}
