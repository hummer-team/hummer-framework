package com.hummer.data.sync.plugin.handler;

import com.hummer.data.sync.plugin.context.OrderSyncContext;

/**
 * OrderSyncContextHolder
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 11:04
 */
public class OrderSyncContextHolder {

    private static final ThreadLocal<OrderSyncContext> CONTEXT = new ThreadLocal<OrderSyncContext>() {
        @Override
        protected OrderSyncContext initialValue() {
            return new OrderSyncContext();
        }
    };

    public static OrderSyncContext get() {
        return CONTEXT.get();
    }

    public static void clean() {
        CONTEXT.remove();
    }

}
