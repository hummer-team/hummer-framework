package com.hummer.support.exceptions;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/24 15:26
 **/
public class ErrorRequestException extends AppException {
    private static final int ERROR_CODE = 40000;

    public ErrorRequestException(final String msg) {
        super(ERROR_CODE, msg);
    }


    public ErrorRequestException(final String msg,Object responseArgs) {
        super(ERROR_CODE, msg,responseArgs);
    }

    public ErrorRequestException(final int errorCode, final String msg) {
        super(errorCode, msg);
    }
}
