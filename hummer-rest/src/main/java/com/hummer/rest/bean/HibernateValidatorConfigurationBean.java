package com.hummer.rest.bean;

import org.hibernate.validator.HibernateValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/28 16:37
 **/
@Configuration
public class HibernateValidatorConfigurationBean implements WebMvcConfigurer {
    @Bean
    @Primary
    public MethodValidationPostProcessor methodValidationPostProcessor2() {
        MethodValidationPostProcessor postProcessor=new MethodValidationPostProcessor();
        postProcessor.setValidator(validator());
        return postProcessor;
    }

    @Bean
    public static Validator validator() {
        return Validation
                .byProvider(HibernateValidator.class)
                .configure()
                //if verify fail fast
                .failFast(true)
                .buildValidatorFactory()
                .getValidator();
    }
}
