package com.hummer.nacos.service;

import com.hummer.common.SysConstant;
import com.hummer.common.utils.DateUtil;
import com.hummer.data.sync.plugin.annotation.OrderDataSync;
import com.hummer.data.sync.plugin.enums.OrderSyncEnums;
import com.hummer.data.sync.plugin.handler.OrderSyncContextHolder;
import com.hummer.data.sync.plugin.model.OrderStatusChangeData;
import com.hummer.data.sync.plugin.model.OrderSyncData;
import com.hummer.data.sync.plugin.model.OrderSyncMessage;
import com.hummer.nacos.model.ProductWeightChangeData;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
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
    public void orderStatusUpdate(String businessCode, Integer originStatus, Integer targetStatus) {
        log.debug("order status update params,businessCode=={},originStatus=={},targetStatus=={}",
                businessCode, originStatus, targetStatus);
        OrderSyncContextHolder.get().setSyncMessage(composeOrderSyncMessage(businessCode
                , originStatus, targetStatus));
    }

    private OrderSyncMessage<ProductWeightChangeData> composeOrderSyncMessage(String businessCode
            , Integer originStatus, Integer targetStatus) {
        return OrderSyncMessage.<ProductWeightChangeData>builder()
                .batchId(MDC.get(SysConstant.REQUEST_ID))
                .action(OrderSyncEnums.ActionType.CANCELED.getValue())
                .businessId(businessCode)
                .businessType(OrderSyncEnums.BusinessType.SHOPPING_ORDER.getValue())
                .operatorId("ED1960FE-7DD1-4199-AED8-25F119BD56EF")
                .operatorIp("0.0.0.0")
                .operatorName("some one test")
                .operatorType(OrderSyncEnums.OperatorType.USER.getValue())
                .syncData(OrderSyncData.<ProductWeightChangeData>builder()
                        .businessId(businessCode)
                        .createTime(DateUtil.now())
                        .remark("测试订单操作数据同步")
                        .statusChange(OrderStatusChangeData.builder()
                                .originalStatus(originStatus)
                                .targetStatus(targetStatus).build())
                        .build())
                .build()
                ;
    }
}
