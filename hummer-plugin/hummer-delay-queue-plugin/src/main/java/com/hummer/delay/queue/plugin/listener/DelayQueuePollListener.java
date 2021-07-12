package com.hummer.delay.queue.plugin.listener;

import com.alibaba.fastjson.JSON;
import com.hummer.common.SysConstant;
import com.hummer.common.utils.CommonUtil;
import com.hummer.delay.queue.plugin.consumer.DelayQueuePollConsumer;
import com.hummer.delay.queue.plugin.model.DelayQueueInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * DelayQueuePollListener
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/7 17:55
 */
@Component
@Slf4j
public class DelayQueuePollListener {

    @Autowired
    private Map<String, DelayQueuePollConsumer> handlerMap;

    public void handle(DelayQueueInfo data) {
        MDC.put(SysConstant.REQUEST_ID, data.getRequestId());
        log.debug("delay queue poll listener start");
        DelayQueuePollConsumer handler = null;
        if (MapUtils.isNotEmpty(handlerMap)) {
            String name = CommonUtil.ifEmptyDefault(data.getHandlerName()
                    , data.getData().getClass().getSimpleName() + "Consumer");
            handler = handlerMap.get(name);
        }
        if (handler == null) {
            log.error("matches DelayQueuePollConsumer not fund , data {}", JSON.toJSONString(data));
            return;
        }
        handler.handle(data);
    }
}
