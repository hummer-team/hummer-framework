package com.hummer.message.facade.publish.bus;

import com.hummer.message.facade.publish.BaseMessageBusTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 15:37
 **/
@Service(value = "RabbitMqBaseMessageBus")
public class RabbitMqBaseMessageBus extends BaseMessageBusTemplate {
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
    public <T extends Serializable> void doSendBatch(Collection<T> body, String appId) {

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
    public <T extends Serializable> void doSend(T body, String appId) {

    }
}
