package com.hummer.delay.queue.plugin.holder;

import com.hummer.core.SpringApplicationContext;
import com.hummer.delay.queue.plugin.listener.DelayQueuePollListener;
import com.hummer.delay.queue.plugin.model.DelayQueueInfo;
import com.hummer.pipeline.plugin.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * QueuePollHolder
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/8 13:19
 */
@Component
@Slf4j
public class QueuePollHolder {

    @Autowired
    private DelayQueueHolder shopTradeFailRetryDelayQueue;

    @Autowired
    private DelayQueuePollListener delayQueuePollListener;

    public QueuePollHolder() {
        schedulePoll();
    }

    private void schedulePoll() {
        if (!DelayQueuePropertiesHolder.getDelayQueueSchedulePollEnable()) {
            return;
        }
        ScheduledExecutorService executor
                = SpringApplicationContext.getBean(Constants.SCHEDULE_POOL_DEFAULT_TASK_GROUP
                , ScheduledExecutorService.class);
        executor.scheduleAtFixedRate(() -> {
                    DelayQueueInfo data = null;
                    while ((data = shopTradeFailRetryDelayQueue.poll()) != null) {
                        delayQueuePollListener.handle(data);
                    }

                }, DelayQueuePropertiesHolder.getDelayQueuePollInitialDelayMillions()
                , DelayQueuePropertiesHolder.getDelayQueuePollPeriodMillions(), TimeUnit.MILLISECONDS);
    }

    public void shutDown() {
        ScheduledExecutorService executor
                = SpringApplicationContext.getBean(Constants.SCHEDULE_POOL_DEFAULT_TASK_GROUP
                , ScheduledExecutorService.class);
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
