package com.hummer.common.exceptions;



import java.util.Arrays;

import static com.hummer.common.SysConsts.SYS_ERROR_CODE;

public class SysException extends RuntimeException {

    private static final long serialVersionUID = 3116483353040779859L;

    private transient Object[] args;
    private transient Object returnObj;
    private int errorCode;

    public SysException(int errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }
    
    public SysException(String msg) {
    	super(msg);
    	this.errorCode = SYS_ERROR_CODE;
    }

    public SysException(int errorCode, String msg, Object returnObj) {
        super(msg);
        this.errorCode = errorCode;
        this.returnObj = returnObj;
    }

    public SysException(int errorCode, String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
    }

    public SysException(int errorCode, String msg, Throwable cause, Object returnObj) {
        super(msg, cause);
        this.errorCode = errorCode;
        this.returnObj = returnObj;
    }

    public SysException(int errorCode, String msg, Object[] args) {
        super(msg);
        this.errorCode = errorCode;
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length).clone();
    }

    public SysException(int errorCode, String msg, Object[] args, Object returnObj) {
        super(msg);
        this.errorCode = errorCode;
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
        this.returnObj = returnObj;
    }

    public SysException(int errorCode, String msg, Object[] args, Throwable cause) {
        super(msg, cause);
        this.errorCode = errorCode;
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
    }

    public SysException(int errorCode, String msg, Object[] args, Throwable cause, Object returnObj) {
        super(msg, cause);
        this.errorCode = errorCode;
        this.args = args == null ? new Object[0] : Arrays.copyOf(args, args.length);
        this.returnObj = returnObj;
    }

    public SysException(Throwable cause) {
        super(cause);
    }

    public SysException(Throwable cause, Object returnObj) {
        super(cause);
        this.returnObj = returnObj;
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

    public int getCode() {
        return errorCode;
    }

    public void setCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
