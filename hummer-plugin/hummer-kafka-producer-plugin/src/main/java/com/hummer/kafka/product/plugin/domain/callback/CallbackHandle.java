package com.hummer.kafka.product.plugin.domain.callback;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/9 14:33
 **/
@Component
public class CallbackHandle implements Callback {
    private static final Logger LOGGER = LoggerFactory.getLogger(CallbackHandle.class);

    /**
     * A callback method the user can implement to provide asynchronous handling of request completion. This method will
     * be called when the record sent to the server has been acknowledged. Exactly one of the arguments will be
     * non-null.
     *
     * @param metadata  The metadata for the record that was sent (i.e. the partition and offset). Null if an error
     *                  occurred.
     * @param exception The exception thrown during processing of this record. Null if no error occurred.
     *                  Possible thrown exceptions include:
     *                  <p>
     *                  Non-Retriable exceptions (fatal, the message will never be sent):
     *                  <p>
     *                  InvalidTopicException
     *                  OffsetMetadataTooLargeException
     *                  RecordBatchTooLargeException
     *                  RecordTooLargeException
     *                  UnknownServerException
     *                  <p>
     *                  Retriable exceptions (transient, may be covered by increasing #.retries):
     *                  <p>
     *                  CorruptRecordException
     *                  InvalidMetadataException
     *                  NotEnoughReplicasAfterAppendException
     *                  NotEnoughReplicasException
     *                  OffsetOutOfRangeException
     *                  TimeoutException
     */
    @Override
    public void onCompletion(RecordMetadata metadata, Exception exception) {
        LOGGER.info("send message callback handle {}", metadata);
    }
}
