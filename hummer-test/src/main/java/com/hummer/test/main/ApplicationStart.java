package com.hummer.test.main;

import com.hummer.core.starter.HummerApplicationStart;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * application enter
 *
 * @author liguo
 **/
@SpringBootApplication(scanBasePackages = "com.hummer.test", exclude = {DataSourceAutoConfiguration.class})
public class ApplicationStart {

    public static void main(String[] args) {
        HummerApplicationStart.start(ApplicationStart.class, args);
    }

}
