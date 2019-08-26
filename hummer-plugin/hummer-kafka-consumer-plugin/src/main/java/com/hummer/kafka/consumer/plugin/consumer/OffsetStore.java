package com.hummer.kafka.consumer.plugin.consumer;

import javax.validation.constraints.NotNull;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/26 16:49
 **/
public interface OffsetStore {
    /**
     * persistence kafka consumer offset
     *
     * @param topic  topic
     * @param offset offset
     * @return void
     * @author liguo
     * @date 2019/8/26 16:59
     * @since 1.0.0
     **/
    void store(final @NotNull String topic, final long offset,final int offsetKey);

    /**
     * get offset value
     *
     * @param topic
     * @return long
     * @author liguo
     * @date 2019/8/26 17:02
     * @since 1.0.0
     **/
    long getOffset(final @NotNull String topic,final int offsetKey);
}
