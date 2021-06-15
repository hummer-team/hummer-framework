package com.hummer.core.starter;

import com.hummer.core.PropertiesContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ResourceBanner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;

import java.util.ServiceLoader;

/**
 * wrapper application, biz application need use this method {@link #start}.
 *
 * @Author: lee
 * @version:1.0.0
 * @Date: 2019/6/13 18:34
 **/
public class HummerApplicationStart {
    private static final Logger LOGGER = LoggerFactory.getLogger(HummerApplicationStart.class);

    private HummerApplicationStart() {

    }

    /**
     * start application.
     *
     * @param startClass main class .
     * @param args       input parameter
     * @return void
     * @author liguo
     * @date 2019/6/13 18:41
     * @version 1.0.0
     **/
    public static void start(Class<?> startClass, String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        System.setProperty("sun.zip.encoding", "UTF-8");
        ResourceBanner rb = new ResourceBanner(new ClassPathResource("banner2.txt"));
        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        ServiceLoader.load(ApplicationListener.class)
                .forEach(listener -> builder.application().addListeners(listener));
        LOGGER.debug("register application listener list {}"
                , builder.application().getListeners());
        PropertiesContainer.put("ApplicationBootClass", startClass);
        builder.sources(startClass).banner(rb).run(args);
    }
}
