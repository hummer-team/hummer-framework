package com.hummer.message.facade.publish;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.hummer.message.facade.metadata.KafkaMessageMetadata;
import com.hummer.message.facade.metadata.RabbitMqMessageMetadata;

import java.io.Serializable;
import java.util.Collection;
import java.util.Properties;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/2 15:48
 **/
public abstract class BaseMessageBusTemplate {
    /**
     * send batch message
     *
     * @param body       message body
     * @param appId      business unique id
     * @param properties configuration
     * @return void
     * @author liguo
     * @date 2019/8/5 14:26
     * @since 1.0.0
     **/
    public <T extends Serializable> void publishBatch(Collection<T> body, String appId, Properties properties) {
        KafkaMessageMetadata metadata = kafkaMessageMetadata(appId);
        if (!metadata.isEnable()) {
            return;
        }
        Preconditions.checkArgument(!Strings.isNullOrEmpty(appId), "app id can't null");
        doSendBatch(body, appId);
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
    public <T extends Serializable> void publish(T body, String appId, Properties properties) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(appId), "app id can't null");
        doSend(body, appId);
    }

    protected KafkaMessageMetadata kafkaMessageMetadata(final String appId) {
        return KafkaMessageMetadata.getKafkaMessageMetadata(appId);
    }

    protected RabbitMqMessageMetadata rabbitMqMessageMetadata(String appId) {
        return new RabbitMqMessageMetadata(appId);
    }

    protected abstract <T extends Serializable> void doSendBatch(Collection<T> body, String appId);

    protected abstract <T extends Serializable> void doSend(T body, String appId);
}
