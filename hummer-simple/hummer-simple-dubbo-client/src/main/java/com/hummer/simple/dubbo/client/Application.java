package com.hummer.simple.dubbo.client;


import com.hummer.core.starter.HummerApplicationStart;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(scanBasePackages = "com.hummer.simple.dubbo", exclude = {
        DataSourceAutoConfiguration.class
})
@ImportResource("classpath:dubbo.xml")
public class Application {
    public static void main(String[] args) {
        HummerApplicationStart.start(Application.class, args);
    }
}
