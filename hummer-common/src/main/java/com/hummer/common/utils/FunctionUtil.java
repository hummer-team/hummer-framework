package com.hummer.common.utils;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author bingy
 */
public class FunctionUtil {

    private FunctionUtil() {

    }

    /**
     * if condition is true then call consumer
     *
     * @param value     value
     * @param condition condition
     * @param consumer  consumer
     * @param <T>       value
     */
    public static <T> void actionByCondition(final T value
            , final Predicate<T> condition
            , final Consumer<T> consumer) {
        if (condition.test(value)) {
            consumer.accept(value);
        }
    }
}
