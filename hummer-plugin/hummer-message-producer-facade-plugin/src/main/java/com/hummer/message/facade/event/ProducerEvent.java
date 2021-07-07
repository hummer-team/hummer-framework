package com.hummer.message.facade.event;

import com.hummer.message.facade.publish.MessageBus;
import lombok.Data;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * @author edz
 */
@Data
public class ProducerEvent {
    private RecordMetadata metadata;
    private MessageBus messageBus;
    private Exception exception;
    private long startTime;
    private boolean async;
    private boolean retry;
}
