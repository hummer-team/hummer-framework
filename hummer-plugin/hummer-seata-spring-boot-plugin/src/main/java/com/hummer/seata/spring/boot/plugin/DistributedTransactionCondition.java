package com.hummer.seata.spring.boot.plugin;

import com.hummer.core.PropertiesContainer;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class DistributedTransactionCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return PropertiesContainer.valueOf("distributed.transaction.enable", Boolean.class, Boolean.FALSE);
    }
}
