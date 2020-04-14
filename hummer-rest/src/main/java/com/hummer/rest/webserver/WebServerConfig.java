package com.hummer.rest.webserver;

import com.hummer.core.PropertiesContainer;
import io.undertow.UndertowOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

public class WebServerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebServerConfig.class);

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> embeddedServletContainerFactory() {
        return new WebServerFactoryCustomizer<UndertowServletWebServerFactory>() {
            @Override
            public void customize(UndertowServletWebServerFactory factory) {
                factory.addBuilderCustomizers(builder -> {
                    final long size = 33554432L;
                    builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true);
                    builder.setServerOption(UndertowOptions.MAX_ENTITY_SIZE,
                            PropertiesContainer.valueOf("undertow.max.entity.size", Long.class, size));
                    builder.setServerOption(UndertowOptions.MULTIPART_MAX_ENTITY_SIZE,
                            PropertiesContainer.valueOf("undertow.multipart.max.entity.size"
                                    , Long.class, size));
                });
                LOGGER.info("customize undertow configuration define done");
            }
        };
    }
}
