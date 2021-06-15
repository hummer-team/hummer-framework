package com.hummer.api.web;

import com.hummer.api.dto.NoProcessOrderInfoRespDto;
import com.hummer.first.restfull.plugin.annotation.HummerRestApiDeclare;
import com.hummer.first.restfull.plugin.annotation.HummerFirstRest;
import com.hummer.rest.model.ResourceResponse;

@HummerFirstRest
public interface OrderApi2 {
    @HummerRestApiDeclare(apiName = "orderNotProcess",apiPath = "",host = "")
    ResourceResponse<NoProcessOrderInfoRespDto> noProcessOrder();
}
