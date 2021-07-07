package com.hummer.message.facade.publish;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.hummer.message.facade.event.ProducerEvent;
import com.hummer.message.facade.metadata.KafkaMessageMetadata;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/2 15:48
 **/
public abstract class BaseMessageBusTemplate {
    /**
     * send one message
     *
     * @param messageBus message body
     * @return void
     * @author liguo
     * @date 2019/8/5 14:26
     * @since 1.0.0
     **/
    public void send(final MessageBus messageBus) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(messageBus.getTopicId())
                , "topic id can't null");
        //get metadata
        KafkaMessageMetadata metadata = KafkaMessageMetadata.getKafkaMessageMetadata(messageBus.getTopicId());
        //if disabled send message
        if (!metadata.isEnable()) {
            return;
        }
        //verify
        verified(messageBus);
        //send message
        if (messageBus.isAsync()) {
            doSendAsync(messageBus);
        } else {
            doSend(messageBus);
        }
    }

    protected void callback(final RecordMetadata metadata
            , final MessageBus messageBus
            , final Exception exception
            , final long startTime) {

        //send event to local messageBus
        ProducerEvent event = new ProducerEvent();
        event.setException(exception);
        event.setMessageBus(messageBus);
        event.setMetadata(metadata);
        event.setStartTime(startTime);
        event.setRetry(messageBus.isRetry());
        EventWrapper.post(event);
        //callback business
        callbackBusiness(metadata, messageBus, exception);
    }

    private void callbackBusiness(RecordMetadata metadata, MessageBus messageBus, Exception exception) {
        if (messageBus.getCallback() != null) {
            if (metadata != null) {
                messageBus.getCallback().callBack(metadata.partition(), metadata.offset(), messageBus.getBody(), exception);
            } else {
                messageBus.getCallback().callBack(-1, -1, messageBus.getBody(), exception);
            }
        }
    }

    /**
     * verified message , if verified failed then throw exception
     *
     * @param messageBus
     * @return void
     * @author liguo
     * @date 2019/9/12 13:51
     * @since 1.0.0
     **/
    protected abstract void verified(final MessageBus messageBus);

    /**
     * send message to message bus server by sync
     *
     * @param messageBus message bus entity
     * @return void
     * @author liguo
     * @date 2019/8/9 16:13
     * @since 1.0.0
     **/
    protected abstract void doSend(final MessageBus messageBus);

    /**
     * send message to message bus server by async
     *
     * @param messageBus message bus entity
     * @return void
     * @author liguo
     * @date 2019/8/9 16:26
     * @since 1.0.0
     **/
    protected abstract void doSendAsync(final MessageBus messageBus);
}
