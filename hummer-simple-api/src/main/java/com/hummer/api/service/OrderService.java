package com.hummer.api.service;

import com.hummer.api.dao.OrderDaoMapper;
import com.hummer.api.po.OrderPo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/11/1 17:46
 **/
@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderDaoMapper orderDaoMapper;

    //@GlobalTransactional(timeoutMills = 30000, name = "save-order")
    public int save() {
        OrderPo po = new OrderPo();
        po.setOrderTitle("test order");
        po.setUserId(45677);

        int result = orderDaoMapper.save(po);

        log.info("save order done,db response {}", result);
        return po.getOrderId();
    }
}
