package com.hummer.core.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.TimeUnit;

/**
 * application failed listener
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/20 17:00
 **/
public class ApplicationFailedListener implements ApplicationListener<ApplicationFailedEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationFailedListener.class);

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        LOGGER.error("application start failed,reason ",event.getException());
        handle();
    }

    private void handle() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(3);
                    LOGGER.warn("System after 1s exists!");
                    System.exit(0);
                } catch (InterruptedException e) {
                    LOGGER.warn(e.getMessage(), e);
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }
}
