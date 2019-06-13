package com.hummer.thread.plugin;

/**
 * biz action strategy.
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/12 11:28
 **/
public class BizActionStrategy {
    /**
     * default
     * <pre>
     *     1,5s timeout.
     *     2,interrupt and rollback.
     * </pre>
     **/
    public static final BizActionStrategy DEFAULT_STRATEGY = new BizActionStrategy(5000L,
            WaitCompleteExceptionFlowEnum.INTERRUPT, "default");

    /**
     * action 执行超时毫秒
     **/
    private long timeOutMillisecond;
    /**
     * 等待执行完成异常策略
     **/
    private WaitCompleteExceptionFlowEnum exceptionFlowEnum;
    /**
     * biz action name
     **/
    private String bizActionName;

    public BizActionStrategy(long timeOutMillisecond
            , WaitCompleteExceptionFlowEnum exceptionFlowEnum
            , String bizActionName) {
        this.timeOutMillisecond = timeOutMillisecond;
        this.exceptionFlowEnum = exceptionFlowEnum;
        this.bizActionName = bizActionName;
    }

    public long getTimeOutMillisecond() {
        return timeOutMillisecond;
    }

    public WaitCompleteExceptionFlowEnum getExceptionFlowEnum() {
        return exceptionFlowEnum;
    }

    public String getBizActionName() {
        return bizActionName;
    }
}
