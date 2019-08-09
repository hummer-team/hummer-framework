package com.hummer.kafka.product.plugin.support;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * this class is wrapper kafka producer
 *
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/8 16:38
 **/
public class CloseableKafkaProducer<K, V> implements DisposableBean, InitializingBean, Lifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(CloseableKafkaProducer.class);
    private volatile boolean running;

    private KafkaProducer<K, V> producer;
    private SendMessageMetadata messageMetadata;

    public CloseableKafkaProducer(final KafkaProducer<K, V> producer
            , final SendMessageMetadata messageMetadata) {
        this.producer = producer;
        this.messageMetadata = messageMetadata;
    }

    /**
     * send message to server for sync
     *
     * @param messageRecord   message
     * @param messageMetadata metadata
     * @return void
     * @author liguo
     * @date 2019/8/8 17:19
     * @since 1.0.0
     **/
    public void send(final ProducerRecord<K, V> messageRecord,final long sendTimeOutMills, final Callback callback) {
        Future<RecordMetadata> future = null;
        try {
            future = producer
                    .send(messageRecord);
            future.get(sendTimeOutMills, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            if (e instanceof InterruptedException || e.getCause() instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                if (future != null) {
                    future.cancel(true);
                }
            }
            callback.onCompletion(null, e);
            LOGGER.error("send message to kafka server exception ", e);
        }
    }

    /**
     * send message to kafka server for async
     *
     * @param messageRecord   message
     * @param messageMetadata metadata
     * @return void
     * @author liguo
     * @date 2019/8/8 17:26
     * @since 1.0.0
     **/
    public void sendAsync(final ProducerRecord<K, V> messageRecord, final Callback callback) {
        producer.send(messageRecord, callback);
    }

    /**
     * send message to kafka server support transaction
     *
     * @param messageRecord   message
     * @param messageMetadata metadata
     * @return void
     * @author liguo
     * @date 2019/8/8 17:41
     * @since 1.0.0
     **/
    public void sendByTransaction(final ProducerRecord<K, V> messageRecord, final Callback callback) {
        producer.initTransactions();
        try {
            producer.beginTransaction();
            producer.send(messageRecord, callback);
            producer.commitTransaction();
        } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
            producer.close();
            callback.onCompletion(null, e);
        } catch (KafkaException e) {
            LOGGER.error("send message to kafka server for transaction error", e);
            producer.abortTransaction();
            callback.onCompletion(null, e);
        }
    }

    /**
     * flush
     *
     * @return void
     * @author liguo
     * @date 2019/8/8 17:30
     * @since 1.0.0
     **/
    public void flush() {
        producer.flush();
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
        producer.flush();
        producer.close(messageMetadata.getCloseProducerTimeOutMillis(),TimeUnit.MILLISECONDS);
        LOGGER.info("closeableKafkaProducer already stop");
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
