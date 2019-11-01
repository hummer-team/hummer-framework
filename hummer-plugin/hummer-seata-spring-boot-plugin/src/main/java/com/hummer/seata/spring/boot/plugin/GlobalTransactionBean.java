package com.hummer.seata.spring.boot.plugin;

import com.hummer.core.PropertiesContainer;
import io.seata.spring.annotation.GlobalTransactionScanner;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * register global transaction scanner
 *
 * @author bingy
 */
@Configuration
@Conditional(value = DistributedTransactionCondition.class)
public class GlobalTransactionBean {
    @Bean
    public GlobalTransactionScanner transactionScanner() {
        return new GlobalTransactionScanner(
                PropertiesContainer.valueOfString("distracted.transaction.application.id")
                , PropertiesContainer.valueOfString("distracted.transaction.service.group")
        );
    }


    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean<XidTransferFilter> filter =
                new FilterRegistrationBean<>();
        filter.setFilter(new XidTransferFilter());
        filter.addUrlPatterns(PropertiesContainer.valueOfString("global.transaction.url.patterns"
                , "*/*"));
        filter.setName("GlobalTransaction-XId-Filter");
        return filter;
    }

    @Bean
    public XidTransferFilter xidTransferFilter() {
        return new XidTransferFilter();
    }
}
