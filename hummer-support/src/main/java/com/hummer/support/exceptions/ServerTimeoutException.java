package com.hummer.support.exceptions;

public class ServerTimeoutException extends SysException {

    private static final long serialVersionUID = -8022610820483200483L;
    private static final int ERROR_CODE = 50400;

    public ServerTimeoutException(String msg) {
        super(ERROR_CODE, msg);
    }

    @Override
    public int getCode() {
        return ERROR_CODE;
    }

}
