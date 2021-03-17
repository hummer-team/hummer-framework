package com.hummer.common.holder;

import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.Nullable;

import java.util.function.Supplier;

/**
 * thread content holder
 *
 * @author edz
 */
public class HummerContextHolder {

    private static final ThreadLocal<Object> requestAttributesHolder =
            new NamedThreadLocal<>("Request hummer attributes");

    private static final ThreadLocal<Object> inheritableRequestAttributesHolder =
            new NamedInheritableThreadLocal<>("Request hummer context");

    public static void reset() {
        requestAttributesHolder.remove();
        inheritableRequestAttributesHolder.remove();
    }

    public static <T> T get(boolean inheritable) {
        if (inheritable) {
            return (T) inheritableRequestAttributesHolder.get();
        }
        return (T) requestAttributesHolder.get();
    }

    public static <T> T get(boolean inheritable, Supplier<T> supplier) {
        if (inheritable) {
            T result = (T) inheritableRequestAttributesHolder.get();
            if (result == null) {
                set(supplier.get(), inheritable);
            }
            return result;
        }
        T result = (T) requestAttributesHolder.get();
        if (result == null) {
            set(supplier.get(), inheritable);
        }

        return result;
    }

    public static <T> T get() {
        return get(false);
    }

    public static <T> T get(Supplier<T> supplier) {
        return get(false, supplier);
    }

    public static <T> void set(@Nullable T obj) {
        set(obj, false);
    }

    public static <T> void set(@Nullable T obj, boolean inheritable) {
        if (inheritable) {
            inheritableRequestAttributesHolder.set(obj);
            requestAttributesHolder.remove();
        } else {
            requestAttributesHolder.set(obj);
            inheritableRequestAttributesHolder.remove();
        }
    }
}
