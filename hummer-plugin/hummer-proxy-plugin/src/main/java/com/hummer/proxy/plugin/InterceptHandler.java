package com.hummer.proxy.plugin;

/**
 * @author lee
 */
public interface InterceptHandler<T> {
    /**
     * execute the proxy method first and then the proxy method
     *
     * @param proxy proxy instance
     * @param args  promoter
     * @return result
     */
    T before(Object proxy, Object[] args);

    /**
     * processing logic after the execution of proxy method
     *
     * @param before before result
     * @param result proxy result
     */
    void after(T before, Object result);

    /**
     * This method is called because of an exception in the proxy method
     *
     * @param before before result
     * @param e      exception
     */
    default void exception(T before, Throwable e) {

    }
}
