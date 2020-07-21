package com.hummer.cache.plugin;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * @author edz
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {FIELD, PARAMETER})
public @interface HummerSimpleObjectCacheKey {
    /**
     * this is cache key name
     */
    String keyName() default "";

    /**
     * for large keys, md5 encryption is used, which has improved transmission performance
     */
    boolean maxKeyMd5() default false;
}
