package com.hummer.message.facade.event;

import com.hummer.message.facade.publish.MessageBus;
import lombok.Data;

import java.util.Map;

/**
 * @author edz
 */
@Data
public class ProducerEvent {
    private MessageBus messageBus;
    private Throwable exception;
    private long startTime;
    private boolean async;
    private boolean retry;
    private String busDriverType;
    private long offset;
    private Integer partition;
    private Map<String,Object> affiliatedData;
    private String tag;
    private boolean ack;
    private int delayLevel;
}
