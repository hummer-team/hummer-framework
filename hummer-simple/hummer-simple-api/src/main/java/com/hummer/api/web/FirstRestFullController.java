package com.hummer.api.web;

import com.hummer.api.dto.NoProcessOrderInfoRespDto;
import com.hummer.rest.model.ResourceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;

@RestController
@RequestMapping("/v1")
public class FirstRestFullController {
    @Autowired
    private OrderApi orderApi;

    @PostMapping("/order")
    public ResourceResponse<NoProcessOrderInfoRespDto> queryOrder() {
        //style 1:
        //return ((OrderApi)PropertiesContainer.valueOfInstanceDirect(OrderApi.class)).noProcessOrder(123, "test");
        //style 2:
        return orderApi.noProcessOrder(123, "test",URLEncoder.encode("Qkv+iGy0m+zBAGjE6yYao5dn57dEDNshRKG5xM4sJwpS8h+/BOME0UqQHeBgk6oggp6JVp9+lmTMj/3YW9EtBUKp4q4OIgWSGPjaIXEHOCUBWi11/LHd8TwRFs2Rzgwj5DbT+2HdV/cOktv7znRkxFA/0E95QxA9bMzcUZ87Oe0=?t=a2e018007ff140f1bdadc5fe51f96e41"));
    }
}
