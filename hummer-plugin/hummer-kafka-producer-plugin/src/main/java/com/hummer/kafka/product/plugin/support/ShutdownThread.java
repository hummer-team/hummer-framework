package com.hummer.kafka.product.plugin.support;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/8 17:45
 **/
public class ShutdownThread {
    private ShutdownThread() {

    }

    public static void regiter(Thread thread) {
        Runtime.getRuntime().addShutdownHook(thread);
    }
}
