package com.hummer.api;

import com.hummer.spring.plugin.context.init.HummerApplicationStart;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "com.hummer.api")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Application2 {

    public static void main(String[] args) {
        HummerApplicationStart.start(Application2.class,args);
    }

}
