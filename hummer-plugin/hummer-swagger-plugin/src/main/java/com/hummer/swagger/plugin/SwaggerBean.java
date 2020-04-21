package com.hummer.swagger.plugin;

import com.hummer.core.PropertiesContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Conditional(SwaggerCondition.class)
@Lazy
public class SwaggerBean {
    @Bean
    @Lazy
    public Docket createRestApi(@Value("${swagger.title}") String title
            , @Value("${swagger.contact}") String contact
            , @Value("${swagger.version}") String version
            , @Value("${swagger.basePackage}") String basePackage) {
        boolean enable = !PropertiesContainer.valueOfString("spring.profiles.active").startsWith("prod");
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enable)
                .apiInfo(apiInfo(title, contact, version))
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(String title, String contact, String version) {
        return new ApiInfoBuilder()
                .title(title)
                .contact(new Contact(contact, "", ""))
                .version(version)
                .build();
    }
}
