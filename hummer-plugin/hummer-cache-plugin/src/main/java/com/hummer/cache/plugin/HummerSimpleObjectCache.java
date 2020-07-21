package com.hummer.cache.plugin;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * hummer cache wrapper
 *
 * @author edz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
@Inherited
public @interface HummerSimpleObjectCache {
    /**
     * this is define key name spaces,e.g:kingkong
     */
    String applicationName() default "";

    /**
     * this is define business description,e.g:order
     */
    String businessCode() default "";

    /**
     * time out seconds
     */
    int timeoutSeconds() default 30;

    /**
     * if true use cache else disable cache
     */
    boolean enable() default true;
}
