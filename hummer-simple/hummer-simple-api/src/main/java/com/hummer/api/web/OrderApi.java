package com.hummer.api.web;

import com.hummer.api.dto.NoProcessOrderInfoRespDto;
import com.hummer.first.restfull.plugin.HummerRestByDeclare;
import com.hummer.first.restfull.plugin.HummerSimpleRest;
import com.hummer.rest.model.ResourceResponse;

@HummerSimpleRest(businessName = "订单查询")
public interface OrderApi {
    @HummerRestByDeclare(apiPath = "v1/purchase/order/not-process/order-count/{id}/{name}"
        , host = "kingkong.service.panli.com"
        , timeOutMills = 20
        , retryCount = 1)
    ResourceResponse<NoProcessOrderInfoRespDto> noProcessOrder(int id, String name);
}
