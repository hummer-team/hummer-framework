package com.hummer.dao.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * this is mark annotation, mybatis scanner DaoAnnotation only for
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/25 18:04
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
@Inherited
public @interface DaoAnnotation {
}
