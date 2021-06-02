package com.hummer.common.holder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author edz
 */
public class HummerContextMapHolder {
    private final ThreadLocal<ConcurrentHashMap<String, String>> REQUEST_CONTEXT_MAP
            = new ThreadLocal<ConcurrentHashMap<String, String>>() {
        /**
         * Returns the current thread's "initial value" for this
         * thread-local variable.  This method will be invoked the first
         * time a thread accesses the variable with the {@link #get}
         * method, unless the thread previously invoked the {@link #set}
         * method, in which case the {@code initialValue} method will not
         * be invoked for the thread.  Normally, this method is invoked at
         * most once per thread, but it may be invoked again in case of
         * subsequent invocations of {@link #remove} followed by {@link #get}.
         *
         * <p>This implementation simply returns {@code null}; if the
         * programmer desires thread-local variables to have an initial
         * value other than {@code null}, {@code ThreadLocal} must be
         * subclassed, and this method overridden.  Typically, an
         * anonymous inner class will be used.
         *
         * @return the initial value for this thread-local
         */
        @Override
        protected ConcurrentHashMap<String, String> initialValue() {
            return new ConcurrentHashMap<>(16);
        }
    };

    public HummerContextMapHolder() {

    }

    public void clearHolder() {
        try {
            REQUEST_CONTEXT_MAP.get().clear();
            REQUEST_CONTEXT_MAP.remove();
        } catch (Exception e) {
            //ignore
        }
    }

    public String get(String key) {
        return REQUEST_CONTEXT_MAP.get().get(key);
    }

    public void set(String key, String value) {
        REQUEST_CONTEXT_MAP.get().put(key, value);
    }

    public int size() {
        return REQUEST_CONTEXT_MAP.get().size();
    }
}
