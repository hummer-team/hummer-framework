package com.hummer.register.eureka;

import com.hummer.core.starter.HummerApplicationStart;
import com.hummer.rest.webserver.UndertowServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author edz
 */
@SpringBootApplication(scanBasePackages = "com.hummer.register")
@EnableEurekaServer
@UndertowServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        HummerApplicationStart.start(EurekaServerApplication.class, args);
    }
}
