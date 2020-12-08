package com.hummer.nacos.service;

import com.alibaba.fastjson.JSONObject;
import com.hummer.dao.annotation.TargetDataSourceTM;
import com.hummer.data.sync.plugin.annotation.OrderDataSync;
import com.hummer.data.sync.plugin.handler.OrderSyncContextHolder;
import com.hummer.data.sync.plugin.model.OrderSyncMessage;
import com.hummer.nacos.model.ProductWeightChangeData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * OrderDataSyncServiceImpl
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 11:58
 */
@Service
@Slf4j
public class OrderDataSyncServiceImpl implements OrderDataSyncService {

    @Override
    @OrderDataSync
    @TargetDataSourceTM(dbName = "order_w"
            , transactionManager = "order_w_TM"
            , rollbackFor = Exception.class
            , timeout = 30)
    public void orderStatusUpdate(String businessCode, Integer originStatus, Integer targetStatus) {
        log.debug("order status update params,businessCode=={},originStatus=={},targetStatus=={}",
                businessCode, originStatus, targetStatus);
        OrderSyncContextHolder.get().setSyncMessage(composeOrderSyncMessage(businessCode
                , originStatus, targetStatus));
    }

    private OrderSyncMessage<ProductWeightChangeData> composeOrderSyncMessage(String businessCode
            , Integer originStatus, Integer targetStatus) {

        return new OrderSyncMessage<>();
    }

    @Override
    @OrderDataSync
    public void orderChange() {
        log.debug("data sync context == {}", JSONObject.toJSONString(OrderSyncContextHolder.get()));
    }
}
