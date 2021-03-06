package com.hummer.data.sync.plugin.context;

import com.hummer.data.sync.plugin.handler.OrderSyncContextHolder;
import com.hummer.data.sync.plugin.model.OrderSyncMessage;

/**
 * OrderSyncContextCreator
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 17:54
 */
public abstract class AbstractOrderSyncContextCreator<T> {


    public abstract OrderSyncMessage compose(T input);

    public void create(T input) {

        fillContext(compose(input));
    }

    private void fillContext(OrderSyncMessage message) {
        OrderSyncContextHolder.get().setSyncMessage(message);
    }
}
