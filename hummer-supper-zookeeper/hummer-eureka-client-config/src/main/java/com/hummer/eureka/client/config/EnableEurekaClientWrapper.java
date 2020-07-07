package com.hummer.eureka.client.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author edz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ConditionalOnProperty(value = "hummer.enable.eureka.client", matchIfMissing = false)
@Import(value = {EnableEurekaClient.class})
public @interface EnableEurekaClientWrapper {

}
