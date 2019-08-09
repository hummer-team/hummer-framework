package com.hummer.kafka.product.plugin.bean;


import com.hummer.kafka.product.plugin.domain.serializer.MessageBodyJsonSerializer;
import com.hummer.kafka.product.plugin.domain.serializer.MessageBodyThirftSerializer;
import com.hummer.kafka.product.plugin.support.CloseableKafkaProducer;
import com.hummer.kafka.product.plugin.support.SendMessageMetadata;
import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.spring.plugin.context.SpringApplicationContext;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Properties;


/**
 * @Author: lee
 * @since:1.0.0
 * @Date: 2019/8/8 18:29
 **/
@Configuration
@ComponentScan(value = "com.hummer.kafka.product")
@Conditional(value = KafkaCondition.class)
public class ProductImportBean {

    @Bean
    @Lazy
    public CloseableKafkaProducer<String,Object> producer() {

        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
                , PropertiesContainer
                        .valueOfStringWithAssertNotNull(formatKey(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, bodySerializer());
        properties.put(ProducerConfig.ACKS_CONFIG, PropertiesContainer
                .valueOfString(formatKey(ProducerConfig.ACKS_CONFIG), "1"));

        KafkaProducer<String,Object> kafkaProducer = new KafkaProducer<>(properties);
        SendMessageMetadata sendMessageMetadata=SendMessageMetadata
                .builder()
                .sendMessageTimeOutMills(PropertiesContainer.valueOf(formatKey("send.timeout.mills")
                        ,Long.class,3000L))
                .closeProducerTimeOutMillis(PropertiesContainer.valueOf(formatKey("producer.close.timeout.mills")
                        ,Long.class,3000L))
                .build();
        return new CloseableKafkaProducer<>(kafkaProducer,sendMessageMetadata);
    }

    private String formatKey(final String key) {
        final String messageBusKeyPrefix = "hummer.message.";
        return
                String.format("%s%s", messageBusKeyPrefix, key);
    }

    private Class<?> bodySerializer() {
        final String fastJson = "fastJson";

        String bodySerializerType = PropertiesContainer.valueOfString(
                formatKey(String.format("%s.type",ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG)), fastJson);
        if (fastJson.equalsIgnoreCase(bodySerializerType)) {
            return MessageBodyJsonSerializer.class;
        }
        final String thirft = "thirft";
        if (thirft.equalsIgnoreCase(bodySerializerType)) {
            return MessageBodyThirftSerializer.class;
        }

        return MessageBodyJsonSerializer.class;
    }
}
