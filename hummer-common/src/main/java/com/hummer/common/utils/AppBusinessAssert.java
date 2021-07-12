package com.hummer.common.utils;

import com.hummer.common.ErrorCode;
import com.hummer.common.exceptions.AppException;
import com.hummer.common.exceptions.BusinessIdempotentException;
import com.hummer.core.PropertiesContainer;

/**
 * AppBusinessAssert
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/9/14 15:07
 */
public class AppBusinessAssert {

    public static void isTrue(boolean expression, int code, String msg) {
        if (!expression) {
            throw new AppException(code, msg);
        }
    }

    public static void isTrue(boolean expression, ErrorCode errorCode) {

        isTrue(expression, errorCode.getCode(), errorCode.getMsg());
    }

    public static void isTrueOrIdempotentException(boolean expression, int code, String msg) {

        if (!expression) {
            throw new BusinessIdempotentException(code, msg);
        }
    }

    public static void isTrueOrIdempotentException(boolean expression, ErrorCode errorCode) {

        isTrueOrIdempotentException(expression, errorCode.getCode(), errorCode.getMsg());
    }


    public static void environment(String env) {
        String current = PropertiesContainer.valueOfString("spring.profiles.active", "");
        isTrue(current.equals(env), 40000, String.format("current env %s ,need env %s", current, env));
    }

}
