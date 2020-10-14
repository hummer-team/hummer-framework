package com.hummer.data.sync.plugin.model;

import lombok.Data;

/**
 * OrderStatusChangeData
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 10:36
 */
@Data
public class OrderStatusChangeData {

    private Integer targetStatus;

    private Integer originalStatus;

}
