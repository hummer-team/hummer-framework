package com.hummer.simple.dubbo.server;


import com.hummer.core.starter.HummerApplicationStart;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@ImportResource(locations = "classpath:dubbo-service.xml")
@SpringBootApplication(scanBasePackages = "com.hummer.simple.dubbo",exclude = {DataSourceAutoConfiguration.class})
public class Application {
    public static void main(String[] args){
        HummerApplicationStart.start(Application.class, args);
    }
}
