package com.hummer.soa.plugin.enums;

/**
 * @author liguo.
 * @date 2018/4/20.
 */
public enum ExecuteFlowEnum {
    /**
     * 下个handle继续执行
     */
    CONTINUE(0, "下个handle继续执行"),
    /**
     * @deprecated 中断且回滚使用 throw exception
     *
     * 中断,需要执行回滚列表
     */
    @Deprecated
    INTERRUPT(2, "中断且回滚"),
    /**
     * 需要重试
     */
    CONTINUE_NEED_RETRY(3, "需要重试");

    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ExecuteFlowEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ExecuteResultEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
