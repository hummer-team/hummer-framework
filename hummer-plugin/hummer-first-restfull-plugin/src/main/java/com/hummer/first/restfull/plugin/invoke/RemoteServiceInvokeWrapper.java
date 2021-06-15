package com.hummer.first.restfull.plugin.invoke;

import java.lang.reflect.Method;

/**
 * @author lee
 */
public interface RemoteServiceInvokeWrapper {

    /**
     * call service by declare parameter
     *
     * @return
     */
    Object invoke(Object proxy, Method method, Object[] args) throws Exception;
}
