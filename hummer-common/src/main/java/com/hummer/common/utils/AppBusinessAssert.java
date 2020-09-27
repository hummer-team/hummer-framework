package com.hummer.common.utils;

import com.hummer.common.exceptions.AppException;

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
}
