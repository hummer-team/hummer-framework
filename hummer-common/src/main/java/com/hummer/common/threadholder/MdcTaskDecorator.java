package com.hummer.common.threadholder;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

/**
 * use simple
 * <code>
 * <pre>
 *          ThreadFactory tf =
 *                 new ThreadFactoryBuilder()
 *                         .setNameFormat(String.format("%s-Th"
 *                                 , BaseProperties.getProperty("task-queue-biz-name"
 *                                         , String.class
 *                                         , "defaultTaskOenThreadGroupV2")
 *                                         + "-%d"))
 *                         .setDaemon(true)
 *                         .build();
 *
 *         final MdcTaskDecorator taskDecorator = new MdcTaskDecorator();
 *
 *         return new ThreadPoolExecutor(1
 *                 , 1
 *                 , 0
 *                 , TimeUnit.MILLISECONDS
 *                 , new LinkedBlockingQueue<>(size)
 *                 , tf
 *                 , new ThreadPoolExecutor.CallerRunsPolicy()) {
 *             @Override
 *             public void execute(Runnable command) {
 *                 super.execute(taskDecorator.decorate(command));
 *             }
 *         };
 *     </pre>
 * </code>
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
