package com.hummer.pipeline.plugin;

import com.hummer.core.PropertiesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author edz
 */
public class TaskThreadPoolConditional implements Condition {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskThreadPoolConditional.class);

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        boolean enable = PropertiesContainer.valueOf("hummer.hummer.task.thread.enable"
                , Boolean.class, Boolean.FALSE);
        LOGGER.info("hummer.hummer.task.thread.enable value is {}", enable);
        return enable;
    }
}
