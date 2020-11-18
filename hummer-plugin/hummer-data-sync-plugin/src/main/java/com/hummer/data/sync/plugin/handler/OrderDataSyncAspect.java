package com.hummer.data.sync.plugin.handler;

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
public class OrderDataSyncAspect {

    @Autowired
    private MqMessageProducer mqMessageProducer;

    @After(" @annotation(syncData)")
    public void orderDataSync(JoinPoint point, OrderDataSync syncData) {
        log.debug("orderDataSync : >>>> {}", point.getSignature());
        OrderSyncContext context = OrderSyncContextHolder.get();
        if (context == null || context.getSyncMessage() == null) {
            log.debug("orderDataSync context not exist");
            return;
        }
        mqMessageProducer.asyncPush(context.getSyncMessage(), this::clean);
    }

    public boolean clean(Object o) {
        log.info("data sync msg req produce result=={}", o);
        OrderSyncContextHolder.clean();
        return true;
    }
}
