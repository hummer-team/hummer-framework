package com.hummer.api.web;

import com.hummer.api.dto.NoProcessOrderInfoRespDto;
import com.hummer.api.service.AfterHandlerProviderImpl;
import com.hummer.first.restfull.plugin.annotation.HummerRestApiClient;
import com.hummer.first.restfull.plugin.annotation.HummerRestApiDeclare;
import com.hummer.rest.model.ResourceResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@HummerRestApiClient
public interface OrderApi {
    @HummerRestApiDeclare(
        apiPath = "/v1/purchase/order/not-process/order-count"
        , host = "http://kingkong.service.panli.com"
        , timeOutMills = 2000
        , retryCount = 1
        , afterHandler = AfterHandlerProviderImpl.class)
    ResourceResponse<NoProcessOrderInfoRespDto> noProcessOrder(@RequestParam(name = "id") int id
        , @RequestParam(name = "name") String name, @RequestHeader(name = "cookieValue") String h1);

    @HummerRestApiDeclare(
        apiPath = "/v1/purchase/order/not-process/order-count/{id}/{name}"
        , host = "http://kingkong.service.panli.com"
        , timeOutMills = 20
        , httpMethod = "POST"
        , retryCount = 1)
    ResourceResponse<NoProcessOrderInfoRespDto> noProcessOrder2(@PathVariable(name = "id") int id
        , @PathVariable(name = "name") String name, @RequestBody Map<String, Object> body);
}
