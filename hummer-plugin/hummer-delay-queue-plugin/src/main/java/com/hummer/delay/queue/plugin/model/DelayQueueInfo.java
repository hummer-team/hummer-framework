package com.hummer.delay.queue.plugin.model;

import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * DelayQueueInfo
 *
 * @author chen wei
 * @version 1.0
 * <p>Copyright: Copyright (c) 2021</p>
 * @date 2021/7/7 17:36
 */
@Data
public class DelayQueueInfo<T> implements Delayed {

    private T data;

    private long expireTime;

    private int retryCount;

    private String requestId;

    private String handlerName;

    @Override
    public long getDelay(TimeUnit unit) {

        return expireTime - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        DelayQueueInfo item = (DelayQueueInfo) o;
        return this.expireTime - item.expireTime >= 0 ? 1 : -1;
    }

}
