package com.hummer.pipeline.plugin.test;


import com.hummer.pipeline.plugin.ThreadWithCancel;
import org.junit.Test;

public class ThreadWithCancelTest {
    @Test
    public void cancelTask() throws InterruptedException {
        System.out.println("...");
        ThreadWithCancel threadWithCancel = new ThreadWithCancel(() -> {
            System.out.println("业务");
        }, 200, "test");

        threadWithCancel.start();

        Thread.sleep(2000);

        threadWithCancel.cancel();

        Thread.sleep(2000);

        System.out.println(threadWithCancel.getState());

    }
}
