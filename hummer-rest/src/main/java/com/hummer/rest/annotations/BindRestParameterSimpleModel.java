package com.hummer.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * 1,convert query string parameter to pojo model
 * 2,parameter name ignore case
 * </p>
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/6 10:40
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface BindRestParameterSimpleModel {
}
