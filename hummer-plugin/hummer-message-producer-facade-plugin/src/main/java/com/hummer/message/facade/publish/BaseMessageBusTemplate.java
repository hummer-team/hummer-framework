package com.hummer.message.facade.publish;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.hummer.message.facade.event.ProducerEvent;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.rocketmq.client.producer.SendResult;

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
        Preconditions.checkArgument(!Strings.isNullOrEmpty(messageBus.getTopicId()), "topic id can't null");
        if (!enable(messageBus.getTopicId())) {
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

    protected void callbackOfRocketMq(final SendResult metadata
            , final MessageBus messageBus
            , final Throwable exception
            , final long startTime) {
        //send event to local messageBus
        ProducerEvent event = new ProducerEvent();
        event.setException(exception);
        event.setMessageBus(messageBus);
        if (metadata != null) {
            event.setOffset(metadata.getQueueOffset());
            event.setPartition(metadata.getMessageQueue().getQueueId());
        }
        event.setStartTime(startTime);
        event.setRetry(messageBus.isRetry());
        event.setBusDriverType(MessageBus.ROCKETMQ_BOCKER);
        event.setAsync(messageBus.isAsync());
        if (messageBus.getRocketMq() != null) {
            event.setTag(messageBus.getRocketMq().getTag());
            event.setAck(messageBus.getRocketMq().isAck());
            event.setDelayLevel(messageBus.getRocketMq().getDelayLevel());
        }

        EventWrapper.post(event);

        callbackBusiness(metadata == null ? -1 : metadata.getMessageQueue().getQueueId()
                , metadata == null ? -1 : metadata.getQueueOffset()
                , messageBus, exception);
    }

    protected void callbackOfKafka(final RecordMetadata metadata
            , final MessageBus messageBus
            , final Exception exception
            , final long startTime) {

        //send event to local messageBus
        ProducerEvent event = new ProducerEvent();
        event.setException(exception);
        event.setMessageBus(messageBus);
        if (metadata != null) {
            event.setOffset(metadata.offset());
            event.setPartition(metadata.partition());
        }
        event.setStartTime(startTime);
        event.setRetry(messageBus.isRetry());
        event.setPartition(messageBus.getKafka().getPartition());
        event.setBusDriverType(MessageBus.KAFKA_BOCKER);
        EventWrapper.post(event);
        //callback business
        callbackBusiness(metadata == null ? -1 : metadata.partition()
                , metadata == null ? -1 : metadata.offset()
                , messageBus, exception);
    }

    private void callbackBusiness(int partition, long offset, MessageBus messageBus, Throwable exception) {
        if (messageBus.getCallback() != null) {
            messageBus.getCallback().callBack(partition, offset, messageBus.getBody(), exception);
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

    /**
     * if disabled then don't send message
     *
     * @param topicId topic id
     * @return if disabled then don't send message
     */
    protected abstract boolean enable(String topicId);
}
