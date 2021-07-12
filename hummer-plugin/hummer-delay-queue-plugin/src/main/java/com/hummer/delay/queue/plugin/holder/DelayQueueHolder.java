package com.hummer.delay.queue.plugin.holder;

import com.hummer.delay.queue.plugin.model.DelayQueueInfo;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.concurrent.DelayQueue;

/**
 * DelayQueueHolder
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/7 17:35
 */
@Component
public class DelayQueueHolder {

    private static final DelayQueue<DelayQueueInfo> QUEUE = new DelayQueue<>();

    public void put(@NotNull DelayQueueInfo data) {
        QUEUE.put(data);
    }

    public DelayQueueInfo poll() {

        return QUEUE.poll();
    }

    public int getSize() {
        return QUEUE.size();
    }
}
