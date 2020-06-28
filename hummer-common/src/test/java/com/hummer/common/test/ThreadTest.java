package com.hummer.common.test;

import org.junit.Test;
import sun.applet.AppletClassLoader;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThreadTest {
    @Test
    public void test() {
        Lock lock = new ReentrantLock();
        Condition empty = lock.newCondition();
        Condition full = lock.newCondition();

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
    }


    @Test
    public void dataStruct() {
        System.out.println(Integer.parseInt("0001111", 2) & 15);
        System.out.println(Integer.parseInt("1001111", 2) & 15);
    }

    @Test
    public void cyclicBarrier() throws IOException {
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
        Executor executor = Executors.newFixedThreadPool(3);

        CyclicBarrier barrier = new CyclicBarrier(3, () -> {
            Integer[] result = queue.toArray(new Integer[0]);
            System.out.println(Arrays
                    .stream(result)
                    .mapToInt(i -> i).sum());
        });

        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                System.out.println(queue.offer(1));
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }
        System.in.read();
    }

    @Test
    public void exchange() throws IOException, InterruptedException {
        Exchanger<String> exchanger = new Exchanger<>();
        Executor executor = Executors.newFixedThreadPool(3);

        executor.execute(() -> {
            try {
                System.out.println(exchanger.exchange("A"));
            } catch (InterruptedException e) {

            }
        });
        executor.execute(() -> {
            try {
                System.out.println(exchanger.exchange("B"));
            } catch (InterruptedException e) {

            }
        });


        System.in.read();
    }

    @Test
    public void classLoader() {
        URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
        for (URL url : urls) {
            System.out.println(url);
        }

        System.out.println("---------------");
        URL[] urlss = ((URLClassLoader) ClassLoader.getSystemClassLoader().getParent()).getURLs();
        for (URL url : urlss) {
            System.out.println(url);
        }

        System.out.println("----------------");

        URL[] urlsss = ((URLClassLoader) AppletClassLoader.getSystemClassLoader()).getURLs();
        for (URL url : urlsss) {
            System.out.println(url);
        }
    }

}
