package com.hummer.dao.monitor.stat;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.google.common.base.Strings;
import com.hummer.dao.condition.DruidStatCondition;
import com.hummer.core.PropertiesContainer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * @author bingy
 */
@Configuration
public class DruidConfiguration {

    /**
     * register StatViewServlet bean
     * @return
     */
    @Lazy
    @Bean
    @Conditional(DruidStatCondition.class)
    public ServletRegistrationBean DruidStatViewServlet(){
        //org.springframework.boot.context.embedded.ServletRegistrationBean.
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean =
            new ServletRegistrationBean<>(new StatViewServlet(),"/druid/*");
        //whitelist ip

            servletRegistrationBean.addInitParameter("allow"
                , PropertiesContainer.valueOfString("druid.stat.allow","127.0.0.1"));

        //ip black list ,tips::Sorry, you are not permitted to view this page.
        String denyIps=PropertiesContainer.valueOfString("druid.stat.dny");
        if(!Strings.isNullOrEmpty(denyIps)) {
            servletRegistrationBean.addInitParameter("deny", denyIps);
        }
        //user adn password
        servletRegistrationBean.addInitParameter("loginUsername"
            , PropertiesContainer.valueOfString("druid.stat.user","admin"));
        servletRegistrationBean.addInitParameter("loginPassword"
            ,PropertiesContainer.valueOfString("druid.stat.user.password","123456"));
        //disabled reset data
        servletRegistrationBean.addInitParameter("resetEnable","false");
        return servletRegistrationBean;
    }

    /**
     * register filterRegistrationBean
     * @return
     */
    @Lazy
    @Bean
    @Conditional(DruidStatCondition.class)
    public FilterRegistrationBean druidStatFilter(){
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean =
            new FilterRegistrationBean<>(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        //exclusions resource.
        filterRegistrationBean.addInitParameter("exclusions"
            ,PropertiesContainer.valueOfString("druid.stat.exclusions"
                ,"*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"));
        return filterRegistrationBean;
    }
}


