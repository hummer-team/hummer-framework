package com.hummer.api.service;

import com.hummer.api.dao.OrderDaoMapper;
import com.hummer.api.po.OrderPo;
import com.hummer.dao.annotation.TargetDataSourceTM;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/11/1 17:46
 **/
//@Service
@Slf4j
public class OrderService {

//    @Autowired
    private OrderDaoMapper orderDaoMapper;

    @GlobalTransactional(timeoutMills = 30000, name = "save-order")
    @TargetDataSourceTM(dbName = "hj_classs_courseware"
        , transactionManager = "hj_classs_courseware_TM", timeout = 5)
    public int save() {
        OrderPo po = new OrderPo();
        po.setOrderTitle("test order");
        po.setUserId(45677);

        int result = orderDaoMapper.save(po);

        log.info("save order done,db response {}", result);
        return po.getOrderId();
    }
}
