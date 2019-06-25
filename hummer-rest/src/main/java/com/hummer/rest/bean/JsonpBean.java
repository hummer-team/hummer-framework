package com.hummer.rest.bean;

import com.alibaba.fastjson.support.spring.JSONPResponseBodyAdvice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/25 15:49
 **/
@Configuration
public class JsonpBean extends WebMvcConfigurerAdapter {

    @Bean
    public JSONPResponseBodyAdvice jsonpResponseBodyAdvice(){
        return new JSONPResponseBodyAdvice();
    }
}
