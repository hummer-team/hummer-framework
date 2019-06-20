package com.hummer.support.exceptions;

import java.util.Arrays;

public class AppException extends RuntimeException {

    private static final long serialVersionUID = 8688953989589840707L;

    private transient Object[] args;
    private transient Object returnObj;
    private int errorCode;

    public AppException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

    public AppException(int errorCode, String msg, Object returnObj) {
        super(msg);
        this.returnObj = returnObj;
        this.errorCode = errorCode;

    }

    public AppException(int errorCode, String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public AppException(String msg, Throwable cause, Object returnObj) {
        super(msg, cause);
        this.returnObj = returnObj;
    }

    public AppException(int errorCode, String msg, Throwable cause, Object returnObj) {
        super(msg, cause);
        this.returnObj = returnObj;
        this.errorCode = errorCode;
    }

    public AppException(int errorCode, String msg, Object[] args) {
        super(msg);
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
        this.errorCode = errorCode;
    }


    public AppException(int errorCode, String msg, Object[] args, Throwable cause) {
        super(msg, cause);
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
        this.errorCode = errorCode;
    }

    public Object[] getArgs() {
        return this.args;
    }

    public void setArgs(Object[] args) {
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
    }

    public Object getReturnObj() {
        return this.returnObj;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    public int getCode() {
        return this.errorCode;
    }

    public void setCode(int errorCode) {
        this.errorCode = errorCode;
    }


}
