package com.hummer.thread.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * wrapper thread with cancel task.
 */
public class ThreadWithCancel extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadWithCancel.class);
    private Runnable runnable;
    private long cycleMillisecond;
    private volatile boolean cancel = false;

    public ThreadWithCancel(Runnable runnable, long cycleMillisecond, String threadName) {
        setDaemon(true);
        setName(threadName);
        this.runnable = runnable;
        this.cycleMillisecond = cycleMillisecond;
    }

    @Override
    public void run() {
        while (!cancel) {
            //业务
            this.runnable.run();
            wait2();
        }
        printRunnableStatus();
    }

    private void wait2() {
        if (cycleMillisecond <= 0) {
            return;
        }
        try {
            MILLISECONDS.sleep(cycleMillisecond);
        } catch (InterruptedException e) {
            //处理线程被取消异常
            Thread.currentThread().interrupt();
        }
    }

    private void printRunnableStatus() {
        LOGGER.info("cancelTask {}", cancel);
    }

    private void cancelTask() {
        //set stop flag.
        cancel = true;
    }

    /**
     * cancel this thread.
     */
    public void cancel() {
        //取消任务
        cancelTask();
        //取消该工作线程
        interrupt();
        LOGGER.info("thread cancel success.");
    }
}