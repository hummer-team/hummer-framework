package com.hummer.kafka.product.plugin.bean;


import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/8 18:29
 **/
@Configuration
@ComponentScan(value = "com.hummer.kafka.product")
@Conditional(value = KafkaCondition.class)
public class ProductBean {

}
