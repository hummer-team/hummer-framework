package com.hummer.api.service;

import com.hummer.core.SpringApplicationContext;
import com.hummer.kafka.consumer.plugin.callback.ConsumerHandle;
import com.hummer.message.consumer.facade.KafkaConsumerWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Service
public class KafkaService {

    @PostConstruct
    private void init() {
        KafkaConsumerWrapper.start(Collections.singleton("topic001"), "topic001-group-01"
            , SpringApplicationContext.getBean(ConsumerHandle.class));
    }
}
