package com.hummer.delay.queue.plugin.consumer;


import com.hummer.delay.queue.plugin.model.DelayQueueInfo;

/**
 * DelayQueuePollConsumer
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/8 13:34
 */
public interface DelayQueuePollConsumer<T> {

    void handle(DelayQueueInfo<T> t);

    void success(DelayQueueInfo<T> t);

    void fail(DelayQueueInfo<T> t);
}
