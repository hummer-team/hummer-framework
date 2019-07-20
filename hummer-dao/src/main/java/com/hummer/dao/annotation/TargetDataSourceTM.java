package com.hummer.dao.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * multiple data source transactional manager
 *
 * @author bingy
 * @date 2019-07-20
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Transactional
public @interface TargetDataSourceTM {
    /**
     * target data source name
     *
     * @return
     */
    String dbName();

    /**
     * format data key suffix `_TM`
     * @return
     */
    @AliasFor(annotation = Transactional.class, attribute = "transactionManager")
    String transactionManager();

    @AliasFor(annotation = Transactional.class, attribute = "propagation")
    Propagation propagation() default Propagation.REQUIRED;

    @AliasFor(annotation = Transactional.class, attribute = "isolation")
    Isolation isolation() default Isolation.DEFAULT;

    @AliasFor(annotation = Transactional.class, attribute = "timeout")
    int timeout() default -1;

    @AliasFor(annotation = Transactional.class, attribute = "readOnly")
    boolean readOnly() default false;

    @AliasFor(annotation = Transactional.class, attribute = "rollbackFor")
    Class<? extends Throwable>[] rollbackFor() default {};

    @AliasFor(annotation = Transactional.class, attribute = "rollbackForClassName")
    String[] rollbackForClassName() default {};

    @AliasFor(annotation = Transactional.class, attribute = "noRollbackFor")
    Class<? extends Throwable>[] noRollbackFor() default {};

    @AliasFor(annotation = Transactional.class, attribute = "noRollbackForClassName")
    String[] noRollbackForClassName() default {};
}
