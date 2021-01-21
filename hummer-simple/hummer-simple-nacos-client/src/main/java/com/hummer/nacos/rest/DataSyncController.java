package com.hummer.nacos.rest;

import com.hummer.data.sync.plugin.annotation.OrderDataSync;
import com.hummer.nacos.assembler.OrderChangeTestAssembler;
import com.hummer.nacos.service.OrderDataSyncService;
import com.hummer.request.idempotent.plugin.annotation.BusinessIdempotentAnnotation;
import com.hummer.request.idempotent.plugin.annotation.RequestIdempotentAnnotation;
import com.hummer.rest.model.ResourceResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * DataSyncController
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 11:52
 */
@Api(value = "DataSyncController", tags = "DataSyncController")
@RestController
@RequestMapping("/v1/data/sync")
public class DataSyncController {

    @Resource
    private OrderDataSyncService orderDataSyncService;

    @ApiOperation(value = "order-change", notes = "order-change")
    @PostMapping("order/change")
    @OrderDataSync
    @BusinessIdempotentAnnotation
    public ResourceResponse<Void> orderChangeTest(
            @RequestParam("businessCode") String businessCode,
            @RequestParam("originStatus") Integer originStatus,
            @RequestParam("targetStatus") Integer targetStatus
    ) throws SQLIntegrityConstraintViolationException {

        orderDataSyncService.orderStatusUpdate(businessCode, originStatus, targetStatus);
        return ResourceResponse.ok();
    }

    @ApiOperation(value = "order-change", notes = "order-change")
    @PostMapping("order/change/consumer")
    @RequestIdempotentAnnotation(businessCode = "order-change-test"
            , validParamsAssembler = OrderChangeTestAssembler.class)
    public ResourceResponse<Void> orderChangeConsumer(
            @RequestParam("businessCode") String businessCode,
            @RequestParam("businessType") Integer businessType
    ) {
        orderDataSyncService.orderChange();
        return ResourceResponse.ok();
    }
}
