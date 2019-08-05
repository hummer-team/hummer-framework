package com.hummer.message.facade.publish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:37
 **/
public class KafkaBasePublish extends BasePublish {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaBasePublish.class);

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
    protected <T extends Serializable> void innerSendBatch(Collection<T> body, String appId) {

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
    protected <T extends Serializable> void innerSend(T body, String appId) {
        LOGGER.warn("{}", body);
    }
}
