package com.hummer.redis.plugin.test.main;

import com.hummer.core.starter.HummerApplicationStart;
import com.hummer.rest.webserver.UndertowServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * application enter
 *
 * @author liguo
 **/
@SpringBootApplication(scanBasePackages = "com.hummer.redis.plugin.test")
@UndertowServer
public class ApplicationStart {

    public static void main(String[] args) {
        HummerApplicationStart.start(ApplicationStart.class, args);
    }

}
