package com.hummer.kafka.product.plugin.bean;

import com.hummer.spring.plugin.context.PropertiesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;


/**
 * kafka condition
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/8 18:40
 **/
public class KafkaCondition implements Condition {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaCondition.class);

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
        Boolean enable = PropertiesContainer.valueOf("hummer.message.bus.kafka.enable"
                , Boolean.class, Boolean.TRUE);
        LOGGER.info("message bus kafka enable {}", enable);
        return enable;
    }
}
