package com.hummer.eureka.client.config;

import org.springframework.cloud.loadbalancer.config.BlockingLoadBalancerClientAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {BlockingLoadBalancerClientAutoConfiguration.class})
@ComponentScan(basePackages = "com.hummer.eureka.client.config")
public class ExportEurekaClientConfig {
}
