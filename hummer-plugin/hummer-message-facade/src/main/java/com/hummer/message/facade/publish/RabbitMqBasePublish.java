package com.hummer.message.facade.publish;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:37
 **/
public class RabbitMqBasePublish extends BasePublishTemplate {
    /**
     * send batch message
     *
     * @param body  message body
     * @param appId business unique id
     * @return void
     * @author liguo
     * @date 2019/8/5 14:26
     * @since 1.0.0
     **/
    @Override
    public <T extends Serializable> void innerSendBatch(Collection<T> body, String appId) {

    }

    /**
     * send one message
     *
     * @param body  message body
     * @param appId business unique id
     * @return void
     * @author liguo
     * @date 2019/8/5 14:26
     * @since 1.0.0
     **/
    @Override
    public <T extends Serializable> void innerSend(T body, String appId) {

    }
}
