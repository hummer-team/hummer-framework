package com.hummer.user.plugin.configuration;

import com.hummer.user.plugin.filter.RequestContextFilter;
import com.hummer.user.plugin.interceptor.AuthorityInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.MappedInterceptor;

/**
 * @author edz
 */
@Configuration
@Slf4j
public class UserInterceptorConfiguration implements WebMvcConfigurer {
    /**
     * Add Spring MVC lifecycle interceptors for pre- and post-processing of
     * controller method invocations. Interceptors can be registered to apply
     * to all requests or be limited to a subset of URL patterns.
     * <p><strong>Note</strong> that interceptors registered here only apply to
     * controllers and not to resource handler requests. To intercept requests for
     * static resources either declare a
     * {@link MappedInterceptor MappedInterceptor}
     * bean or switch to advanced configuration mode by extending
     * {@link WebMvcConfigurationSupport
     * WebMvcConfigurationSupport} and then override {@code resourceHandlerMapping}.
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorityInterceptor()).addPathPatterns("/**");
        log.debug("hummer AuthorityInterceptor register done,with url /**");
        //WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Bean
    public FilterRegistrationBean<RequestContextFilter> contextHolderBean() {
        FilterRegistrationBean<RequestContextFilter> regBean = new FilterRegistrationBean<>();
        regBean.setFilter(new RequestContextFilter());
        regBean.setName("contextHolderFilter");
        regBean.addUrlPatterns("*");
        regBean.addInitParameter("exclusions", "/warmup*");
        log.debug("hummer RequestContextFilter register done,with url *");
        return regBean;
    }
}
