package com.hummer.spring.plugin.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/13 16:53
 **/
@Configuration
public class ConfigDemoTest {

    @Bean(name = "demo")
    public String val() {
        return "demo";
    }
}
