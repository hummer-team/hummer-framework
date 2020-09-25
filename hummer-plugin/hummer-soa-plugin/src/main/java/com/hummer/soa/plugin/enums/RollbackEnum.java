package com.hummer.soa.plugin.enums;

/**
 * @author liguo.
 * @date 2018/5/15.
 */
public enum RollbackEnum {
    /**
     * 回滚当前业务执行
     */
    ROLLBACK_CURRENT_BIZ(1,"回滚当前业务执行"),
    /**
     * 回滚所有调用链的业务
     */
    ROLLBACK_ALL_BIZ(0,"回滚所有调用链的业务");

    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    RollbackEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "RollbackEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
