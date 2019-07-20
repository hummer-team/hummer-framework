package com.hummer.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * multiple data source switch,need match properties file data source configuration  .
 *
 *
 * <p>
 * 
 * {@code spring.jdbc.hj_class_learning_biz_log.driverClassName} ,correct is `hj_class_learning_biz_log`
 * </p>
 *
 * @author bingy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface TargetDataSource {

    /**
     * target data source key name
     *
     * @return
     */
    String value() default "";
}
