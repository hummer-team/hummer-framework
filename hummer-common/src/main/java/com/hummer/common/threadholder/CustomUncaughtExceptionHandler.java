package com.hummer.common.threadholder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUncaughtExceptionHandler.class);

    private static String getStack(StackTraceElement[] stackTraceElements) {
        if (stackTraceElements == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : stackTraceElements) {
            builder.append(element.toString()).append(" ");
        }
        return builder.toString();
    }

    /**
     * Method invoked when the given thread terminates due to the
     * given uncaught exception.
     * <p>Any exception thrown by this method will be ignored by the
     * Java Virtual Machine.
     *
     * @param t the thread
     * @param e the exception
     */
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOGGER.error(">>>thread uncaughtException process" +
                        " will exit,available processors,thread is {},stack is {},throwable {}"
                , Runtime.getRuntime().availableProcessors()
                , t.getName()
                , getStack(t.getStackTrace())
                , e);
        System.err.println(t);
        Runtime.getRuntime().exit(1);
    }
}
