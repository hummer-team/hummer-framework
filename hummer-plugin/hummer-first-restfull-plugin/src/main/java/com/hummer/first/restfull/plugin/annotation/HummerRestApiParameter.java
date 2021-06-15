package com.hummer.first.restfull.plugin.annotation;

import com.hummer.first.restfull.plugin.ApiParameterTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author edz
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface HummerRestApiParameter {
    ApiParameterTypeEnum apiParam() default ApiParameterTypeEnum.ONLY_QUERY_STRING;
}
