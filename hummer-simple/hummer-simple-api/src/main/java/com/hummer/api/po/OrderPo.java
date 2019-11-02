package com.hummer.api.po;

import lombok.Data;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/11/1 17:53
 **/
@Data
public class OrderPo {
    private int orderId;
    private String orderTitle;
    private int userId;
}
