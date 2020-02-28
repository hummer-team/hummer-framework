package com.hummer.api;

import com.hummer.core.SpringApplicationContext;
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
@Import(value = {BootStarterBean.class
    , Service1.class
    , RefService1.class
    , MessageDigestBean.class
    , TestEventListener.class})
@PropertySource(value = {"classpath:application.properties"})
public class CommonTest {

    @Autowired
    private Service1 service1;
    @Autowired
    private MessageDigestBean messageDigestBean;

    @Test
    public void demo() {
        for (int i = 0; i < 2; i++) {
            service1.hell();
        }
    }

    @Test
    public void md5() throws Exception {
        byte[] by = messageDigestBean.digestMd5().getObject().digest("abc".getBytes());
        StringBuilder builder = new StringBuilder();
        for (byte b : by) {
            builder.append((char) b);
        }
        System.out.println(builder.toString());
    }

    @Test
    public void event() {
        SpringApplicationContext.publishEvent(new TestEvent("test"));
    }

    @Test
    public void mod() {
        System.out.println(3 % 1);
        System.out.println(3 % 9);
    }
}
