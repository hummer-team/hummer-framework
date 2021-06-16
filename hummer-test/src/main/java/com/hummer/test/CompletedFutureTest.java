package com.hummer.test;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * {@link https://dzone.com/articles/20-s-of-using-javas-completablefuture}
 * {@link https://www.baeldung.com/java-completablefuture}
 * {@link https://www.callicoder.com/java-8-completablefuture-tutorial/}
 */
public class CompletedFutureTest {

    static final ScheduledExecutorService SCHEDULER = new ScheduledThreadPoolExecutor(0);
    static ExecutorService executor = Executors.newFixedThreadPool(3, new ThreadFactory() {
        int count = 1;

        @Override
        public Thread newThread(Runnable runnable) {
            return new Thread(runnable, "custom-executor-" + count++);
        }
    });

    static Executor delayedExecutor(long delay, TimeUnit unit) {
        return delayedExecutor(delay, unit, ForkJoinPool.commonPool());
    }

    static Executor delayedExecutor(long delay, TimeUnit unit, Executor executor) {
        return r -> SCHEDULER.schedule(() -> executor.execute(r), delay, unit);
    }

    @Test
    public void getNow() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message");
        assertTrue(cf.isDone());
        assertEquals("message", cf.getNow(null));
    }

    @Test
    public void syncToAsyncWrapper() {
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            // TODO: 2020/6/6
        });
        //wait of complete
        cf.join();
        assertTrue(cf.isDone());
    }


    @Test
    public void runAsync() {
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            assertTrue(Thread.currentThread().isDaemon());
            randomSleep();
        });
        assertFalse(cf.isDone());
        sleepEnough(100);
        assertTrue(cf.isDone());
    }

    @Test
    public void thenApplyForAsync() {
        CompletableFuture<String> cf = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println(Thread.currentThread().getName());
                    return "message";
                })
                .thenApply(s -> {
                    System.out.println(Thread.currentThread().getName());
                    return s.toUpperCase();
                });
        assertEquals("MESSAGE", cf.getNow(null));
    }

    @Test
    public void handleException() {
        CompletableFuture<String> cf = CompletableFuture
                .supplyAsync(() -> {
                    if (Boolean.TRUE) {
                        throw new RuntimeException("test exception");
                    }
                    return "message";
                })
                .thenApply(t -> t.toUpperCase())
                .handle((r, t) -> {
                    System.out.println("exception is " + t);
                    return r;
                });
        //wait of completable
        cf.join();
    }

    @Test
    public void waitAnyOf() {
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            randomSleep();
            System.out.println("C1");
        });

        CompletableFuture<Void> cf2 = CompletableFuture.runAsync(() -> {
            randomSleep();
            System.out.println("C2");
        });
        //wait cf or cf2
        CompletableFuture.anyOf(cf, cf2).whenComplete((result, throwable) -> {
            //handle exception
        });
    }

    @Test
    public void timeOutCancel() {
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            sleepEnough(500);
            System.out.println("C1");
            return "C1";
        }).thenCombine(CompletableFuture.supplyAsync(() -> {
            sleepEnough(1500);
            return "SS";
        }), (r1, r2) -> {
            return String.format("%s-%s", r1, r2);
        });
        try {
            //超过800毫秒则取消
            cf.get(800, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            boolean cancel = cf.cancel(true);
            //这里并不总是断言成功
            assertTrue(cancel);
        }
        assertTrue(cf.isCancelled());
    }


    @Test
    public void composeMore() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("A");
        cf.thenCompose(r -> CompletableFuture.completedFuture(String.format("%sB", r)))
                .thenCompose(r -> CompletableFuture.completedFuture(String.format("%sC", r)))
                .thenAccept(r -> System.out.println(r));
        cf.join();
        assertEquals("A", cf.getNow(null));
    }

    @Test
    public void combineMore() {
        //流程：A，B，C 分别由独立对future处理，且都依赖前面都结果
        CompletableFuture<String> cf = CompletableFuture.completedFuture("A")
                .thenCombine(CompletableFuture.completedFuture("B"), (r1, r2) -> r1 + r2)
                .thenCombine(CompletableFuture.completedFuture("C"), (r1, r2) -> r1 + r2)
                .whenComplete((r,throwable)->System.out.println(r));
        //assert result
        assertEquals("ABC", cf.join());
    }

    @Test
    public void thenApply() {
        //run at same thread
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message")
                .thenApply(s -> {
                    assertFalse(Thread.currentThread().isDaemon());
                    System.out.println(Thread.currentThread().getName());
                    return s.toUpperCase();
                });
        System.out.println(Thread.currentThread().getName());
        assertEquals("MESSAGE", cf.getNow(null));
    }

    @Test
    public void thenApplyAsync() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message")
                .thenApplyAsync(s -> {
                    assertTrue(Thread.currentThread().isDaemon());
                    randomSleep();
                    return s.toUpperCase();
                });
        //result is null.
        assertNull(cf.getNow(null));
        //get result
        assertEquals("MESSAGE", cf.join());
    }

    @Test
    public void thenApplyAsyncWithCustomExecutor() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(s -> {
            assertTrue(Thread.currentThread().getName().startsWith("custom-executor-"));
            assertFalse(Thread.currentThread().isDaemon());
            randomSleep();
            return s.toUpperCase();
        }, executor);
        assertNull(cf.getNow(null));
        assertEquals("MESSAGE", cf.join());
    }

    @Test
    public void thenAccept() {
        //thenAccept no return.
        //thenAccept executed thenAccept
        StringBuilder result = new StringBuilder();
        CompletableFuture.completedFuture("thenAccept message")
                .thenAccept(s -> result.append(s));
        assertTrue("Result was empty", result.length() > 0);
    }

    @Test
    public void thenAcceptAsync() {
        //thenAcceptAsync executed async
        StringBuilder result = new StringBuilder();
        CompletableFuture<Void> cf = CompletableFuture.completedFuture("thenAcceptAsync message")
                .thenAcceptAsync(s -> result.append(s));
        cf.join();
        assertTrue("Result was empty", result.length() > 0);
    }

    @Test
    public void completeExceptionally() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message")
                .thenApplyAsync(String::toUpperCase,
                        delayedExecutor(1, TimeUnit.SECONDS));

        CompletableFuture<String> exceptionHandler =
                cf.handle((s, th) -> {
                    return (th != null) ? "message upon cancel" : "";
                });
        cf.completeExceptionally(new RuntimeException("completed exceptionally"));
        assertTrue("Was not completed exceptionally", cf.isCompletedExceptionally());
        try {
            cf.join();
            fail("Should have thrown an exception");
        } catch (CompletionException ex) { // just for testing
            assertEquals("completed exceptionally", ex.getCause().getMessage());
        }
        assertEquals("message upon cancel", exceptionHandler.join());
    }

    @Test
    public void cancel() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message")
                .thenApplyAsync(String::toUpperCase,
                        delayedExecutor(1, TimeUnit.SECONDS));
        CompletableFuture<String> cf2 = cf.exceptionally(throwable -> "canceled message");
        //true cancel if running task
        assertTrue("Was not canceled", cf.cancel(true));
        assertTrue("Was not completed exceptionally", cf.isCompletedExceptionally());
        assertEquals("canceled message", cf2.join());
    }

    @Test
    public void applyToEither() {
        String original = "Message";
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture(original)
                .thenApplyAsync(s -> delayedUpperCase(s));
        CompletableFuture<String> cf2 = cf1.applyToEither(
                CompletableFuture.completedFuture(original).thenApplyAsync(s -> delayedLowerCase(s)),
                s -> s + " from applyToEither");
        assertTrue(cf2.join().endsWith(" from applyToEither"));
    }

    @Test
    public void acceptEither() {
        String original = "Message";
        //StringBuffer is thread safe
        StringBuffer result = new StringBuffer();
        CompletableFuture<Void> cf = CompletableFuture.completedFuture(original)
                .thenApplyAsync(s -> delayedUpperCase(s))
                .acceptEither(CompletableFuture.completedFuture(original).thenApplyAsync(s -> delayedLowerCase(s)),
                        s -> result.append(s).append("acceptEither"));
        cf.join();
        assertTrue("Result was empty", result.toString().endsWith("acceptEither"));
    }

    @Test
    public void runAfterBoth() {
        String original = "Message";
        StringBuilder result = new StringBuilder();
        //runAfterBoth executed sync
        CompletableFuture.completedFuture(original)
                .thenApply(String::toUpperCase)
                .runAfterBoth(
                        CompletableFuture.completedFuture(original)
                                .thenApply(String::toLowerCase),
                        () -> result.append(" done"));

        assertEquals(" done", result.toString());
        assertTrue("Result was empty", result.length() > 0);
    }

    @Test
    public void thenAcceptBoth() {
        String original = "Message";
        StringBuilder result = new StringBuilder();
        //thenAcceptBoth cf1 and cf2 result
        CompletableFuture.completedFuture(original)
                .thenApply(String::toUpperCase)
                .thenAcceptBoth(
                        CompletableFuture.completedFuture(original)
                                .thenApply(String::toLowerCase),
                        (s1, s2) -> result.append(s1 + s2));
        assertEquals("MESSAGEmessage", result.toString());
    }

    @Test
    public void thenCombine() {
        String original = "Message";
        CompletableFuture<String> cf = CompletableFuture
                .completedFuture(original)
                .thenApply(s -> delayedUpperCase(s))
                .thenCombine(CompletableFuture
                                .completedFuture(original)
                                .thenApply(s -> delayedLowerCase(s)),
                        (s1, s2) -> s1 + s2);
        assertEquals("MESSAGEmessage", cf.getNow(null));
    }

    @Test
    public void thenCombineAsync() {
        String original = "Message";
        CompletableFuture<String> cf = CompletableFuture.completedFuture(original)
                .thenApplyAsync(s -> delayedUpperCase(s))
                .thenCombine(CompletableFuture.completedFuture(original).thenApplyAsync(s -> delayedLowerCase(s)),
                        (s1, s2) -> s1 + s2);
        assertEquals("MESSAGEmessage", cf.join());
    }

    @Test
    public void anyOf() {
        StringBuilder result = new StringBuilder();
        List<String> messages = Arrays.asList("a", "b", "c");
        List<CompletableFuture<String>> futures = messages.stream()
                .map(msg -> CompletableFuture.completedFuture(msg)
                        .thenApply(s -> delayedUpperCase(s)))
                .collect(Collectors.toList());
        CompletableFuture.anyOf(futures.toArray(new CompletableFuture[futures.size()]))
                .whenComplete((res, th) -> {
                    if (th == null) {
                        assertTrue(isUpperCase((String) res));
                        result.append(res);
                    }
                });
        assertTrue("Result was empty", result.length() > 0);
    }

    private boolean isUpperCase(String res) {
        return res.equals(res.toUpperCase());
    }

    @Test
    public void thenCompose() {
        //thenCompose This method waits for the first stage
        String original = "Message";
        CompletableFuture<String> cf = CompletableFuture
                .completedFuture(original)
                .thenApply(s -> delayedUpperCase(s))
                .thenCompose(upper -> CompletableFuture
                        .completedFuture(original)
                        .thenApply(s -> delayedLowerCase(s))
                        .thenApply(s -> upper + s));
        assertEquals("MESSAGEmessage", cf.join());
    }

    @Test
    public void allOf() {
        StringBuilder result = new StringBuilder();
        List<String> messages = Arrays.asList("a", "b", "c");
        List<CompletableFuture<String>> futures = messages.stream()
                .map(msg -> CompletableFuture.completedFuture(msg).thenApply(s -> delayedUpperCase(s)))
                .collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).whenComplete((v, th) -> {
            futures.forEach(cf -> assertTrue(isUpperCase(cf.getNow(null))));
            result.append("done");
        });
        assertTrue("Result was empty", result.length() > 0);
    }

    @Test
    public void allOfAsync() {
        StringBuilder result = new StringBuilder();
        List<String> messages = Arrays.asList("a", "b", "c");
        List<CompletableFuture<String>> futures = messages.stream()
                .map(msg -> CompletableFuture.completedFuture(msg).thenApplyAsync(s -> delayedUpperCase(s)))
                .collect(Collectors.toList());
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
                .whenComplete((v, th) -> {
                    futures.forEach(cf -> assertTrue(isUpperCase(cf.getNow(null))));
                    result.append("done");
                });
        allOf.join();
        assertTrue("Result was empty", result.length() > 0);
    }

    String delayedUpperCase(String val) {
        sleepEnough(20);
        return val.toUpperCase();
    }

    String delayedLowerCase(String val) {
        sleepEnough(20);
        return val.toLowerCase();
    }

    @Test
    public void random() {
        System.out.println(Math.random() * 100);
        System.out.println(new Random().nextLong() * 10);
    }

    private void sleepEnough(long sleepMillis) {
        try {
            TimeUnit.MILLISECONDS.sleep(sleepMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void randomSleep() {
        try {
            TimeUnit.MILLISECONDS.sleep((long) (Math.abs(Math.random()) * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void split(){
        String[] a="a".split(",");
        for(String s : a){
            System.out.println(s);
        }
    }
}
