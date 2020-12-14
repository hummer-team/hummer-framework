package com.hummer.request.idempotent.plugin.annotation;

import com.hummer.common.SysConstant;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * BusinessIdempotentAnnotation
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/14 13:26
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessIdempotentAnnotation {

    int code() default SysConstant.BUSINESS_IDEMPOTENT_ERROR_CODE;
}
