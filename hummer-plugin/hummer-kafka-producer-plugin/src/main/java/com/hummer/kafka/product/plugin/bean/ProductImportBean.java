package com.hummer.kafka.product.plugin.bean;


import com.hummer.kafka.product.plugin.support.pool.ProducerPool;
import com.hummer.kafka.product.plugin.support.producer.CloseableKafkaProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PreDestroy;


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
    public CloseableKafkaProducer<String, Object> producer() {
        return ProducerPool.SingleProducer.get();
    }

    @PreDestroy
    private void destroy() {
        ProducerPool.ThreadLocalProducer.remove();
        ProducerPool.KeySharedProducer.remove();
    }
}
