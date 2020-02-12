package com.hummer.api;

import com.hummer.core.starter.BootStarterBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AnnotationConfigApplicationContext.class)
@Import(value = {BootStarterBean.class, Service1.class, RefService1.class})
@PropertySource(value = {"classpath:application.properties"})
public class HTest {

    @Autowired
    private Service1 service1;

    @Test
    public void demo() {
        for (int i = 0; i < 2; i++) {
            service1.hell();
        }
    }
}
