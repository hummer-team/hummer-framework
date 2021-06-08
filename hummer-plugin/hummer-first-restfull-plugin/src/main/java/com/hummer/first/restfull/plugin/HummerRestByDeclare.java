package com.hummer.first.restfull.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author edz
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HummerRestByDeclare {
    /**
     * api path
     */
    String apiPath();

    /**
     * service host name
     */
    String host();

    /**
     * http method,e.g:get，post
     */
    String httpMethod() default "GET";

    /**
     * timeout
     */
    int timeOutMills() default 0;

    /**
     * retry count
     */
    int retryCount() default 0;

    /**
     * remote service response parse program
     */
    Class<? extends CustomParseResp> parse() default CustomParseResp.class;
}
