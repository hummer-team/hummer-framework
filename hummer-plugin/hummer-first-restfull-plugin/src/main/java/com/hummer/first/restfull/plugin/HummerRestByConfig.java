package com.hummer.first.restfull.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lee
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HummerRestByConfig {
    /**
     * remote service api name
     */
    String apiName() default "";
    /**
     * remote service response parse program
     */
    Class<? extends CustomParseResp> parse() default CustomParseResp.class;
}
