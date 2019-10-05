package com.hummer.seata.spring.boot.plugin;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(value = "com.hummer.seata.spring.boot.plugin")
public class DistributedTransactionConfiguration {
}
