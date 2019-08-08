package com.hummer.message.facade.publish;

import com.hummer.message.facade.publish.bus.KafkaBaseMessageBus;
import com.hummer.message.facade.publish.bus.RabbitMqBaseMessageBus;

import java.io.Serializable;
import java.util.Collection;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/5 16:07
 **/
public class MessageBus {
    public static final BaseMessageBusTemplate KAFKA_PUBLISH = new RabbitMqBaseMessageBus();
    public static final BaseMessageBusTemplate RABBIT_MQ_PUBLISH = new KafkaBaseMessageBus();

    public static  <T extends Serializable> void publishBatch(Collection<T> body, String appId){

    }


    public static  <T extends Serializable> void publish(T body, String appId){

    }
}
