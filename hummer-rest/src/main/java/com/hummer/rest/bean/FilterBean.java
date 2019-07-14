package com.hummer.rest.bean;

import com.hummer.rest.filter.RequestFilter;
import com.hummer.spring.plugin.context.PropertiesContainer;
import com.hummer.common.SysConstant;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class FilterBean {
    @Bean
    public FilterRegistrationBean registration(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new RequestFilter());
        filterRegistrationBean.addUrlPatterns(PropertiesContainer.valueOfString(SysConstant.RestConstant.REST_REQUESTILTER_IINCLUDE_URL
                , SysConstant.RestConstant.INCLUDE_URL_PATTEER));
        filterRegistrationBean.addInitParameter("exclusions"
                , PropertiesContainer.valueOfString(SysConstant.RestConstant.REST_REQUESTILTER_EXCLUSIONS_URL
                        , SysConstant.RestConstant.EXCLUSIONS_URL_PATTEER));
        filterRegistrationBean.setName("hummer-rest-filter");
        return filterRegistrationBean;
    }
}
