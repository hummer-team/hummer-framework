package com.hummer.api.web;

import com.hummer.api.dto.NoProcessOrderInfoRespDto;
import com.hummer.core.SpringApplicationContext;
import com.hummer.rest.model.ResourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
public class FirstRestFullController {
    @Autowired(required = false)
    private OrderApi orderApi;

    @PostMapping("/order")
    public ResourceResponse<NoProcessOrderInfoRespDto> queryOrder() {
        OrderApi orderApi = (OrderApi) SpringApplicationContext.getBean(OrderApi.class);
        return orderApi.noProcessOrder(123, "test");
    }
}
