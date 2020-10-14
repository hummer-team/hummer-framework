package com.hummer.data.sync.plugin.model;

import lombok.Builder;
import lombok.Getter;

/**
 * OrderSyncMessage
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 10:41
 */
@Builder
@Getter
public class OrderSyncMessage<T> {

    private String batchId;

    private String businessId;

    private String businessType;

    private String action;

    private OrderSyncData<T> syncData;

    private String operatorId;

    private Integer operatorType;

    private String operatorName;

    private String operatorIp;
}
