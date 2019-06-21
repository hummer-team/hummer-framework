package com.hummer.rest.bean;

import com.hummer.rest.monitor.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/21 17:57
 **/
@Configuration
public class RestConfigurationBean {

    @Bean
    public GlobalExceptionHandler exceptionHandler(){
        return new  GlobalExceptionHandler();
    }
}
