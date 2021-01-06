package com.hummer.yug.user.plugin.annotation.member;

import com.hummer.yug.tools.plugin.enums.UserEnums;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author edz
 */
@Documented
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MemberNeedAuthority {

    UserEnums.UserType userType() default UserEnums.UserType.MEMBER;

    String remark() default "会员用户登录校验";
}
