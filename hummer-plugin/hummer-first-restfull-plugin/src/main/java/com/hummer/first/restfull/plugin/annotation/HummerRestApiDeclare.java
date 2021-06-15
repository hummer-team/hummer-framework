package com.hummer.first.restfull.plugin.annotation;

import com.hummer.first.restfull.plugin.CustomParseRespProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author edz
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HummerRestApiDeclare {
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
    int timeOutMills() default 5000;

    /**
     * retry count
     */
    int retryCount() default 0;

    /**
     * api business describe
     */
    String businessDescribe() default "";

    /**
     * remote service response parse program
     */
    Class<? extends CustomParseRespProvider> parse() default CustomParseRespProvider.class;

    /**
     * is enable api call
     */
    boolean enable() default true;

    /**
     * remote service api name
     */
    String apiName() default "";

    /**
     * is async call remote api
     */
    boolean async() default false;

    /**
     * if value is 0 then disable cache
     */
    int cacheTimeOutMills() default 0;
}
