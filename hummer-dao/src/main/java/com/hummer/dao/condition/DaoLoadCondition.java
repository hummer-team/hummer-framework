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
 * this class Impl dao loading switch,if configuration true then load dao else ignore dao loading.
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/25 17:20
 **/
public class DaoLoadCondition implements Condition {
    private static final Logger LOGGER = LoggerFactory.getLogger(DaoLoadCondition.class);

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
        boolean isLoad = PropertiesContainer.valueOf("dao.load.enable", Boolean.class, Boolean.TRUE);
        LOGGER.info("=========loading dao switch {},if `dao.load.enable` " +
                "value is false will ignore dao feature===========", isLoad);
        return isLoad;
    }
}
