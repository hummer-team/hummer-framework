package com.hummer.data.sync.plugin.model;

import lombok.Builder;
import lombok.Getter;

/**
 * OrderStatusChangeData
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 10:36
 */
@Builder
@Getter
public class OrderStatusChangeData {

    private Integer targetStatus;

    private Integer originalStatus;

}
