package com.hummer.doorgod.api.main;


import com.hummer.core.starter.HummerApplicationStart;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
* application enter
* @author liguo
**/
@SpringBootApplication(scanBasePackages = "com.hummer.doorgod")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ApplicationStart {

    public static void main(String[] args) {
        HummerApplicationStart.start(ApplicationStart.class,args);
    }

}
