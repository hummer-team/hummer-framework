package com.hummer.common.http;

import org.apache.http.nio.reactor.IOReactorExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author liguo.
 * @date 2018/11/5.
 */
public class IOExceptionHandler implements IOReactorExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(IOExceptionHandler.class);

    /**
     * This method is expected to examine the I/O exception passed as
     * a parameter and decide whether it is safe to continue execution of
     * the I/O reactor.
     *
     * @param ex potentially recoverable I/O exception
     * @return {@code true} if it is safe to ignore the exception
     * and continue execution of the I/O reactor; {@code false} if the
     * I/O reactor must throw {@link IOReactorException} and terminate
     */
    @Override
    public boolean handle(IOException ex) {
        LOGGER.error("@@=>http async util `IOReactorExceptionHandler`", ex);
        return false;
    }

    /**
     * This method is expected to examine the runtime exception passed as
     * a parameter and decide whether it is safe to continue execution of
     * the I/O reactor.
     *
     * @param ex potentially recoverable runtime exception
     * @return {@code true} if it is safe to ignore the exception
     * and continue execution of the I/O reactor; {@code false} if the
     * I/O reactor must throw {@link RuntimeException} and terminate
     */
    @Override
    public boolean handle(RuntimeException ex) {
        LOGGER.error("@@=>http async util `RuntimeException`", ex);
        return false;
    }
}
