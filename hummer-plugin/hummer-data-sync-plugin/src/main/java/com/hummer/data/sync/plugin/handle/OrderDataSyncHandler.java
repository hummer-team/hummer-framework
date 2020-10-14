package com.hummer.data.sync.plugin.handle;

import com.alibaba.fastjson.JSONObject;
import com.hummer.data.sync.plugin.annotation.OrderDataSync;
import com.hummer.data.sync.plugin.context.OrderSyncContext;
import com.hummer.data.sync.plugin.pipeline.MqMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * description     java类作用描述
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2020</p>
 * @date 2020/10/14 11:24
 */
@Aspect
@Component
@Slf4j
public class OrderDataSyncHandler {

    @Autowired
    private MqMessageProducer mqMessageProducer;

    @After(" @annotation(ds)")
    public void orderDataSync(JoinPoint point, OrderDataSync ds) {
        log.debug("orderDataSync : >>>> {}", point.getSignature());
        OrderSyncContext context = OrderSyncContextHolder.get();
        if (context == null) {
            log.debug("orderDataSync context not exist");
            return;
        }
        Boolean flag = mqMessageProducer.push(context.getSyncMessage());
        log.debug("order sync push mq,context=={}, result=={}", JSONObject.toJSONString(context), flag);
        clean();
    }

    private void clean() {
        OrderSyncContextHolder.clean();
    }
}
