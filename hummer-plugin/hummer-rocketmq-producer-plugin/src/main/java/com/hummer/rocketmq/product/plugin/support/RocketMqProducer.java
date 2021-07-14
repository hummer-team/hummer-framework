package com.hummer.rocketmq.product.plugin.support;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;

import javax.validation.constraints.NotNull;

/**
 * @author lee
 */
public class RocketMqProducer implements DisposableBean, InitializingBean, Lifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMqProducer.class);
    private final DefaultMQProducer producer;
    private volatile boolean running;

    public RocketMqProducer(@NotNull DefaultMQProducer producer) {
        this.producer = producer;
        this.conn();
    }

    public RocketMqProducer setProperties(final RocketMqProducerPool.RocketMqMetadata mqMetadata, boolean sync) {
        this.producer.setRetryTimesWhenSendFailed(sync ? 0 : mqMetadata.getSendFailRetryCount());
        this.producer.setRetryTimesWhenSendAsyncFailed(mqMetadata.getSendFailRetryCount());
        this.producer.setSendMsgTimeout(mqMetadata.getSentMsgTimeoutMills());
        this.producer.setDefaultTopicQueueNums(mqMetadata.getDefTopicQueueCount());
        this.producer.setCompressMsgBodyOverHowmuch(mqMetadata.getCompressMsgBodyOverLimit());
        this.producer.setMaxMessageSize(mqMetadata.getMaxMessageSize());
        //set other properties
        return this;
    }

    public void send(Message message, long timeoutMills)
            throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        producer.send(message, timeoutMills);
    }

    public void send(Message message, MessageQueueSelector mq, long timeoutMills)
            throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
        if (mq == null) {
            send(message, timeoutMills);
        } else {
            producer.send(message, mq, timeoutMills);
        }
    }

    public void sendAsync(Message message, MessageQueueSelector mq, SendCallback sendCallback, long timeoutMills)
            throws InterruptedException
            , RemotingException
            , MQClientException {
        if (mq == null) {
            sendAsync(message, sendCallback, timeoutMills);
        } else {
            producer.send(message, mq, message.getKeys(), sendCallback, timeoutMills);
        }
    }

    public void sendAsync(Message message, SendCallback sendCallback, long timeoutMills)
            throws InterruptedException
            , RemotingException
            , MQClientException {
        producer.send(message, sendCallback, timeoutMills);
    }

    public void sendOneway(Message message, MessageQueueSelector mq)
            throws RemotingException, MQClientException, InterruptedException {
        if (mq == null) {
            sendOneway(message);
        } else {
            producer.sendOneway(message, mq, message.getKeys());
        }
    }

    public void sendOneway(Message message)
            throws RemotingException, MQClientException, InterruptedException {
        producer.sendOneway(message);
    }

    private void conn() {
        //conn name server
        try {
            LOGGER.info("begin name server  {}", this.producer.getNamesrvAddr());
            long start = System.currentTimeMillis();
            this.producer.start();
            LOGGER.info("conn name server {} success cost {} mills", this.producer.getNamesrvAddr()
                    , System.currentTimeMillis() - start);
        } catch (MQClientException e) {
            LOGGER.error("rocketMq conn to name server exception so abort this producer instance,cause exception is:", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     *
     * @throws Exception in case of shutdown errors. Exceptions will get logged
     *                   but not rethrown to allow other beans to release their resources as well.
     */
    @Override
    public void destroy() throws Exception {
        stop();
    }

    /**
     * Invoked by the containing {@code BeanFactory} after it has set all bean properties
     * and satisfied {@link BeanFactoryAware}, {@code ApplicationContextAware} etc.
     * <p>This method allows the bean instance to perform validation of its overall
     * configuration and final initialization when all bean properties have been set.
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an
     *                   essential property) or if initialization fails for any other reason
     */
    @Override
    public void afterPropertiesSet() throws Exception {

    }

    /**
     * Start this component.
     * <p>Should not throw an exception if the component is already running.
     * <p>In the case of a container, this will propagate the start signal to all
     * components that apply.
     *
     * @see SmartLifecycle#isAutoStartup()
     */
    @Override
    public void start() {
        running = true;
    }

    /**
     * Stop this component, typically in a synchronous fashion, such that the component is
     * fully stopped upon return of this method. Consider implementing {@link SmartLifecycle}
     * and its {@code stop(Runnable)} variant when asynchronous stop behavior is necessary.
     * <p>Note that this stop notification is not guaranteed to come before destruction:
     * On regular shutdown, {@code Lifecycle} beans will first receive a stop notification
     * before the general destruction callbacks are being propagated; however, on hot
     * refresh during a context's lifetime or on aborted refresh attempts, a given bean's
     * destroy method will be called without any consideration of stop signals upfront.
     * <p>Should not throw an exception if the component is not running (not started yet).
     * <p>In the case of a container, this will propagate the stop signal to all components
     * that apply.
     *
     * @see SmartLifecycle#stop(Runnable)
     * @see DisposableBean#destroy()
     */
    @Override
    public void stop() {
        running = false;
        producer.shutdown();
        LOGGER.info("rocketmq producer stop done");
    }

    /**
     * Check whether this component is currently running.
     * <p>In the case of a container, this will return {@code true} only if <i>all</i>
     * components that apply are currently running.
     *
     * @return whether the component is currently running
     */
    @Override
    public boolean isRunning() {
        return running;
    }
}
