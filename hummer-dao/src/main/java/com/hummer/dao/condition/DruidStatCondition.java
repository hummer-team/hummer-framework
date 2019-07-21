package com.hummer.dao.condition;

import com.hummer.spring.plugin.context.PropertiesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;

/**
 * @author bingy
 */
public class DruidStatCondition implements Condition {
    private static final Logger LOGGER = LoggerFactory.getLogger(DruidStatCondition.class);

    /**
     * Determine if the condition matches,if `dao.load.enable` value equals false then ignore data source
     *
     * @param context  the condition context
     * @param metadata metadata of the {@link AnnotationMetadata class}
     *                 or {@link MethodMetadata method} being checked
     * @return {@code true} if the condition matches and the component can be registered,
     * or {@code false} to veto the annotated component's registration
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean isLoad = PropertiesContainer.valueOf("druid.stat.enable", Boolean.class, Boolean.TRUE);
        LOGGER.info("=========druid stat enable {},if `druid.stat.enable` " +
                "value is false will disabled druid stat feature===========", isLoad);
        return isLoad;
    }
}
