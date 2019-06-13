package com.hummer.thread.plugin;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 业务操作
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/11 16:44
 **/
public interface BizAction<T, R> extends Function<T, R>, Consumer<R> {
    /**
     * 业务执行action策略
     *
     * @param
     * @return long
     * @author liguo
     * @date 2018/12/11 19:05
     * @version 1.0.0
     **/
    default BizActionStrategy actionStrategy() {
        return BizActionStrategy.DEFAULT_STRATEGY;
    }

    /**
     * 处理业务异常
     *
     * @param
     * @return java.util.function.Consumer<java.lang.Exception>
     * @author liguo
     * @date 2018/12/12 14:33
     * @version 1.0.0
     **/
    default Consumer<Exception> handleBizActionWaitCompleteException() {
        return null;
    }
}
