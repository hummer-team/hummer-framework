package com.hummer.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageDigestBean {
    public @Bean
    MessageDigestFactoryBean digestMd5() {
        return new MessageDigestFactoryBean();
    }
}
