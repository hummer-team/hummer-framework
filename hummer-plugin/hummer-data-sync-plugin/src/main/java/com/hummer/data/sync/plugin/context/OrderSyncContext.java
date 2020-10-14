package com.hummer.data.sync.plugin.context;

import com.hummer.data.sync.plugin.model.OrderSyncMessage;
import lombok.Data;

/**
 * OrderSyncContext
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 10:34
 */
@Data
public class OrderSyncContext<T> {

    private OrderSyncMessage<T> syncMessage;
}
