package com.hummer.doorgod.service.domain.exception;


import com.hummer.common.exceptions.AppException;

/**
 * message sign authentic exception
 *
 * @author bingy
 * @since 1.0.0
 */
public class BusinessException extends AppException {
    private static final long serialVersionUID = -6610609235989773199L;
    private int errorCode;
    private String message;

    public BusinessException(int errorCode, String message) {
        super(errorCode,message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
