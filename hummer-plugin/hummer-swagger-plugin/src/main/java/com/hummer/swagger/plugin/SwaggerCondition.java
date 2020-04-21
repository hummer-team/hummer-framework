package com.hummer.swagger.plugin;

import com.hummer.core.PropertiesContainer;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class SwaggerCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {

        String env = PropertiesContainer.valueOfString("spring.profiles.active");
        return env == null || !env.startsWith("prod");
    }
}

