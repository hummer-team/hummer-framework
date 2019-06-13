package com.hummer.thread.plugin;

/**
 * 业务action执行异常策略
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/12 11:31
 **/
public enum WaitCompleteExceptionFlowEnum {
    /**
     * 下个handle继续执行
     */
    CONTINUE(0, "下个handle继续执行"),
    /**
     * 中断且回滚使用 throw exception
     * <p>
     * 中断跳出流程,即链路上剩余handle不执行
     */
    INTERRUPT(2, "中断跳出流程"),
    /**
     * 需要重试（业务暂时用不到）
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

    WaitCompleteExceptionFlowEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 根据状态码获取预约状态枚举
     *
     * @param code
     * @return com.yeshj.classs.learning.reservation.support.task.WaitCompleteExceptionFlowEnum
     * @author malikai
     * @date 2018-12-18 14:07
     * @version 1.0.0
     **/
    public static WaitCompleteExceptionFlowEnum getByCode(int code) {
        for (WaitCompleteExceptionFlowEnum statusEnum : values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum;
            }
        }
        return CONTINUE;
    }

    @Override
    public String toString() {
        return "ExecuteResultEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
