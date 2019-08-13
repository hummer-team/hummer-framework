package com.hummer.api;

import com.hummer.core.SpringApplicationContext;
import com.hummer.core.init.HummerApplicationStart;
import com.hummer.kafka.consumer.plugin.consumer.ConsumerManager;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "com.hummer.api")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Application2 {

    public static void main(String[] args) {
        HummerApplicationStart.start(Application2.class, args);

        SpringApplicationContext.getBean(ConsumerManager.class).start();
    }

}
