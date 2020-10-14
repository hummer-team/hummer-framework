package com.hummer.data.sync.plugin.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

/**
 * OrderSyncData
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 10:39
 */
@Builder
@Getter
public class OrderSyncData<T> {

    private String businessId;

    private T data;

    private OrderStatusChangeData statusChange;

    private String remark;

    private Date createTime;
}
