package com.hummer.kafka.consumer.plugin.callback;

import com.google.common.collect.ImmutableList;


/**
 * business implement this interface
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/12 17:48
 **/
public interface HandleBusiness {
    /**
     * handle
     *
     * @param messageBodyCollection message
     * @return void
     * @author liguo
     * @date 2019/8/12 18:06
     * @since 1.0.0
     **/
    void handle(final ImmutableList<MessageBodyMetadata> messageBodyCollection);
}
