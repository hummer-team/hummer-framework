package com.hummer.redis.plugin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/10/28 11:03
 **/
@Configuration
@ComponentScan(value = "com.hummer.redis.plugin")
public class BeanConfiguration {

    @Bean
    public RedisOp redisOp() {
        return new RedisOp();
    }
}
