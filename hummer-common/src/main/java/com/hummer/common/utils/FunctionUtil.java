package com.hummer.common.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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



    /**
     * if fu1 result  to meet the conditions then return result ,else return fu2 result
     *
     * @param fu1          function 1
     * @param fu2          function 2
     * @param conditionFu1 condition
     * @param <T>          return type
     * @return
     */
    public static <T> T with(final Supplier<T> fu1
            , final Function<T, Boolean> conditionFu1
            , final Supplier<T> fu2
    ) {
        T r = fu1.get();
        if (conditionFu1.apply(r)) {
            return r;
        }
        return fu2.get();
    }
}
