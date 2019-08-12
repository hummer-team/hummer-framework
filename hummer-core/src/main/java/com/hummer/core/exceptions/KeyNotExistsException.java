package com.hummer.core.exceptions;

/**
 * @author bingy
 */
public class KeyNotExistsException extends RuntimeException {
    private static final long serialVersionUID = 2443534989818754241L;

    private int errorCode;

    public KeyNotExistsException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public int getCode() {
        return errorCode;
    }

    public void setCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
