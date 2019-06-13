package com.hummer.thread.plugin;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 *
 * @author liguo
 * @date 2017/9/5
 */
public class MdcTaskDecorator implements TaskDecorator {
    /**
     * Decorate the given {@code Runnable}, returning a potentially wrapped
     * {@code Runnable} for actual execution.
     *
     * @param runnable the original {@code Runnable}
     * @return the decorated {@code Runnable}
     */
    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        return () -> {
            try {
                // Right now: @Async thread executedContext !
                // (Restore the Web thread executedContext's MDC data)
                MDC.setContextMap(contextMap);
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
