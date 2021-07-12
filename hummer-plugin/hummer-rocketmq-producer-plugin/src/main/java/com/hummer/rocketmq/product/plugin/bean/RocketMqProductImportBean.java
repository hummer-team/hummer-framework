package com.hummer.rocketmq.product.plugin.bean;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author edz
 */
@Configuration
@ComponentScan(value = "com.hummer.rocketmq.product.plugin")
@Conditional(value = RocketMqCondition.class)
public class RocketMqProductImportBean {

}
