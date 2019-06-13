package com.hummer.thread.plugin;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * TaskWrapper
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/11 16:03
 **/
public class TaskWrapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskWrapper.class);
    private static final String DEFAULT_TASK_GROUP = "defaultTaskGroupV2";
    private final String bizGroupName;
    private static final String OEN_THREAD_GROUP = "defaultTaskOenThreadGroupV2";
    private Collection<BizAction> bizActions;
    private Collection<TaskInfoInner> tasks;

    /**
     * create new task wrapper
     *
     * @param bizThreadGroupName thread pool name.
     * @return link {#TaskWrapper}
     * @author liguo
     * @date 2018/12/12 14:45
     * @version 1.0.0
     **/
    public static TaskWrapper create(final String bizThreadGroupName) {
        return new TaskWrapper(bizThreadGroupName);
    }

    /**
     * 创建taskwrapper，使用默认的线程池策略
     *
     * @param []
     * @return com.yeshj.classs.learning.reservation.support.task.TaskWrapper
     * @author liguo
     * @date 2018/12/12 14:45
     * @version 1.0.0
     **/
    public static TaskWrapper createDefault() {
        return new TaskWrapper(DEFAULT_TASK_GROUP);
    }

    private TaskWrapper(final String bizThreadGroupName) {
        this.bizGroupName = bizThreadGroupName;
        this.bizActions = Lists.newArrayListWithCapacity(7);
        this.tasks = Lists.newArrayListWithCapacity(7);
    }

    /**
     * 提交任务不执行
     * <pre>
     *     调用 run 执行已提交的任务
     * </pre>
     *
     * @param bizAction 业务action列表
     * @return com.yeshj.classs.learning.reservation.support.task.TaskWrapper
     * @author liguo
     * @date 2018/12/12 14:42
     * @version 1.0.0
     **/
    public TaskWrapper submit(final BizAction bizAction) {
        this.bizActions.add(bizAction);
        return this;
    }

    /**
     * 执行已提交的业务action。
     * <pre>
     *     如果业务action列表为空，则忽略
     * </pre>
     *
     * @param
     * @return com.yeshj.classs.learning.reservation.support.task.TaskWrapper
     * @author liguo
     * @date 2018/12/12 14:43
     * @version 1.0.0
     **/
    public TaskWrapper run() {
        return executeAsync(bizActions);
    }

    /**
     * 异步执行任务
     *
     * @param bizActions 业务action列表
     * @return com.yeshj.classs.learning.reservation.support.task.TaskWrapper
     * @author liguo
     * @date 2018/12/12 14:40
     * @version 1.0.0
     **/
    public TaskWrapper executeAsync(final Collection<BizAction> bizActions) {
        return executeAsync(null, bizActions);
    }


    /**
     * 异步执行action列表
     * <pre>
     *     1.input 作为所有action入参
     *     2.action的apply输出结果作为accept输入参数
     *     3.如果action的apply,accept 则调用自定义异常处理策略
     * </pre>
     *
     * @param input      依赖参数
     * @param bizActions 业务action列表
     * @return com.yeshj.classs.learning.reservation.support.task.TaskWrapper
     * @author liguo
     * @date 2018/12/12 14:40
     * @version 1.0.0
     **/
    @SuppressWarnings("unchecked")
    public <T> TaskWrapper executeAsync(T input, Collection<BizAction> bizActions) {
        bizActions.forEach(bizAction -> {
            CompletableFuture localFuture = CompletableFuture
                    .supplyAsync(() -> bizAction.apply(input)
                            , /**SpringApplicationContext.getBean(bizGroupName, ExecutorService.class)**/null)
                    .handle((r, e) -> handleException(bizAction, e, r))
                    .thenAcceptAsync(bizAction::accept
                            , /**SpringApplicationContext.getBean(bizGroupName, ExecutorService.class)**/null)
                    .handle((r, e) -> handleException(bizAction, e, r));
            //加入业务task执行列表
            tasks.add(new TaskInfoInner(localFuture
                    , bizAction.actionStrategy()
                    , bizAction.handleBizActionWaitCompleteException()));
        });

        return this;
    }

    /**
     * action,action的apply结果输出作为accept输入参数。且等待action执行完成
     * <pre>
     *     1,等待action apply完成后在调用accept
     *     2,如果action apply异常则回调exception handle且抛出异常中断执行accept
     *     3,如果action accept异常则根据自定义策略是否抛出异常 <see>BizActionStrategy</see>
     *     4,如果action get 结果异常则根据自定义策略是否抛出异常 <see>BizActionStrategy</see>
     * </pre>
     *
     * @param bizAction task
     * @return com.yeshj.classs.learning.reservation.support.task.TaskWrapper
     * @author liguo
     * @date 2018/12/11 18:20
     * @version 1.0.0
     **/
    @SuppressWarnings("unchecked")
    public TaskWrapper execute(BizAction bizAction) {
        return execute(null, bizAction);
    }

    /**
     * 执行action,action的apply结果输出作为accept输入参数。且等待action执行完成
     * <pre>
     *     1,等待action apply完成后在调用accept
     *     2,如果action apply异常则回调exception handle且抛出异常中断执行accept
     *     3,如果action accept异常则根据自定义策略是否抛出异常 <see>BizActionStrategy</see>
     *     4,如果action get 结果异常则根据自定义策略是否抛出异常 <see>BizActionStrategy</see>
     * </pre>
     *
     * @param input     输入参数
     * @param bizAction task
     * @return com.yeshj.classs.learning.reservation.support.task.TaskWrapper
     * @author liguo
     * @date 2018/12/11 18:20
     * @version 1.0.0
     **/
    @SuppressWarnings("unchecked")
    public <T> TaskWrapper execute(T input, BizAction bizAction) {
        try {
            CompletableFuture.supplyAsync(() -> bizAction.apply(input)
                    , /**SpringApplicationContext.getBean(OEN_THREAD_GROUP, ExecutorService.class)**/null)
                    .handle((r, e) -> handleExceptionAndThrowable(bizAction, e, r))
                    .thenAcceptAsync(r -> bizAction.accept(r)
                            , /**SpringApplicationContext.getBean(OEN_THREAD_GROUP, ExecutorService.class)**/null)
                    .handle((r, e) -> handleException(bizAction, e, r))
                    .get(bizAction.actionStrategy().getTimeOutMillisecond(), TimeUnit.MILLISECONDS);

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            if (bizAction.handleBizActionWaitCompleteException() != null) {
                bizAction.handleBizActionWaitCompleteException().accept(e);
            }

            if (bizAction.actionStrategy().getExceptionFlowEnum()
                    != WaitCompleteExceptionFlowEnum.CONTINUE) {
                throw new RuntimeException(e);
            }
        }
        return this;
    }

    /**
     * 等待任务完成
     * <pre>
     *     1.action等待超时时间根据<see>BizActionStrategy</see>设置值判断
     *     2.action等待异常则根据<see>BizActionStrategy</see>设置值判断是否中断流程
     * </pre>
     *
     * @param []
     * @return void
     * @author liguo
     * @date 2018/12/12 14:37
     * @version 1.0.0
     **/
    public void tryWaitAllTaskComplete() {

        if (CollectionUtils.isEmpty(tasks)) {
            return;
        }

        StringBuilder log = new StringBuilder();
        tasks.forEach(taskInfoInner -> {
            try {
                long startTime = System.currentTimeMillis();
                taskInfoInner.getFuture().get(taskInfoInner
                                .getActionStrategy()
                                .getTimeOutMillisecond()
                        , TimeUnit.MILLISECONDS);
                log.append(String.format("action `%s` execute done,wait cost time %d ms,"
                        , taskInfoInner.getActionStrategy().getBizActionName()
                        , System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                Consumer<Exception> handle = taskInfoInner.getExceptionConsumer();
                if (handle != null) {
                    handle.accept(e);
                }
                if (taskInfoInner.getActionStrategy().getExceptionFlowEnum()
                        != WaitCompleteExceptionFlowEnum.CONTINUE) {
                    taskInfoInner.getFuture().cancel(true);
                    throw new RuntimeException(e);
                }
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        LOGGER.info(log.toString());
    }

    @SuppressWarnings("unchecked")
    private Object handleException(BizAction bizFuture, Throwable e, Object resp) {
        if (e == null) {
            return resp;
        }
        if (bizFuture.handleBizActionWaitCompleteException() != null) {
            bizFuture.handleBizActionWaitCompleteException().accept(e);
            if (bizFuture.actionStrategy().getExceptionFlowEnum() != WaitCompleteExceptionFlowEnum.CONTINUE) {
                throw new RuntimeException(e);
            }
        }
        return resp;
    }


    @SuppressWarnings("unchecked")
    private Object handleExceptionAndThrowable(BizAction bizFuture, Throwable e, Object resp) {
        if (e == null) {
            return resp;
        }
        if (bizFuture.handleBizActionWaitCompleteException() != null) {
            bizFuture.handleBizActionWaitCompleteException().accept(e);
        }
        throw new RuntimeException(e);
    }
}
