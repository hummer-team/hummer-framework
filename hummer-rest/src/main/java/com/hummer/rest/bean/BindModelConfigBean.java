package com.hummer.rest.bean;

import com.hummer.rest.resolver.BindRestParameterSimpleModelResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * register parameter resolver
 *
 * @author liguo.
 * @date 2018/6/20.
 */
@Configuration
public class BindModelConfigBean extends WebMvcConfigurerAdapter {
    /**
     * Add resolvers to support custom controller method argument types.
     * <p>This does not override the built-in support for resolving handler
     * method arguments. To customize the built-in support for argument
     * resolution, configure {@link RequestMappingHandlerAdapter} directly.
     *
     * @param argumentResolvers initially an empty list
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new BindRestParameterSimpleModelResolver(true));
    }
}
