package com.hummer.swagger.plugin;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SwaggerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        String env = context.getEnvironment().getProperty("spring.profiles.active");
        return env == null || !env.startsWith("prod");
    }
}

