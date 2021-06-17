package com.hummer.first.restfull.plugin.annotation;

import com.hummer.first.restfull.plugin.bean.RegisterDynamicProxyBean;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author lee
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({RegisterDynamicProxyBean.class})
public @interface HummerRestApiClientBootScan {
    /**
     * target package
     */
    String scanBasePackages();

    /**
     * exclude dont scan package
     */
    Class<?>[] exclude() default {};
}
