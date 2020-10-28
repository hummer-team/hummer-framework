package com.hummer.request.idempotent.plugin.annotation;

import com.hummer.common.SysConstant;
import com.hummer.request.idempotent.plugin.constants.Constants;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RequestIdempotentAnnotation
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/28 10:21
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestIdempotentAnnotation {

    String key() default SysConstant.REQUEST_ID;

    int expireSeconds() default Constants.DEFAULT_EXPIRED_TIME_SECONDS;

    /**
     * this is define key name spaces,e.g:kingkong
     */
    String applicationName() default "";

    /**
     * this is define business description,e.g:order
     */
    String businessCode();

    boolean enable() default true;
}
