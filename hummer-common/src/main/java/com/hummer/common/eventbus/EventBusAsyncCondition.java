package com.hummer.common.eventbus;

import com.hummer.core.PropertiesContainer;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/7/10 14:35
 **/
public class EventBusAsyncCondition implements Condition {
    /**
     * Determine if the condition matches.
     *
     * @param context  the condition context
     * @param metadata metadata of the {@link AnnotationMetadata class}
     *                 or {@link MethodMetadata method} being checked
     * @return {@code true} if the condition matches and the component can be registered,
     * or {@code false} to veto the annotated component's registration
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return PropertiesContainer.valueOf("hummer.memory.event.bus.async.enable", Boolean.class, Boolean.FALSE);
    }
}
