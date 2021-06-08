package com.hummer.api.web;

import com.hummer.api.dto.NoProcessOrderInfoRespDto;
import com.hummer.first.restfull.plugin.HummerRestByConfig;
import com.hummer.first.restfull.plugin.HummerSimpleRest;
import com.hummer.rest.model.ResourceResponse;

@HummerSimpleRest(businessName = "订单查询")
public interface OrderApi3 {
    @HummerRestByConfig(apiName = "orderQuery")
    ResourceResponse<NoProcessOrderInfoRespDto> noProcessOrder();
}
