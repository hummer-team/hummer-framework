package com.hummer.nacos;

import com.hummer.core.starter.HummerApplicationStart;
import com.hummer.rest.webserver.UndertowServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "com.hummer.nacos", exclude = {DataSourceAutoConfiguration.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
@UndertowServer
public class ApplicationNacos {

    public static void main(String[] args) {
        HummerApplicationStart.start(ApplicationNacos.class, args);
        //KafkaConsumerWrapper.start(Collections.singleton("log-type-group-out2"), "log-type-group-01"
        //    , SpringApplicationContext.getBean(ConsumerHandle.class));
    }

}
