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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    public void orderDataSync(JoinPoint point, OrderDataSync syncData) throws Throwable {
        log.debug("orderDataSync : >>>> {}", point.getSignature());
        OrderSyncContext context = OrderSyncContextHolder.get();
        if (context == null) {
            log.debug("orderDataSync context not exist");
            return;
        }
        List<String> list = new ArrayList<>();
        list.stream().map(this::clean).collect(Collectors.toList());
        mqMessageProducer.asyncPush(context.getSyncMessage(), this::clean);
    }

    public boolean clean(Object o) {

        OrderSyncContextHolder.clean();
        return true;
    }
}
