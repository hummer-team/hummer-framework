package com.hummer.soa.plugin.enums;

/**
 * @author liguo.
 * @date 2018/4/27.
 */
public enum RollbackLinkStrategyEnum {
    /**
     * 默认队列
     */
    DEFAULT_QUEUE(0, "默认队列"),
    /**
     * 根据rollback序号执行,(暂未实现)
     */
    SORT_BY_ROLLBACK_ORDER(1, "根据rollback序号执行"),
    /**
     * 先进后出顺序
     **/
    STACK_ORDER(2, "先进后出顺序");

    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    RollbackLinkStrategyEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "RollbackLinkStrategyEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
