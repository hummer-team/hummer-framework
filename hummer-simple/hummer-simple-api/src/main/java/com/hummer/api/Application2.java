package com.hummer.api;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import com.hummer.core.SpringApplicationContext;
import com.hummer.core.init.HummerApplicationStart;
import com.hummer.kafka.consumer.plugin.callback.ConsumerHandle;
import com.hummer.message.consumer.facade.KafkaConsumerWrapper;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Collections;

@SpringBootApplication(scanBasePackages = "com.hummer.api")
@EnableAspectJAutoProxy(proxyTargetClass = true)
@NacosPropertySources({
    @NacosPropertySource(autoRefreshed = true, dataId = "mytest_01",groupId = "test_01"),
    @NacosPropertySource(autoRefreshed = true, dataId = "data_01"),
})
public class Application2 {

    public static void main(String[] args) {
        HummerApplicationStart.start(Application2.class, args);
        //KafkaConsumerWrapper.start(Collections.singleton("log-type-group-out2"), "log-type-group-01"
        //    , SpringApplicationContext.getBean(ConsumerHandle.class));
    }

}
