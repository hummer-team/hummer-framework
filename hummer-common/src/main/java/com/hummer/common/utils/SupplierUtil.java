package com.hummer.common.utils;

import java.util.function.Function;
import java.util.function.Supplier;

public class SupplierUtil {
    private SupplierUtil() {

    }

    /**
     * if fu1 result  to meet the conditions then return result ,else return fu2 result
     *
     * @param fu1
     * @param fu2
     * @param conditionFu1
     * @param <T>
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
