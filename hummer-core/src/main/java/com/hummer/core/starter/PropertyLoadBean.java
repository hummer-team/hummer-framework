package com.hummer.core.starter;

import com.hummer.core.PropertiesContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * @author edz
 */
@Configuration
public class PropertyLoadBean {
    @Bean
    @DependsOn(value = "com.hummer.core.starter.BootStarterBean")
    public boolean checkIsLoadProperties() {
        return PropertiesContainer.isLoadPropertyData();
    }
}
