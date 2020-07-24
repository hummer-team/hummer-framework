package com.hummer.api;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import com.hummer.core.starter.HummerApplicationStart;
import com.hummer.rest.webserver.UndertowServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "com.hummer.api")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@NacosPropertySources({
    @NacosPropertySource(autoRefreshed = true, dataId = "cloud_conf_data_1", groupId = "example")
})
@UndertowServer
public class Application2 {

    public static void main(String[] args) {
        HummerApplicationStart.start(Application2.class, args);
        //KafkaConsumerWrapper.start(Collections.singleton("log-type-group-out2"), "log-type-group-01"
        //    , SpringApplicationContext.getBean(ConsumerHandle.class));
    }

}
