package com.hummer.common.exceptions;


public class ConfigurationErrorException extends SysException {

    private static final long serialVersionUID = 2600970878613452467L;

    private static final int ERROR_CODE = 50001;

    public ConfigurationErrorException(String msg) {
        super(ERROR_CODE, msg);
    }

}
