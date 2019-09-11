package com.hummer.api;

import com.hummer.core.init.HummerApplicationStart;
import com.hummer.message.consumer.facade.KafkaConsumer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "com.hummer.api")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Application2 {

    public static void main(String[] args) {
        HummerApplicationStart.start(Application2.class, args);
        KafkaConsumer.start();
    }

}
