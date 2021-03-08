package com.hummer.yug.user.plugin.annotation.member;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UserAuthorityAnnotation
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/3/5 10:40
 */
@Documented
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserAuthorityAnnotation {

    String validApi() default "";

    String[] userTokens() default {};

    String[] authorityCodes() default {};

    String remark() default "";

    AuthorityConditionEnum authorityCondition() default AuthorityConditionEnum.ALL_OF;

}
