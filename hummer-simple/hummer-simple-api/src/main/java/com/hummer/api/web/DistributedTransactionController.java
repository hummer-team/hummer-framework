package com.hummer.api.web;


import com.hummer.api.service.OrderService;
import com.hummer.rest.model.ResourceResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/11/1 17:41
 **/
@RestController
@RequestMapping("/v1")
public class DistributedTransactionController {
//    @Autowired
    private OrderService orderDaoMapper;

    @GetMapping(value = "/order/save")
    public ResourceResponse saveOrder() {
        orderDaoMapper.save();
        return ResourceResponse.ok();
    }
}
