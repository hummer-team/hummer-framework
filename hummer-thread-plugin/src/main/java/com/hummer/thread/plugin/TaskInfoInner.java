package com.hummer.thread.plugin;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/11 19:06
 **/
public class TaskInfoInner {
    /**
     * 业务action执行 future
     **/
    private CompletableFuture future;
    /**
     * 业务action执行策略
     **/
    private BizActionStrategy actionStrategy;
    /**
     * 处理业务异常
     **/
    private Consumer<Exception> exceptionConsumer;

    public TaskInfoInner(CompletableFuture future, BizActionStrategy actionStrategy) {
        this.future = future;
        this.actionStrategy = actionStrategy;
    }

    public TaskInfoInner(CompletableFuture future
            , BizActionStrategy actionStrategy
            , Consumer<Exception> exceptionConsumer) {
        this.future = future;
        this.actionStrategy = actionStrategy;
        this.exceptionConsumer = exceptionConsumer;
    }

    public CompletableFuture getFuture() {
        return future;
    }

    public BizActionStrategy getActionStrategy() {
        return actionStrategy;
    }

    public Consumer<Exception> getExceptionConsumer() {
        return exceptionConsumer;
    }
}
