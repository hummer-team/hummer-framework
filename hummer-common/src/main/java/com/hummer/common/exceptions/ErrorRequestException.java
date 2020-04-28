package com.hummer.common.exceptions;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/24 15:26
 **/
public class ErrorRequestException extends AppException {
    private static final int ERROR_CODE = HttpServletResponse.SC_BAD_REQUEST;

    public ErrorRequestException(final String msg) {
        super(ERROR_CODE, msg);
    }


    public ErrorRequestException(final String msg, Object responseArgs) {
        super(ERROR_CODE, msg, responseArgs);
    }

    public ErrorRequestException(final int errorCode, final String msg) {
        super(errorCode, msg);
    }
}
