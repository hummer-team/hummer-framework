package com.hummer.common.exceptions;

/**
 * BusinessIdempotentException
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/12/14 13:44
 */
public class BusinessIdempotentException extends AppException {


    public BusinessIdempotentException(int errorCode, String msg) {
        super(errorCode, msg);
    }
}
