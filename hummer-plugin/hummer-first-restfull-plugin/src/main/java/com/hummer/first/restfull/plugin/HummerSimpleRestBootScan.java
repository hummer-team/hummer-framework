package com.hummer.first.restfull.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lee
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HummerSimpleRestBootScan {
    /**
     * target package
     */
    String scanBasePackages();

    /**
     * exclude dont scan package
     */
    Class<?>[] exclude() default {};
}
